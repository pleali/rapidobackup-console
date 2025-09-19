import { useMutation, useQueryClient } from '@tanstack/react-query';
import { loginUser, signupUser, logoutUser, changePassword, UserDto } from '@/lib/api';
import { useAuthStore, useIsAuthenticated, useCurrentUser } from '@/stores/authStore';

// Re-export the hooks from the store for consistency
export { useIsAuthenticated, useCurrentUser };

// Hook for login mutation
export const useLogin = () => {
  const { login } = useAuthStore();

  return useMutation({
    mutationFn: ({ username, password, rememberMe = false }: { username: string; password: string; rememberMe?: boolean }) =>
      loginUser(username, password, rememberMe),
    onSuccess: (user: UserDto) => {
      try {
        // Update Zustand store with user data only
        login(user);
      } catch (error) {
        console.error('Error storing auth data:', error);
      }
    },
    onError: (error) => {
      console.error('Login failed:', error);
    },
  });
};

// Hook for signup mutation
export const useSignup = () => {
  return useMutation({
    mutationFn: ({ email, password }: { email: string; password: string }) =>
      signupUser(email, password),
    onError: (error) => {
      console.error('Signup failed:', error);
    },
  });
};

// Hook for logout mutation
export const useLogout = () => {
  const queryClient = useQueryClient();
  const { logout } = useAuthStore();

  return useMutation({
    mutationFn: () => logoutUser(),
    onSuccess: () => {
      // Clear Zustand store
      logout();
      // Clear React Query cache
      queryClient.clear();
      // Redirect to landing page
      window.location.href = '/?reason=logout_success';
    },
    onError: (error) => {
      console.error('Logout failed:', error);
      // Even if logout fails on server, clear local data
      logout();
      queryClient.clear();
      window.location.href = '/?reason=logout_success';
    },
  });
};

// Note: useCurrentUser and useIsAuthenticated are now exported from the store above

// Custom hook for protected routes
export const useAuthGuard = () => {
  const isAuthenticated = useIsAuthenticated();
  
  if (!isAuthenticated) {
    window.location.href = '/login';
  }
  
  return isAuthenticated;
};

// Hook for password change mutation
export const useChangePassword = () => {
  const { updateUser } = useAuthStore();

  return useMutation({
    mutationFn: ({ currentPassword, newPassword }: { currentPassword: string; newPassword: string }) =>
      changePassword(currentPassword, newPassword),
    onSuccess: () => {
      // Update user to mark password change as no longer required
      updateUser({ passwordChangeRequired: false });
    },
    onError: (error) => {
      console.error('Password change failed:', error);
    },
  });
};