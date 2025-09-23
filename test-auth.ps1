# Test authentication flow to check UUID retrieval
$sessionVar = New-Object Microsoft.PowerShell.Commands.WebRequestSession

# Login
Write-Host "Testing login..."
$loginResponse = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method POST -ContentType 'application/json' -Body '{"login":"admin","password":"admin"}' -WebSession $sessionVar
Write-Host "Login response:"
$loginResponse

# Show session cookies immediately after login
Write-Host "`nSession cookies after login:"
$cookies = $sessionVar.Cookies.GetCookies("http://localhost:8080")
foreach ($cookie in $cookies) {
    Write-Host "Cookie: $($cookie.Name) = $($cookie.Value)"
}

# Get current user using the SAME session variable
Write-Host "`nTesting /me endpoint with the same WebSession..."
try {
    $userResponse = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/me' -Method GET -WebSession $sessionVar
    Write-Host "SUCCESS! User response:"
    $userResponse
} catch {
    Write-Host "Error getting current user:"
    Write-Host $_.Exception.Message

    # Try to get the detailed response
    if ($_.Exception.Response) {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response body: $responseBody"
        $reader.Close()
        $stream.Close()
    }
}

# Show final session state
Write-Host "`nFinal session cookies:"
$sessionVar.Cookies.GetCookies("http://localhost:8080")