# Debug détaillé des cookies de session
Write-Host "=== DEBUG SESSION COOKIES OpenAPI ===" -ForegroundColor Magenta

# Test avec capture maximale d'informations
Write-Host "`n1. Test Login avec capture maximale..." -ForegroundColor Yellow

try {
    $loginBody = @{
        login = "admin"
        password = "admin"
        rememberMe = $false
    } | ConvertTo-Json

    Write-Host "Request Body: $loginBody" -ForegroundColor Gray

    # Utiliser Invoke-WebRequest au lieu d'Invoke-RestMethod pour plus d'infos
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -SessionVariable loginSession -Verbose

    Write-Host "`n--- LOGIN RESPONSE DETAILS ---" -ForegroundColor Cyan
    Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Status Description: $($response.StatusDescription)" -ForegroundColor Gray

    # Headers de réponse
    Write-Host "`n--- RESPONSE HEADERS ---" -ForegroundColor Cyan
    foreach ($header in $response.Headers.GetEnumerator()) {
        Write-Host "$($header.Key): $($header.Value)" -ForegroundColor Gray
    }

    # Cookies spécifiquement
    Write-Host "`n--- COOKIES FROM RESPONSE ---" -ForegroundColor Cyan
    if ($response.Headers.ContainsKey("Set-Cookie")) {
        foreach ($cookie in $response.Headers["Set-Cookie"]) {
            Write-Host "Set-Cookie: $cookie" -ForegroundColor Yellow
        }
    } else {
        Write-Host "❌ NO Set-Cookie headers found!" -ForegroundColor Red
    }

    # Session cookies
    Write-Host "`n--- SESSION COOKIES ---" -ForegroundColor Cyan
    if ($loginSession -and $loginSession.Cookies) {
        Write-Host "Session Cookies Count: $($loginSession.Cookies.Count)" -ForegroundColor Green
        foreach ($cookie in $loginSession.Cookies) {
            Write-Host "Cookie: $($cookie.Name) = $($cookie.Value)" -ForegroundColor Yellow
            Write-Host "  Domain: $($cookie.Domain), Path: $($cookie.Path)" -ForegroundColor Gray
            Write-Host "  HttpOnly: $($cookie.HttpOnly), Secure: $($cookie.Secure)" -ForegroundColor Gray
        }
    } else {
        Write-Host "❌ NO session cookies found!" -ForegroundColor Red
    }

    # Corps de réponse
    Write-Host "`n--- RESPONSE BODY ---" -ForegroundColor Cyan
    $loginResult = $response.Content | ConvertFrom-Json
    Write-Host "User ID: $($loginResult.id)" -ForegroundColor Green
    Write-Host "User Login: $($loginResult.login)" -ForegroundColor Green
    Write-Host "User Role: $($loginResult.role)" -ForegroundColor Green

    # Test 2: Utiliser la session pour /me
    if ($loginSession -and $loginSession.Cookies -and $loginSession.Cookies.Count -gt 0) {
        Write-Host "`n2. Test /me avec session cookies..." -ForegroundColor Yellow

        try {
            $meResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/me" -Method GET -WebSession $loginSession -Verbose
            Write-Host "✅ /me Success: $($meResponse.StatusCode)" -ForegroundColor Green
            $meResult = $meResponse.Content | ConvertFrom-Json
            Write-Host "Current User: $($meResult.login)" -ForegroundColor Cyan
        } catch {
            Write-Host "❌ /me failed: $($_.Exception.Message)" -ForegroundColor Red
            if ($_.Exception.Response) {
                Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
            }
        }
    } else {
        Write-Host "`n2. Skipping /me test - no session cookies" -ForegroundColor Yellow
    }

    # Test 3: Nouveau login pour comparer
    Write-Host "`n3. Test nouveau login pour comparer..." -ForegroundColor Yellow
    try {
        $secondResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -SessionVariable secondSession

        Write-Host "Second Login Status: $($secondResponse.StatusCode)" -ForegroundColor Green
        Write-Host "Second Session Cookies Count: $($secondSession.Cookies.Count)" -ForegroundColor Cyan

        if ($secondSession.Cookies.Count -gt 0) {
            foreach ($cookie in $secondSession.Cookies) {
                if ($null -ne $cookie.Value) {
                    Write-Host "Second Cookie: $($cookie.Name) = $($cookie.Value.Substring(0, [Math]::Min(20, $cookie.Value.Length)))..." -ForegroundColor Yellow
                } else {
                    Write-Host "Second Cookie: $($cookie.Name) = <null>" -ForegroundColor Yellow
                }
            }
        }
    } catch {
        Write-Host "❌ Second login failed: $($_.Exception.Message)" -ForegroundColor Red
    }

} catch {
    Write-Host "❌ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Exception Type: $($_.Exception.GetType().Name)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Response Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        Write-Host "Response Description: $($_.Exception.Response.StatusDescription)" -ForegroundColor Red
    }
}

Write-Host "`n=== DEBUG COMPLETE ===" -ForegroundColor Magenta