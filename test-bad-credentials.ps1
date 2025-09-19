try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body '{"login":"wronguser","password":"wrongpass"}' -ErrorAction Stop
    Write-Host "Response:"
    $response
} catch {
    Write-Host "Error Status:" $_.Exception.Response.StatusCode
    Write-Host "Error Content:"
    $_.ErrorDetails.Message
}