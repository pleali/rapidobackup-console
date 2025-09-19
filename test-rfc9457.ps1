# Test script for RFC 9457 Problem Details implementation
Write-Host "Testing RFC 9457 Problem Details implementation..." -ForegroundColor Green

# Start the application in background
Write-Host "Starting Spring Boot application..." -ForegroundColor Yellow
$process = Start-Process -FilePath "mvn" -ArgumentList "spring-boot:run" -PassThru -WindowStyle Hidden

# Wait for application to start
Write-Host "Waiting for application to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

try {
    # Test 1: Validation error (should return RFC 9457 format)
    Write-Host "`nTest 1: Testing validation error..." -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body '{"login":"","password":""}' -ErrorAction Stop
    } catch {
        Write-Host "Response Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        $errorContent = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "RFC 9457 Response:" -ForegroundColor Yellow
        $errorContent | ConvertTo-Json -Depth 3
    }

    # Test 2: Method not allowed error
    Write-Host "`nTest 2: Testing method not allowed error..." -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method PUT -ErrorAction Stop
    } catch {
        Write-Host "Response Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        $errorContent = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "RFC 9457 Response:" -ForegroundColor Yellow
        $errorContent | ConvertTo-Json -Depth 3
    }

    # Test 3: Unsupported media type
    Write-Host "`nTest 3: Testing unsupported media type error..." -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "text/plain" -Body "invalid" -ErrorAction Stop
    } catch {
        Write-Host "Response Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        $errorContent = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "RFC 9457 Response:" -ForegroundColor Yellow
        $errorContent | ConvertTo-Json -Depth 3
    }

} finally {
    # Stop the application
    Write-Host "`nStopping application..." -ForegroundColor Yellow
    if ($process -and !$process.HasExited) {
        Stop-Process -Id $process.Id -Force
    }
}

Write-Host "`nRFC 9457 testing completed!" -ForegroundColor Green