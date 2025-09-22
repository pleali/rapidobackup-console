package com.rapidobackup.console.auth.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.rapidobackup.console.auth.dto.LoginRequest;
import com.rapidobackup.console.auth.dto.PasswordChangeRequest;
import com.rapidobackup.console.auth.dto.SignupRequest;
import com.rapidobackup.console.auth.service.AuthenticationService;
import com.rapidobackup.console.common.dto.MessageResponse;
import com.rapidobackup.console.user.dto.UserDto;
import com.rapidobackup.console.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Code-First Authentication Controller using SpringDoc OpenAPI generation.
 *
 * This controller uses existing DTOs and generates OpenAPI documentation automatically
 * from SpringDoc annotations. No manual OpenAPI YAML file or mappers required.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and session management")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthController(
            AuthenticationService authenticationService,
            UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticate user with login/password and create session"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class)
            )
        )
    })
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            var requestAttributes = RequestContextHolder.getRequestAttributes();
            if (!(requestAttributes instanceof ServletRequestAttributes attr)) {
                logger.error("Current request is not a servlet request");
                throw new IllegalStateException("Current request is not a servlet request");
            }
            HttpServletRequest request = attr.getRequest();

            logger.info("Login attempt for user: {} from IP: {}",
                loginRequest.getLogin(), getClientIp(request));

            // Use existing business logic - this creates the security context
            UserDto user = authenticationService.authenticate(loginRequest);

            // Create or get existing session to ensure cookies are set
            HttpSession session = request.getSession(true);

            // Rely on Spring Security's built-in session management for SecurityContext

            logger.debug("Session created/retrieved: {}", session.getId());
            logger.info("Successful login for user: {} with session: {}",
                loginRequest.getLogin(), session.getId());

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            logger.error("Login failed for user: {}", loginRequest.getLogin(), e);
            throw e; // Let global exception handler deal with this
        }
    }

    @PostMapping("/logout")
    @Operation(
        summary = "User logout",
        description = "Logout user and invalidate session"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Logout successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class)
            )
        )
    })
    @SecurityRequirement(name = "sessionAuth")
    public ResponseEntity<MessageResponse> logout() {
        try {
            logger.debug("Logout requested");

            // Get current HTTP request for session management
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attr.getRequest();

            // Use existing business logic
            authenticationService.logout();

            // Invalidate the session completely
            HttpSession session = request.getSession(false);
            if (session != null) {
                logger.debug("Invalidating session: {}", session.getId());
                session.invalidate();
            }

            MessageResponse response = new MessageResponse("Logout successful", "success");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Logout failed", e);
            throw e; // Let global exception handler deal with this
        }
    }

    @GetMapping("/me")
    @Operation(
        summary = "Get current user",
        description = "Get information about the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User information",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class)
            )
        )
    })
    @SecurityRequirement(name = "sessionAuth")
    public ResponseEntity<UserDto> getCurrentUser() {
        try {
            // Get current principal from Spring Security context
            Principal principal = getCurrentPrincipal();

            if (principal == null) {
                return ResponseEntity.status(401).build();
            }

            // Use existing business logic
            UserDto user = userService.findByLogin(principal.getName());
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            logger.error("Failed to get current user", e);
            throw e; // Let global exception handler deal with this
        }
    }

    @PostMapping("/signup")
    @Operation(
        summary = "User registration",
        description = "Register a new user account"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Registration successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error or user already exists",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class)
            )
        )
    })
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            logger.info("Signup attempt for user: {}", signupRequest.getLogin());

            // Use existing business logic
            authenticationService.registerUser(signupRequest);

            logger.info("Successful registration for user: {}", signupRequest.getLogin());

            MessageResponse response = new MessageResponse("User registered successfully", "success");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Signup failed for user: {}", signupRequest.getLogin(), e);
            throw e; // Let global exception handler deal with this
        }
    }

    @PostMapping("/change-password")
    @Operation(
        summary = "Change password",
        description = "Change the password for the current user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Password changed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Current password incorrect or validation error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class)
            )
        )
    })
    @SecurityRequirement(name = "sessionAuth")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest, HttpServletRequest request) {
        try {
            // Log session information for debugging
            logger.debug("Change password request - Session ID: {}, User Principal: {}",
                request.getSession(false) != null ? request.getSession(false).getId() : "null",
                getCurrentPrincipalName());

            // Get current principal from Spring Security context
            Principal principal = getCurrentPrincipal();

            if (principal == null) {
                logger.warn("Change password failed - no authenticated user found");
                return ResponseEntity.status(401).build();
            }

            logger.info("Password change requested by user: {}", principal.getName());

            // Use existing business logic
            authenticationService.changePassword(
                principal.getName(),
                passwordChangeRequest.getCurrentPassword(),
                passwordChangeRequest.getNewPassword()
            );

            MessageResponse response = new MessageResponse("Password changed successfully", "success");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Password change failed for user: {}", getCurrentPrincipalName(), e);
            throw e; // Let global exception handler deal with this
        }
    }

    /**
     * Helper method to get the current Spring Security principal.
     */
    private Principal getCurrentPrincipal() {
        return org.springframework.security.core.context.SecurityContextHolder
            .getContext()
            .getAuthentication();
    }

    /**
     * Helper method to safely get the current principal name.
     */
    private String getCurrentPrincipalName() {
        Principal principal = getCurrentPrincipal();
        return principal != null ? principal.getName() : "anonymous";
    }

    /**
     * Helper method to extract client IP address from request.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}