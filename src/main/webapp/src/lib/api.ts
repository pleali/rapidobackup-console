// Simulate API calls to a Spring Boot backend

export interface AuthResponse {
  token: string;
  user: {
    id: string;
    email: string;
    // other user properties
  };
}

export const loginUser = (email?: string, password?: string): Promise<AuthResponse> => {
  console.log("Attempting login for:", email); // Keep for debugging, remove password log
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (email === "user@example.com" && password === "password123") {
        resolve({
          token: "fake-jwt-token",
          user: { id: "1", email: "user@example.com" },
        });
      } else {
        reject(new Error("Invalid credentials"));
      }
    }, 1000);
  });
};

export const signupUser = (email?: string, _password?: string): Promise<{ message: string }> => {
  console.log("Attempting signup for:", email); // Keep for debugging, remove password log
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (email && email.includes("@")) {
        resolve({ message: "User registered successfully" });
      } else {
        reject(new Error("Signup failed"));
      }
    }, 1000);
  });
};

export const logoutUser = (): Promise<{ message: string }> => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({ message: "Logged out successfully" });
    }, 500);
  });
};