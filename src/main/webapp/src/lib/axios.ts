import axios, { AxiosResponse } from 'axios';

const API_BASE_URL = '/api';

// Create axios instance with session support
export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
  withCredentials: true, // Include cookies in requests (for session management)
});

// Simple response interceptor to handle authentication errors
apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error) => {
    // If we get a 401 error, redirect to login page (session expired)
    if (error.response?.status === 401 && !(error.config?.url?.includes('/auth/login') || error.config?.url?.includes('/auth/change-password'))) {
      // Clear any local state if needed
      localStorage.clear();
      window.location.href = '/login?reason=session_expired';
    }

    return Promise.reject(error);
  }
);

export default apiClient;