try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body '{"login":"admin","password":"admin"}' -ErrorAction Stop
    Write-Host "Success Response:"
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error Status:" $_.Exception.Response.StatusCode
    Write-Host "Error Content:"
    $_.ErrorDetails.Message
}