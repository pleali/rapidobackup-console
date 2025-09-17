# Test des Cookies de Session - Guide de Validation

Ce guide permet de valider que les cookies de session fonctionnent correctement après la migration.

## 🚀 Démarrage de l'application

```bash
# Démarrer en mode développement
mvn spring-boot:run

# L'application démarre sur http://localhost:8080
```

## 🔍 Tests de validation

### 1. **Vérifier les cookies dans DevTools**

1. Ouvrir http://localhost:8080 dans le navigateur
2. Ouvrir DevTools (F12)
3. Aller dans l'onglet **Application** > **Cookies**
4. **Avant login** : Aucun cookie JSESSIONID visible
5. **Se connecter** avec admin/admin
6. **Après login** : Cookie JSESSIONID doit apparaître avec :
   - `HttpOnly` : ✅ (protégé contre XSS)
   - `Secure` : ❌ (HTTP en dev, normal)
   - `SameSite` : Lax (flexible pour dev)
   - `Path` : /

### 2. **Vérifier les requêtes réseau**

1. Dans DevTools, onglet **Network**
2. Se connecter
3. Observer la requête POST `/api/auth/login` :
   - **Réponse** : Doit retourner uniquement UserDto (pas de tokens)
   - **Headers Set-Cookie** : Doit contenir JSESSIONID
4. Requêtes suivantes :
   - **Headers Cookie** : Doit inclure JSESSIONID automatiquement

### 3. **Test de persistance de session**

1. Se connecter
2. Naviguer vers `/dashboard`
3. **Actualiser la page** (F5)
4. ✅ Doit rester connecté
5. **Fermer/rouvrir l'onglet**
6. ✅ Doit rester connecté (session cookie)

### 4. **Test de déconnexion**

1. Cliquer sur "Logout"
2. Observer la requête POST `/api/auth/logout`
3. **Cookie JSESSIONID** : Doit disparaître des DevTools
4. **Redirection** : Vers /login
5. Tenter d'accéder à `/dashboard` : Redirection vers /login

## 📋 Checklist de validation

- [ ] **Cookie JSESSIONID créé** lors du login
- [ ] **HttpOnly activé** (protection XSS)
- [ ] **Secure désactivé** en dev (HTTP localhost)
- [ ] **SameSite = Lax** en dev (flexibilité)
- [ ] **Envoi automatique** dans les requêtes suivantes
- [ ] **Persistance** lors des rafraîchissements
- [ ] **Suppression** lors du logout
- [ ] **Redirection automatique** si non authentifié

## 🐛 Problèmes courants et solutions

### **Cookie JSESSIONID non créé**
```
Problème : Aucun cookie après login
Solution : Vérifier les logs Spring Session
Logs : org.springframework.session: DEBUG
```

### **Cookie non envoyé dans les requêtes**
```
Problème : Cookie présent mais pas envoyé
Solution : Vérifier withCredentials: true dans Axios
Code : apiClient.defaults.withCredentials = true
```

### **Cookie Secure en dev**
```
Problème : Secure=true bloque en HTTP
Solution : Profil dev avec setUseSecureCookie(false)
Config : @Profile("dev") in SessionConfig
```

### **SameSite trop strict**
```
Problème : Cookie bloqué lors des redirections
Solution : SameSite=Lax en développement
Config : serializer.setSameSite("Lax")
```

## 📊 Logs utiles pour le débogage

### **Logs Spring Session**
```
o.s.session.web.http.SessionRepositoryFilter : Session created: 1234567890
o.s.session.web.http.CookieHttpSessionIdResolver : Session cookie created
```

### **Logs Spring Security**
```
o.s.s.w.context.HttpSessionSecurityContextRepository : Stored SecurityContext
o.s.s.w.context.SecurityContextPersistenceFilter : SecurityContext stored to HttpSession
```

### **Logs d'authentification**
```
c.r.c.auth.service.AuthenticationService : User authenticated successfully: admin
o.s.security.authentication.ProviderManager : Authentication attempt using [Provider]
```

## 🔬 Tests avancés

### **Test de concurrence (multi-onglets)**
1. Se connecter dans l'onglet 1
2. Ouvrir l'onglet 2 sur le même domaine
3. ✅ Les deux onglets doivent partager la session

### **Test d'expiration**
1. Se connecter
2. Attendre 30 minutes (timeout de session)
3. Effectuer une action
4. ✅ Doit être redirigé vers /login

### **Test de sécurité**
1. Examiner localStorage : ✅ Aucun token visible
2. Examiner sessionStorage : ✅ Aucun token visible
3. Console JavaScript : `document.cookie` ne montre pas JSESSIONID (HttpOnly)

## 📈 Métriques de succès

- **Sécurité** : ✅ Aucun token en localStorage
- **Performance** : ✅ useIsAuthenticated instantané
- **UX** : ✅ Connexion transparente et persistante
- **Architecture** : ✅ Code simplifié (-70% vs JWT)

Si tous les tests passent, la migration vers les sessions Spring est réussie ! 🎉