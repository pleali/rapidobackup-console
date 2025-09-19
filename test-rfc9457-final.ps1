# Test final RFC 9457 après correction configuration
Write-Host "=== Test RFC 9457 Après Correction Config ===" -ForegroundColor Green

# Test validation error avec champs vides (doit trigger @NotBlank)
Write-Host "`n1. Test validation @NotBlank..." -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body '{"login":"","password":""}' -ErrorAction Stop
    Write-Host "ERREUR: Devrait échouer avec validation error!" -ForegroundColor Red
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
    if ($_.ErrorDetails.Message) {
        $error = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "RFC 9457 Response:" -ForegroundColor White
        Write-Host "  type: $($error.type)" -ForegroundColor White
        Write-Host "  title: $($error.title)" -ForegroundColor White
        Write-Host "  status: $($error.status)" -ForegroundColor White
        Write-Host "  detail: $($error.detail)" -ForegroundColor White
        if ($error.instance) { Write-Host "  instance: $($error.instance)" -ForegroundColor White }
        if ($error.errors) {
            Write-Host "  errors: $($error.errors | ConvertTo-Json -Compress)" -ForegroundColor White
        }
    } else {
        Write-Host "PAS DE RESPONSE BODY - PROBLÈME!" -ForegroundColor Red
    }
}

# Test méthode non supportée
Write-Host "`n2. Test method not allowed..." -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method PUT -ErrorAction Stop
    Write-Host "ERREUR: Devrait échouer avec method not allowed!" -ForegroundColor Red
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
    if ($_.ErrorDetails.Message) {
        $error = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "RFC 9457 Response:" -ForegroundColor White
        Write-Host "  type: $($error.type)" -ForegroundColor White
        Write-Host "  title: $($error.title)" -ForegroundColor White
        Write-Host "  status: $($error.status)" -ForegroundColor White
    } else {
        Write-Host "PAS DE RESPONSE BODY - PROBLÈME!" -ForegroundColor Red
    }
}

# Test content type non supporté
Write-Host "`n3. Test unsupported media type..." -ForegroundColor Cyan
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "text/plain" -Body "test" -ErrorAction Stop
    Write-Host "ERREUR: Devrait échouer avec unsupported media type!" -ForegroundColor Red
} catch {
    Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
    if ($_.ErrorDetails.Message) {
        $error = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "RFC 9457 Response:" -ForegroundColor White
        Write-Host "  type: $($error.type)" -ForegroundColor White
        Write-Host "  title: $($error.title)" -ForegroundColor White
        Write-Host "  status: $($error.status)" -ForegroundColor White
    } else {
        Write-Host "PAS DE RESPONSE BODY - PROBLÈME!" -ForegroundColor Red
    }
}

Write-Host "`n=== Test terminé - Vérifiez si RFC 9457 fonctionne maintenant ===" -ForegroundColor Green