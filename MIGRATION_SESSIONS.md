# Migration JWT vers Sessions Spring

Ce document décrit la migration de l'authentification JWT vers les sessions Spring pour améliorer la sécurité.

## 🔒 Problèmes de sécurité avec JWT/localStorage

### Vulnérabilités identifiées
- **XSS (Cross-Site Scripting)** : Les tokens JWT stockés dans `localStorage` sont accessibles par tout JavaScript
- **Extensions malveillantes** : Peuvent lire le `localStorage`
- **Pas d'expiration automatique** : Les tokens persistent jusqu'à suppression manuelle
- **Exposition côté client** : Les tokens sensibles sont visibles dans le navigateur

## ✅ Architecture de sessions sécurisée

### Avantages des sessions Spring
- **Cookies `HttpOnly`** : Inaccessibles via JavaScript (protection XSS)
- **Cookies `Secure`** : Transmission uniquement via HTTPS en production
- **`SameSite`** : Protection contre CSRF
- **Expiration automatique** : Gérée côté serveur
- **Aucun token côté client** : Sécurité maximale

### Configuration par environnement
- **Développement** : Sessions en mémoire locale (simplicité)
- **Production** : Sessions Redis (scalabilité)

## 🚀 Changements apportés

### Backend Spring Boot
1. **Ajout de Spring Session**
   - `spring-session-data-redis` pour la gestion des sessions
   - Configuration conditionnelle dev/prod

2. **Suppression complète de JWT**
   - Suppression de `JwtTokenProvider`, `JwtAuthenticationFilter`
   - Suppression des dépendances `jjwt-*`
   - Suppression de la configuration JWT

3. **Simplification des endpoints**
   - `/auth/login` : Retourne `UserDto` + crée session
   - `/auth/logout` : Invalide session
   - `/auth/me` : Retourne utilisateur si session valide
   - Suppression de `/auth/refresh`

4. **Configuration sécurisée**
   ```yaml
   spring:
     session:
       timeout: 30m
       cookie:
         http-only: true
         secure: true  # prod only
         same-site: strict  # prod only
   ```

### Frontend React/TypeScript
1. **Zustand simplifié**
   - Suppression des tokens (`accessToken`, `refreshToken`)
   - Conservation de `isAuthenticated` et `user` seulement
   - Persistance locale minimale

2. **Axios avec sessions**
   ```typescript
   const apiClient = axios.create({
     withCredentials: true,  // Envoi automatique des cookies
     // Suppression des intercepteurs JWT
   });
   ```

3. **Hooks d'authentification**
   - `useLogin()` : Stocke uniquement `UserDto`
   - `useLogout()` : Invalide session côté serveur
   - `useIsAuthenticated()` : Basé sur le store local

## 📋 API simplifiée

### Avant (JWT)
```typescript
// Réponse login complexe
interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserDto;
}

// Gestion manuelle des tokens
localStorage.setItem('accessToken', token);
headers.Authorization = `Bearer ${token}`;
```

### Après (Sessions)
```typescript
// Réponse login simple
const user: UserDto = await loginUser(username, password);

// Session automatique via cookies
// Aucune gestion manuelle nécessaire
```

## 🔧 Configuration de production

### Docker Compose
```yaml
services:
  app:
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - redis

  redis:
    image: redis:7-alpine
    volumes:
      - redis_data:/data
```

### Variables d'environnement
```bash
# Production
SPRING_PROFILES_ACTIVE=prod

# Développement (par défaut)
SPRING_PROFILES_ACTIVE=dev
```

## 🏃‍♂️ Migration pour les développeurs

### Commandes utiles
```bash
# Compilation complète
mvn compile

# Test frontend
cd src/main/webapp && npm run build

# Démarrage avec profil dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Vérifications
1. ✅ Compilation backend sans erreurs
2. ✅ Compilation frontend sans erreurs
3. ✅ Suppression de toutes les références JWT
4. ✅ Configuration sessions dev/prod
5. ✅ Cookies sécurisés configurés

## 📈 Performances améliorées

### Avantages obtenus
- **`useIsAuthenticated` instantané** : Plus d'appels API systématiques
- **Simplicité architecturale** : ~70% de code d'authentification supprimé
- **Sécurité renforcée** : Plus de tokens exposés côté client
- **Maintenance simplifiée** : Architecture standard Spring

## 🔍 Test de la migration

Pour tester la nouvelle architecture :
1. Démarrer l'application : `mvn spring-boot:run`
2. Ouvrir le navigateur : `http://localhost:8080`
3. Se connecter avec admin/admin
4. Vérifier les cookies dans DevTools (onglet Application)
5. Confirmer qu'aucun token n'est stocké dans localStorage

## 📚 Ressources

- [Spring Session Documentation](https://docs.spring.io/spring-session/docs/current/reference/html5/)
- [OWASP: JWT Security Best Practices](https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html)
- [Zustand Documentation](https://zustand-demo.pmnd.rs/)