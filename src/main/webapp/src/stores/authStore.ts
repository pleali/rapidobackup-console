import { create } from 'zustand';
import { persist } from 'zustand/middleware';

// Import UserDto from API to keep consistency
import type { UserDto } from '@/lib/api';

interface AuthState {
  isAuthenticated: boolean;
  user: UserDto | null;
}

interface AuthActions {
  login: (user: UserDto) => void;
  logout: () => void;
  setUser: (user: UserDto) => void;
  clearAuth: () => void;
  updateUser: (updates: Partial<UserDto>) => void;
}

export type AuthStore = AuthState & AuthActions;

const initialState: AuthState = {
  isAuthenticated: false,
  user: null,
};

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      ...initialState,

      login: (user: UserDto) => {
        set({
          isAuthenticated: true,
          user,
        });
      },

      logout: () => {
        set(initialState);
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
      }),
    }
  )
);

// Convenience hooks
export const useIsAuthenticated = () => useAuthStore((state) => state.isAuthenticated);
export const useCurrentUser = () => useAuthStore((state) => state.user);