import { apiClient } from './axios';
import i18n from '@/config/i18n';

// Interface for ProblemDetail error responses (RFC 7807)
interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  errors?: string[];
}

// Helper function to extract error message from axios errors with i18n support
const extractErrorMessage = (error: any, defaultMessage: string): string => {
  // Handle network errors or request setup errors
  if (!error.response) {
    if (error.request) {
      // Request was made but no response received (network error, timeout, etc.)
      if (error.code === 'ECONNABORTED') {
        return i18n.t('errors.network.timeout');
      }
      if (error.code === 'ERR_NETWORK') {
        return i18n.t('errors.network.connection');
      }
      return i18n.t('errors.network.server');
    }
    // Error in request setup
    return error.message || defaultMessage;
  }

  // Handle HTTP error responses
  const errorData = error.response.data;
  const status = error.response.status;

  // Handle empty response data
  if (!errorData) {
    const httpErrorKey = `errors.http.${status}`;
    if (i18n.exists(httpErrorKey)) {
      return i18n.t(httpErrorKey);
    }
    return `HTTP ${status}: ${error.response.statusText || defaultMessage}`;
  }

  // ProblemDetail format (RFC 7807/RFC 9457)
  if (errorData.detail) {
    let message = errorData.detail;

    // If there are validation errors, append them
    if (errorData.errors && Array.isArray(errorData.errors)) {
      const validationErrors = errorData.errors.join(', ');
      message += `: ${validationErrors}`;
    }

    return message;
  }

  // Legacy format fallback
  if (errorData.message) {
    return errorData.message;
  }

  // Spring Boot default error format
  if (errorData.error) {
    let message = errorData.error;
    if (errorData.message) {
      message += `: ${errorData.message}`;
    }
    return message;
  }

  // Fallback to translated status-based message or default
  const httpErrorKey = `errors.http.${status}`;
  if (i18n.exists(httpErrorKey)) {
    return i18n.t(httpErrorKey);
  }

  return `HTTP ${status}: ${error.response.statusText || defaultMessage}`;
};

export interface UserDto {
  id: string;
  login: string;
  firstName?: string;
  lastName?: string;
  email: string;
  activated: boolean;
  langKey?: string;
  imageUrl?: string;
  role: string;
  parentId?: string;
  parentLogin?: string;
  createdDate?: string;
  lastModifiedDate?: string;
  lastLogin?: string;
  passwordChangeRequired: boolean;
}


export interface LoginRequest {
  login: string;
  password: string;
  rememberMe?: boolean;
}

export interface SignupRequest {
  login: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  langKey?: string;
}

export const loginUser = async (username: string, password: string, rememberMe: boolean = false): Promise<UserDto> => {
  try {
    const response = await apiClient.post<UserDto>('/auth/login', {
      login: username,
      password: password,
      rememberMe: rememberMe
    } as LoginRequest);

    return response.data;
  } catch (error: any) {
    const message = extractErrorMessage(error, 'Invalid credentials');
    throw new Error(message);
  }
};

export const signupUser = async (email: string, password: string): Promise<{ message: string }> => {
  try {
    const response = await apiClient.post<{ message: string }>('/auth/signup', {
      login: email,
      email: email,
      password: password,
      langKey: 'en'
    } as SignupRequest);

    return response.data;
  } catch (error: any) {
    const message = extractErrorMessage(error, 'Registration failed');
    throw new Error(message);
  }
};

export const logoutUser = async (): Promise<{ message: string }> => {
  try {
    const response = await apiClient.post<{ message: string }>('/auth/logout');

    return response.data;
  } catch (error: any) {
    const message = extractErrorMessage(error, 'Logout failed');
    throw new Error(message);
  }
};


// Get current user information (requires active session)
export const getCurrentUser = async (): Promise<UserDto> => {
  try {
    const response = await apiClient.get<UserDto>('/auth/me');
    return response.data;
  } catch (error: any) {
    const message = extractErrorMessage(error, 'Failed to get user information');
    throw new Error(message);
  }
};

export const changePassword = async (currentPassword: string, newPassword: string): Promise<{ message: string }> => {
  try {
    const response = await apiClient.post<{ message: string }>('/auth/change-password', {
      currentPassword,
      newPassword
    });

    return response.data;
  } catch (error: any) {
    const message = extractErrorMessage(error, 'Password change failed');
    throw new Error(message);
  }
};