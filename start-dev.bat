@echo off
echo Starting Backup Console Development Environment...

echo.
echo 1. Starting infrastructure services...
docker-compose -f docker-compose.dev.yml up -d postgres redis

echo.
echo 2. Waiting for PostgreSQL to be ready...
timeout /t 10 /nobreak >nul

echo.
echo 3. Running database migrations...
call mvnw.cmd liquibase:update -Dspring.profiles.active=dev

echo.
echo 4. Starting Spring Boot application...
echo Backend will be available at http://localhost:8080
echo Adminer (DB admin) will be available at http://localhost:8081
echo Redis Commander will be available at http://localhost:8082
echo.

start cmd /k "call mvnw.cmd spring-boot:run -Dspring.profiles.active=dev"

echo.
echo Development environment started!
echo Press any key to stop all services...
pause >nul

echo.
echo Stopping services...
docker-compose -f docker-compose.dev.yml down

echo Development environment stopped.