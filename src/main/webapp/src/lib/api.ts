import { apiClient } from './axios';

// Interface for ProblemDetail error responses (RFC 7807)
interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
  errors?: string[];
}

// Helper function to extract error message from ProblemDetail or legacy format
const extractErrorMessage = (error: any, defaultMessage: string): string => {
  const errorData = error.response?.data;

  if (!errorData) {
    return defaultMessage;
  }

  // ProblemDetail format (RFC 7807)
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

  return defaultMessage;
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

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserDto;
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

export const loginUser = async (username: string, password: string, rememberMe: boolean = false): Promise<AuthResponse> => {
  try {
    const response = await apiClient.post<AuthResponse>('/auth/login', {
      login: username,
      password: password,
      rememberMe: rememberMe
    } as LoginRequest);

    // Store tokens after successful login
    const { accessToken, refreshToken } = response.data;
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(response.data.user));

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

export const logoutUser = async (refreshToken: string): Promise<{ message: string }> => {
  try {
    const response = await apiClient.post<{ message: string }>('/auth/logout', {
      refreshToken
    });

    // Clear tokens after successful logout
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');

    return response.data;
  } catch (error: any) {
    const message = extractErrorMessage(error, 'Logout failed');
    throw new Error(message);
  }
};

export const refreshToken = async (refreshToken: string): Promise<AuthResponse> => {
  try {
    const response = await apiClient.post<AuthResponse>('/auth/refresh', {
      refreshToken
    });

    return response.data;
  } catch (error: any) {
    const message = extractErrorMessage(error, 'Token refresh failed');
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