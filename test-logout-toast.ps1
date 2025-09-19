# Test script to simulate logout and check toast
Write-Host "Testing logout toast functionality..." -ForegroundColor Green

# First, let's test accessing the landing page directly with the logout_success parameter
$testUrl = "http://localhost:3000/?reason=logout_success"
Write-Host "Opening URL: $testUrl" -ForegroundColor Yellow

# Check if the development server is running
try {
    $response = Invoke-WebRequest -Uri "http://localhost:3000" -Method HEAD -TimeoutSec 5 -ErrorAction Stop
    Write-Host "Development server is running" -ForegroundColor Green

    # Open the test URL in the default browser
    Start-Process $testUrl
    Write-Host "Check your browser for the logout success toast!" -ForegroundColor Cyan
} catch {
    Write-Host "Development server is not running. Please start it with 'npm run dev'" -ForegroundColor Red
}