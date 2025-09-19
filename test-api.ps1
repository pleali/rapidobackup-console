# Test OpenAPI Integration
Write-Host "Testing OpenAPI Integration..." -ForegroundColor Green

# Test 1: Check if OpenAPI docs are accessible
Write-Host "`n1. Testing OpenAPI Documentation..." -ForegroundColor Yellow
try {
    $apiDocs = Invoke-RestMethod -Uri "http://localhost:8080/v3/api-docs" -Method GET
    Write-Host "✅ OpenAPI docs accessible" -ForegroundColor Green
    Write-Host "OpenAPI version: $($apiDocs.openapi)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ OpenAPI docs not accessible: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Test login endpoint
Write-Host "`n2. Testing Login Endpoint..." -ForegroundColor Yellow
try {
    $loginBody = @{
        login = "admin"
        password = "admin"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody -SessionVariable session
    Write-Host "✅ Login successful" -ForegroundColor Green
    Write-Host "User ID: $($loginResponse.id)" -ForegroundColor Cyan
    Write-Host "User Login: $($loginResponse.login)" -ForegroundColor Cyan
    Write-Host "User Role: $($loginResponse.role)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 3: Test /me endpoint with session
if ($session) {
    Write-Host "`n3. Testing /me Endpoint with Session..." -ForegroundColor Yellow
    try {
        $meResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/me" -Method GET -WebSession $session
        Write-Host "✅ /me endpoint successful" -ForegroundColor Green
        Write-Host "Current User: $($meResponse.login)" -ForegroundColor Cyan
    } catch {
        Write-Host "❌ /me endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`nTesting completed!" -ForegroundColor Green