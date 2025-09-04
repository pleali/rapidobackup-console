import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // Time in milliseconds that unused/inactive cache data remains in memory
      gcTime: 1000 * 60 * 5, // 5 minutes
      // Time in milliseconds after data is considered stale
      staleTime: 1000 * 30, // 30 seconds
      // Retry failed requests 3 times
      retry: 3,
      // Retry delay that increases exponentially
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),
      // Refetch on window focus
      refetchOnWindowFocus: false,
      // Refetch on reconnect
      refetchOnReconnect: true,
    },
    mutations: {
      // Retry failed mutations once
      retry: 1,
    },
  },
});

export default queryClient;