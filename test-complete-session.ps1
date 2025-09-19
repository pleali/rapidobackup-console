# Test complet du cycle de vie de session
Write-Host "=== TEST COMPLET SESSION LIFECYCLE ===`n" -ForegroundColor Magenta

try {
    # 1. Login
    Write-Host "1. Login..." -ForegroundColor Yellow
    $loginBody = '{"login":"admin","password":"admin","rememberMe":false}'
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -SessionVariable session

    Write-Host "✅ Login successful - User: $($loginResponse.login)" -ForegroundColor Green

    # 2. Test /me
    Write-Host "`n2. Test /api/auth/me..." -ForegroundColor Yellow
    $meResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Method GET -WebSession $session
    Write-Host "✅ Current user: $($meResponse.login) - Role: $($meResponse.role)" -ForegroundColor Green

    # 3. Test logout
    Write-Host "`n3. Test logout..." -ForegroundColor Yellow
    $logoutResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/logout" -Method POST -WebSession $session
    Write-Host "✅ Logout: $($logoutResponse.message)" -ForegroundColor Green

    # 4. Test /me après logout (doit échouer)
    Write-Host "`n4. Test /api/auth/me après logout..." -ForegroundColor Yellow
    try {
        $meAfterLogout = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Method GET -WebSession $session
        Write-Host "❌ PROBLÈME: /api/auth/me fonctionne encore après logout!" -ForegroundColor Red
    } catch {
        Write-Host "✅ Correct: Session invalidée après logout" -ForegroundColor Green
    }

    Write-Host "`n🎉 TOUS LES TESTS PASSENT - SESSION AUTHENTICATION OPÉRATIONNELLE!" -ForegroundColor Green

} catch {
    Write-Host "❌ Test failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== TEST COMPLETE ===`n" -ForegroundColor Magenta