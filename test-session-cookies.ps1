# Test Session Cookies avec OpenAPI
Write-Host "Testing OpenAPI Integration with Session Cookies..." -ForegroundColor Green

# Test 1: Login et récupération des cookies
Write-Host "`n1. Testing Login with Cookie Capture..." -ForegroundColor Yellow

try {
    $loginBody = @{
        login = "admin"
        password = "admin"
        rememberMe = $false
    } | ConvertTo-Json

    # Capture des headers de réponse complets
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -SessionVariable session

    Write-Host "✅ Login Request Status: $($response.StatusCode)" -ForegroundColor Green

    # Vérifier les cookies dans la session
    if ($session.Cookies.Count -gt 0) {
        Write-Host "🍪 Session Cookies Found:" -ForegroundColor Cyan
        foreach ($cookie in $session.Cookies) {
            if ($null -ne $cookie.Value) {
                Write-Host "  - Name: $($cookie.Name), Value: $($cookie.Value.Substring(0, [Math]::Min(20, $cookie.Value.Length)))..." -ForegroundColor Cyan
            } else {
                Write-Host "  - Name: $($cookie.Name), Value: <null>" -ForegroundColor Cyan
            }
            Write-Host "    Domain: $($cookie.Domain), Path: $($cookie.Path), HttpOnly: $($cookie.HttpOnly), Secure: $($cookie.Secure)" -ForegroundColor Gray
        }
    } else {
        Write-Host "❌ No cookies found in session!" -ForegroundColor Red
    }

    # Vérifier les headers Set-Cookie
    $setCookieHeaders = $response.Headers["Set-Cookie"]
    if ($setCookieHeaders) {
        Write-Host "🍪 Set-Cookie Headers:" -ForegroundColor Cyan
        foreach ($header in $setCookieHeaders) {
            Write-Host "  - $header" -ForegroundColor Cyan
        }
    } else {
        Write-Host "❌ No Set-Cookie headers found!" -ForegroundColor Red
    }

    # Parser le contenu de la réponse
    $loginResult = $response.Content | ConvertFrom-Json
    Write-Host "User Info - ID: $($loginResult.id), Login: $($loginResult.login), Role: $($loginResult.role)" -ForegroundColor Cyan

} catch {
    Write-Host "❌ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        Write-Host "Response: $($_.Exception.Response | ConvertTo-Json)" -ForegroundColor Red
    }
}

# Test 2: Utiliser la session pour accéder à /me
if ($session -and $session.Cookies.Count -gt 0) {
    Write-Host "`n2. Testing /me Endpoint with Session Cookies..." -ForegroundColor Yellow
    try {
        $meResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/me" -Method GET -WebSession $session
        Write-Host "✅ /me endpoint successful" -ForegroundColor Green
        $meResult = $meResponse.Content | ConvertFrom-Json
        Write-Host "Current User: $($meResult.login) (Role: $($meResult.role))" -ForegroundColor Cyan
    } catch {
        Write-Host "❌ /me endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "`n2. Skipping /me test - no session cookies available" -ForegroundColor Yellow
}

# Test 3: Test logout avec session
if ($session -and $session.Cookies.Count -gt 0) {
    Write-Host "`n3. Testing Logout with Session..." -ForegroundColor Yellow
    try {
        $logoutResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/logout" -Method POST -WebSession $session
        Write-Host "✅ Logout successful" -ForegroundColor Green
        $logoutResult = $logoutResponse.Content | ConvertFrom-Json
        Write-Host "Logout Message: $($logoutResult.message)" -ForegroundColor Cyan
    } catch {
        Write-Host "❌ Logout failed: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "`n3. Skipping logout test - no session cookies available" -ForegroundColor Yellow
}

Write-Host "`nSession Cookie Testing completed!" -ForegroundColor Green