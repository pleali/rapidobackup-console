# Debug Sessions - Guide de Diagnostic

Ce guide permet de diagnostiquer étape par étape pourquoi les sessions ne fonctionnent pas.

## 🚀 Test rapide avec endpoint de debug

### 1. Démarrer l'application
```bash
mvn spring-boot:run
```

### 2. Tester l'endpoint de session
```bash
# Via curl
curl -v http://localhost:8080/api/test/session

# Via navigateur
http://localhost:8080/api/test/session
```

### 3. Vérifier la réponse

**Headers attendus :**
```
Set-Cookie: JSESSIONID=123456789; Path=/; HttpOnly
```

**Body attendu :**
```json
{
  "sessionId": "123456789",
  "isNew": true,
  "creationTime": 1234567890,
  "lastAccessedTime": 1234567890,
  "message": "Session created/accessed successfully"
}
```

## 🔍 Diagnostic par étapes

### Étape 1: Vérifier que Spring Session est actif
```
Logs à chercher :
- "SessionRepositoryFilter"
- "Session"
- "Cookie"
```

### Étape 2: Si pas de cookie Set-Cookie
```
Problème : Spring Session n'est pas activé
Solutions :
1. Vérifier @EnableSpringHttpSession
2. Vérifier les dépendances spring-session-core
3. Vérifier les logs d'erreur au démarrage
```

### Étape 3: Si cookie créé mais pas envoyé
```
Problème : Configuration Axios
Solutions :
1. withCredentials: true dans Axios
2. Vérifier CORS allowCredentials
3. Vérifier le domaine/port
```

### Étape 4: Si tout fonctionne sur /test mais pas sur /auth
```
Problème : Configuration Spring Security
Solutions :
1. Vérifier sessionCreationPolicy
2. Vérifier que l'authentification crée bien une session
3. Vérifier les filters Spring Security
```

## 📋 Checklist de diagnostic

- [ ] **Endpoint /api/test/session** retourne une réponse
- [ ] **Header Set-Cookie** présent dans la réponse
- [ ] **Cookie JSESSIONID** visible dans DevTools
- [ ] **Deuxième appel** montre `isNew: false`
- [ ] **Cookie envoyé** automatiquement dans les requêtes suivantes

## 🛠️ Si ça ne marche toujours pas

### Option 1: Configuration Spring Boot native
```yaml
server:
  servlet:
    session:
      tracking-modes: cookie
      cookie:
        http-only: true
        secure: false
        same-site: lax
```

### Option 2: Retour aux sessions Spring Boot standards
```java
// Supprimer @EnableSpringHttpSession
// Utiliser les sessions HTTP classiques
// Configurer juste les cookies
```

### Option 3: Debug complet des logs
```yaml
logging:
  level:
    org.springframework.security: TRACE
    org.springframework.session: TRACE
    org.springframework.web: TRACE
```

## 🎯 Objectif

Une fois que `/api/test/session` fonctionne correctement, nous pourrons adapter l'authentification pour utiliser les mêmes sessions.

Le but est de voir **JSESSIONID** apparaître dans les cookies du navigateur ! 🍪