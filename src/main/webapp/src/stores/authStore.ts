import { create } from 'zustand';
import { persist } from 'zustand/middleware';

// Import UserDto from API to keep consistency
import type { UserDto } from '@/lib/api';

interface AuthState {
  isAuthenticated: boolean;
  user: UserDto | null;
  accessToken: string | null;
  refreshToken: string | null;
}

interface AuthActions {
  login: (user: UserDto, accessToken: string, refreshToken: string) => void;
  logout: () => void;
  setUser: (user: UserDto) => void;
  clearAuth: () => void;
  updateUser: (updates: Partial<UserDto>) => void;
}

export type AuthStore = AuthState & AuthActions;

const initialState: AuthState = {
  isAuthenticated: false,
  user: null,
  accessToken: null,
  refreshToken: null,
};

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      ...initialState,

      login: (user: UserDto, accessToken: string, refreshToken: string) => {
        set({
          isAuthenticated: true,
          user,
          accessToken,
          refreshToken,
        });
      },

      logout: () => {
        set(initialState);
        // Also clear localStorage tokens (for backward compatibility)
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
      },

      setUser: (user: UserDto) => {
        set({ user });
      },

      clearAuth: () => {
        set(initialState);
      },

      updateUser: (updates: Partial<UserDto>) => {
        const currentUser = get().user;
        if (currentUser) {
          set({
            user: { ...currentUser, ...updates },
          });
        }
      },
    }),
    {
      name: 'rapidobackup-auth',
      // Only persist the necessary data
      partialize: (state) => ({
        isAuthenticated: state.isAuthenticated,
        user: state.user,
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
      }),
    }
  )
);

// Convenience hooks
export const useIsAuthenticated = () => useAuthStore((state) => state.isAuthenticated);
export const useCurrentUser = () => useAuthStore((state) => state.user);
export const useAccessToken = () => useAuthStore((state) => state.accessToken);
export const useRefreshToken = () => useAuthStore((state) => state.refreshToken);