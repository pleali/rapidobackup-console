# Test session cookies avec gestion correcte des sessions
Write-Host "=== TEST SESSION AUTHENTICATION ===`n" -ForegroundColor Magenta

try {
    # 1. Login avec session
    Write-Host "1. Login et capture de session..." -ForegroundColor Yellow

    $loginBody = @{
        login = "admin"
        password = "admin"
        rememberMe = $false
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -SessionVariable webSession

    Write-Host "✅ Login successful" -ForegroundColor Green
    Write-Host "User: $($loginResponse.login) (ID: $($loginResponse.id))" -ForegroundColor Cyan

    # 2. Vérifier les cookies de session
    Write-Host "`n2. Vérification des cookies de session..." -ForegroundColor Yellow
    if ($webSession.Cookies.Count -gt 0) {
        Write-Host "✅ Session cookies trouvés:" -ForegroundColor Green
        foreach ($cookie in $webSession.Cookies) {
            Write-Host "  $($cookie.Name) = $($cookie.Value.Substring(0, [Math]::Min(20, $cookie.Value.Length)))..." -ForegroundColor Cyan
        }
    } else {
        Write-Host "❌ Aucun cookie de session trouvé" -ForegroundColor Red
        return
    }

    # 3. Test endpoint authentifié avec session
    Write-Host "`n3. Test endpoint /api/auth/me avec session..." -ForegroundColor Yellow

    try {
        $meResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Method GET -WebSession $webSession
        Write-Host "✅ /api/auth/me SUCCESS" -ForegroundColor Green
        Write-Host "Current User: $($meResponse.login) - Role: $($meResponse.role)" -ForegroundColor Cyan
    } catch {
        Write-Host "❌ /api/auth/me FAILED: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        }
    }

    # 4. Test logout
    Write-Host "`n4. Test logout..." -ForegroundColor Yellow
    try {
        $logoutResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/logout" -Method POST -WebSession $webSession
        Write-Host "✅ Logout successful: $($logoutResponse.message)" -ForegroundColor Green
    } catch {
        Write-Host "❌ Logout failed: $($_.Exception.Message)" -ForegroundColor Red
    }

    # 5. Test endpoint après logout (doit échouer)
    Write-Host "`n5. Test /api/auth/me après logout (doit échouer)..." -ForegroundColor Yellow
    try {
        $meAfterLogout = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Method GET -WebSession $webSession
        Write-Host "❌ PROBLÈME: /api/auth/me fonctionne encore après logout!" -ForegroundColor Red
    } catch {
        Write-Host "✅ Correct: /api/auth/me échoue après logout" -ForegroundColor Green
    }

} catch {
    Write-Host "❌ Test failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

Write-Host "`n=== TEST COMPLETE ===`n" -ForegroundColor Magenta