/**
 * Generated API client integration example
 *
 * This file demonstrates how to use the OpenAPI-generated TypeScript client
 * alongside the existing manual API implementation.
 */

import { AuthenticationApiFactory, Configuration, LoginRequest, UserDto, MessageResponse, SignupRequestLangKeyEnum } from './generated';
import { apiClient } from './axios';

// Create a configuration for the generated client that uses our existing axios instance
const configuration = new Configuration({
  basePath: '/api',
  // We can pass our configured axios instance to maintain session handling
});

// Create the generated API client using the Factory
const authApi = AuthenticationApiFactory(configuration, '/api', apiClient);

/**
 * Example: Using the generated client for login
 * This replaces the manual loginUser function in api.ts
 */
export const loginUserGenerated = async (
  username: string,
  password: string,
  rememberMe: boolean = false
): Promise<UserDto> => {
  try {
    const loginRequest: LoginRequest = {
      login: username,
      password: password,
      rememberMe: rememberMe
    };

    const response = await authApi.login(loginRequest);
    return response.data;
  } catch (error: any) {
    // Error handling would be similar to the manual implementation
    throw new Error(error.response?.data?.detail || 'Login failed');
  }
};

/**
 * Example: Using the generated client for getting current user
 */
export const getCurrentUserGenerated = async (): Promise<UserDto> => {
  try {
    const response = await authApi.getCurrentUser();
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.detail || 'Failed to get user information');
  }
};

/**
 * Example: Using the generated client for logout
 */
export const logoutUserGenerated = async (): Promise<{ message?: string }> => {
  try {
    const response = await authApi.logout();
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.detail || 'Logout failed');
  }
};

/**
 * Example: Using the generated client for signup
 */
export const signupUserGenerated = async (
  email: string,
  password: string,
  firstName?: string,
  lastName?: string,
  langKey: SignupRequestLangKeyEnum = SignupRequestLangKeyEnum.EN
): Promise<{ message?: string }> => {
  try {
    const signupRequest = {
      login: email,
      email: email,
      password: password,
      firstName: firstName,
      lastName: lastName,
      langKey: langKey
    };

    const response = await authApi.signup(signupRequest);
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.detail || 'Registration failed');
  }
};

/**
 * Example: Using the generated client for password change
 */
export const changePasswordGenerated = async (
  currentPassword: string,
  newPassword: string
): Promise<{ message?: string }> => {
  try {
    const passwordChangeRequest = {
      currentPassword,
      newPassword
    };

    const response = await authApi.changePassword(passwordChangeRequest);
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.detail || 'Password change failed');
  }
};

// Export the API instance for direct use if needed
export { authApi };

/**
 * Migration Notes:
 *
 * 1. Type Safety: The generated client provides full TypeScript type safety
 * 2. Consistency: All API calls follow the same pattern
 * 3. Validation: Request/Response validation is handled automatically
 * 4. Documentation: The generated client includes JSDoc comments from OpenAPI spec
 * 5. Maintainability: API changes in OpenAPI spec automatically update the client
 *
 * To migrate from manual api.ts:
 * 1. Replace imports from './api' with './generated-api-client'
 * 2. Update function names (loginUser -> loginUserGenerated)
 * 3. Error handling may need adjustment based on your preferences
 * 4. Consider keeping the error translation logic from the original api.ts
 */