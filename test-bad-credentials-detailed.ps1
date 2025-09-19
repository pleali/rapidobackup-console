try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body '{"login":"wronguser","password":"wrongpass"}' -ErrorAction Stop
    Write-Host "Success Response:"
    $response.Content
} catch {
    Write-Host "Error Status Code:" $_.Exception.Response.StatusCode
    Write-Host "Error Status Description:" $_.Exception.Response.StatusDescription
    Write-Host "Response Content:"
    if ($_.Exception.Response -and $_.Exception.Response.GetResponseStream) {
        $streamReader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $streamReader.ReadToEnd()
        $streamReader.Close()
        Write-Host $responseBody
    } else {
        Write-Host "No response body available"
    }
}