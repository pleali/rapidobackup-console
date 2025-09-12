// Test de l'intégration Axios + React Query
// Ce fichier peut être supprimé après validation

import React from 'react';
import { useLogin, useCurrentUser } from './hooks/useAuth';
import { Button } from './components/ui/button';

export const TestIntegration: React.FC = () => {
  const loginMutation = useLogin();
  const { data: user, isLoading } = useCurrentUser();

  const handleTestLogin = () => {
    loginMutation.mutate({
      username: 'admin',
      password: 'admin'
    });
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-4">Test Intégration Axios + React Query</h2>
      
      <div className="mb-4">
        <h3 className="font-semibold">État utilisateur:</h3>
        {isLoading ? (
          <p>Chargement...</p>
        ) : user ? (
          <pre>{JSON.stringify(user, null, 2)}</pre>
        ) : (
          <p>Aucun utilisateur connecté</p>
        )}
      </div>

      <div className="mb-4">
        <h3 className="font-semibold">Test Login:</h3>
        <Button 
          onClick={handleTestLogin} 
          disabled={loginMutation.isPending}
        >
          {loginMutation.isPending ? 'Connexion...' : 'Tester Login'}
        </Button>
        
        {loginMutation.error && (
          <p className="text-red-500 mt-2">
            Erreur: {loginMutation.error.message}
          </p>
        )}
        
        {loginMutation.isSuccess && (
          <p className="text-green-500 mt-2">
            Login réussi!
          </p>
        )}
      </div>

      <div className="text-sm text-gray-600">
        <p>✅ Axios configuré avec intercepteurs</p>
        <p>✅ React Query configuré avec QueryClient</p>
        <p>✅ Hooks d'authentification créés</p>
        <p>✅ Hooks d'agents préparés</p>
        <p>✅ Pages Login/Signup migrées</p>
      </div>
    </div>
  );
};

export default TestIntegration;