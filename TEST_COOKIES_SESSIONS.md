# Session Cookies Testing - Validation Guide

This guide helps validate that session cookies are working correctly after the migration.

## 🚀 Application Startup

```powershell
# Start in development mode
.\mvnw.cmd spring-boot:run

# Application starts on http://localhost:8080
```

## 🧪 PowerShell Testing Commands

```powershell
# Test login and save session cookie
$session = $null
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body '{"login":"admin","password":"admin"}' -SessionVariable session

# Test authenticated endpoint using saved session
Invoke-RestMethod -Uri "http://localhost:8080/api/test/session-info" -WebSession $session

# Test current user endpoint
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/current-user" -WebSession $session

# Test logout
Invoke-RestMethod -Uri "http://localhost:8080/api/auth/logout" -Method POST -WebSession $session
```

## 🔍 Validation Tests

### 1. **Verify cookies in DevTools**

1. Open http://localhost:8080 in browser
2. Open DevTools (F12)
3. Go to **Application** > **Cookies** tab
4. **Before login**: No RBSESSIONID cookie visible
5. **Login** with admin/admin
6. **After login**: RBSESSIONID cookie must appear with:
   - `HttpOnly`: ✅ (XSS protection)
   - `Secure`: ❌ (HTTP in dev, normal)
   - `SameSite`: Lax (flexible for dev)
   - `Path`: /

### 2. **Verify network requests**

1. In DevTools, **Network** tab
2. Login
3. Observe POST `/api/auth/login` request:
   - **Response**: Must return only UserDto (no tokens)
   - **Set-Cookie Headers**: Must contain RBSESSIONID
4. Subsequent requests:
   - **Cookie Headers**: Must include RBSESSIONID automatically

### 3. **Session persistence test**

1. Login
2. Navigate to `/dashboard`
3. **Refresh page** (F5)
4. ✅ Must remain logged in
5. **Close/reopen tab**
6. ✅ Must remain logged in (session cookie)

### 4. **Logout test**

1. Click "Logout"
2. Observe POST `/api/auth/logout` request
3. **RBSESSIONID Cookie**: Must disappear from DevTools
4. **Redirect**: To /login
5. Try accessing `/dashboard`: Redirect to /login

## 📋 Validation Checklist

- [ ] **RBSESSIONID cookie created** on login
- [ ] **HttpOnly enabled** (XSS protection)
- [ ] **Secure disabled** in dev (HTTP localhost)
- [ ] **SameSite = Lax** in dev (flexibility)
- [ ] **Automatic sending** in subsequent requests
- [ ] **Persistence** during page refreshes
- [ ] **Removal** on logout
- [ ] **Automatic redirect** when not authenticated

## 🐛 Common Issues and Solutions

### **RBSESSIONID cookie not created**
```
Problem: No cookie after login
Solution: Check Spring Session logs
Logs: org.springframework.session: DEBUG
```

### **Cookie not sent in requests**
```
Problem: Cookie present but not sent
Solution: Verify withCredentials: true in Axios
Code: apiClient.defaults.withCredentials = true
```

### **Secure cookie in dev**
```
Problem: Secure=true blocks HTTP
Solution: Dev profile with secure=false
Config: server.servlet.session.cookie.secure: false
```

### **SameSite too strict**
```
Problem: Cookie blocked during redirects
Solution: SameSite=Lax in development
Config: server.servlet.session.cookie.same-site: lax
```

## 📊 Useful Logs for Debugging

### **Spring Session Logs**
```
o.s.session.web.http.SessionRepositoryFilter : Session created: 1234567890
o.s.session.web.http.CookieHttpSessionIdResolver : Session cookie created
```

### **Spring Security Logs**
```
o.s.s.w.context.HttpSessionSecurityContextRepository : Stored SecurityContext
o.s.s.w.context.SecurityContextPersistenceFilter : SecurityContext stored to HttpSession
```

### **Authentication Logs**
```
c.r.c.auth.service.AuthenticationService : User authenticated successfully: admin
o.s.security.authentication.ProviderManager : Authentication attempt using [Provider]
```

## 🔬 Advanced Tests

### **Concurrency test (multi-tabs)**
1. Login in tab 1
2. Open tab 2 on same domain
3. ✅ Both tabs must share the session

### **Expiration test**
1. Login
2. Wait 30 minutes (session timeout)
3. Perform an action
4. ✅ Must be redirected to /login

### **Security test**
1. Check localStorage: ✅ No tokens visible
2. Check sessionStorage: ✅ No tokens visible
3. JavaScript console: `document.cookie` doesn't show RBSESSIONID (HttpOnly)

## 📈 Success Metrics

- **Security**: ✅ No tokens in localStorage
- **Performance**: ✅ Instant useIsAuthenticated
- **UX**: ✅ Transparent and persistent login
- **Architecture**: ✅ Simplified code (-70% vs JWT)

If all tests pass, the Spring Session migration is successful! 🎉