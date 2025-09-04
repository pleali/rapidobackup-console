import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { loginUser, signupUser, logoutUser, AuthResponse, UserDto } from '@/lib/api';

// Query keys for caching
export const authKeys = {
  user: ['auth', 'user'] as const,
  all: ['auth'] as const,
};

// Hook for login mutation
export const useLogin = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ email, password }: { email: string; password: string }) =>
      loginUser(email, password),
    onSuccess: (data: AuthResponse) => {
      // Update the user cache with the new user data
      queryClient.setQueryData(authKeys.user, data.user);
      // Invalidate and refetch any auth-related queries
      queryClient.invalidateQueries({ queryKey: authKeys.all });
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

  return useMutation({
    mutationFn: (refreshToken: string) => logoutUser(refreshToken),
    onSuccess: () => {
      // Clear all cached data
      queryClient.clear();
      // Redirect to login page
      window.location.href = '/login';
    },
    onError: (error) => {
      console.error('Logout failed:', error);
      // Even if logout fails on server, clear local data
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
      queryClient.clear();
      window.location.href = '/login';
    },
  });
};

// Hook to get current user from cache or localStorage
export const useCurrentUser = () => {
  return useQuery({
    queryKey: authKeys.user,
    queryFn: (): UserDto | null => {
      const userStr = localStorage.getItem('user');
      return userStr ? JSON.parse(userStr) : null;
    },
    staleTime: Infinity, // User data doesn't go stale until manually invalidated
  });
};

// Hook to check authentication status
export const useIsAuthenticated = () => {
  const { data: user } = useCurrentUser();
  const accessToken = localStorage.getItem('accessToken');
  
  return !!(user && accessToken);
};

// Custom hook for protected routes
export const useAuthGuard = () => {
  const isAuthenticated = useIsAuthenticated();
  
  if (!isAuthenticated) {
    window.location.href = '/login';
  }
  
  return isAuthenticated;
};