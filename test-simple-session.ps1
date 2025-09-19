# Test simple de session
Write-Host "=== SIMPLE SESSION TEST ===`n" -ForegroundColor Magenta

# 1. Login
Write-Host "1. Login..." -ForegroundColor Yellow
$loginBody = '{"login":"admin","password":"admin","rememberMe":false}'

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -SessionVariable session
    Write-Host "Login Status: $($response.StatusCode)" -ForegroundColor Green

    # Vérifier si on a des cookies
    Write-Host "Session Cookies Count: $($session.Cookies.Count)" -ForegroundColor Cyan

    if ($session.Cookies.Count -gt 0) {
        Write-Host "✅ Session cookies présents" -ForegroundColor Green

        # 2. Test /me avec session
        Write-Host "`n2. Test /api/auth/me..." -ForegroundColor Yellow
        try {
            $meResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Method GET -WebSession $session
            Write-Host "✅ /api/auth/me SUCCESS" -ForegroundColor Green
            Write-Host "User: $($meResponse.login)" -ForegroundColor Cyan
        } catch {
            Write-Host "❌ /api/auth/me FAILED: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        }
    } else {
        Write-Host "❌ Pas de cookies de session" -ForegroundColor Red
    }

} catch {
    Write-Host "❌ Login failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== TEST COMPLETE ===`n" -ForegroundColor Magenta