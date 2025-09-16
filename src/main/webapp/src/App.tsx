import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import DashboardPage from '@/features/dashboard/routes/DashboardPage';
import AgentsPage from '@/features/agents/routes/AgentsPage';
import Office365BackupPage from '@/features/office365-backup/routes/Office365BackupPage';
import LoginPage from '@/features/auth/routes/LoginPage';
import ChangePasswordPage from '@/features/auth/routes/ChangePasswordPage';
import EditProfilePage from '@/features/user-profile/routes/EditProfilePage';
import Layout from '@/components/shared/layout/Layout';
import RapidoBackupLanding from '@/features/landing/LandingPage';
import { ThemeProvider } from '@/components/theme-provider';
import { SignupPage } from '@/features/auth/routes/SignupPage';
import TestComponentsPage from './features/tests/TestComponentsPage';
import { queryClient } from '@/lib/query-client';
import { useIsAuthenticated, useCurrentUser } from '@/hooks/useAuth';
import ErrorBoundary from '@/components/shared/ErrorBoundary';

// Protected route component
const ProtectedRoute: React.FC<{ element: React.ReactNode }> = ({ element }) => {
  const isAuthenticated = useIsAuthenticated();

  return isAuthenticated ? (
    <>{element}</>
  ) : (
    <Navigate to="/login" replace />
  );
};

const App: React.FC = () => {
  const isAuthenticated = useIsAuthenticated();

  // Get user data directly from Zustand store
  const currentUser = useCurrentUser();
  const userName = isAuthenticated ? (currentUser?.email || currentUser?.firstName || "User") : undefined; 

  return (
    <ErrorBoundary>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider defaultTheme="dark" storageKey="console-ui-theme">
          <Router>
            <Routes>
              {/* Public routes */}
              <Route path="/" element={<RapidoBackupLanding />} />
              <Route path="/test" element={<TestComponentsPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/signup" element={<SignupPage />} />
              <Route path="/change-password" element={<ChangePasswordPage />} />


              {/* Protected routes - wrapped with Layout */}
              <Route path="/dashboard" element={<ProtectedRoute element={<Layout userName={userName}><DashboardPage /></Layout>} />} />
              <Route path="/agents" element={<ProtectedRoute element={<Layout userName={userName}><AgentsPage /></Layout>} />} />
              <Route path="/backups" element={<ProtectedRoute element={<Layout userName={userName}><div>Backups Page (Not implemented yet)</div></Layout>} />} />
              <Route path="/office365" element={<ProtectedRoute element={<Layout userName={userName}><Office365BackupPage /></Layout>} />} />
              <Route path="/remote" element={<ProtectedRoute element={<Layout userName={userName}><div>Remote Maintenance Page (Not implemented yet)</div></Layout>} />} />
              <Route path="/deployments" element={<ProtectedRoute element={<Layout userName={userName}><div>Deployments Page (Not implemented yet)</div></Layout>} />} />
              <Route path="/scripts" element={<ProtectedRoute element={<Layout userName={userName}><div>Scripts Page (Not implemented yet)</div></Layout>} />} />
              <Route path="/profile" element={<ProtectedRoute element={<Layout userName={userName}><EditProfilePage /></Layout>} />} /> {/* Added profile route */}

              {/* Redirect to dashboard by default if authenticated, otherwise to login */}
              <Route path="*" element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <Navigate to="/login" replace />} />
            </Routes>
          </Router>
          <ReactQueryDevtools initialIsOpen={false} />
        </ThemeProvider>
      </QueryClientProvider>
    </ErrorBoundary>
  );
};

export default App;
