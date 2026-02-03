package com.apc.parking.controller;

import com.apc.parking.dto.AuthResponse;
import com.apc.parking.dto.LoginRequest;
import com.apc.parking.dto.RefreshTokenRequest;
import com.apc.parking.dto.SignupRequest;
import com.apc.parking.model.User;
import com.apc.parking.service.UserService;
import com.apc.parking.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.http.ResponseCookie;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * User registration endpoint
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest, 
                                   BindingResult bindingResult,
                                   HttpServletResponse response) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(errors);
            }

            // Prevent users from signing up as ADMIN
            if (signupRequest.getRole() != null && signupRequest.getRole().equalsIgnoreCase("ADMIN")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "You cannot sign up as admin. Admin accounts must be created by system administrators.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            // Register user
            User user = userService.registerUser(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword(),
                signupRequest.getRole()
            );

            // Generate tokens
            String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getUsername(), user.getEmail(), user.getRole()
            );
            String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), user.getUsername()
            );

            // Set secure cookies
            setCookie(response, "refreshToken", refreshToken, 7 * 24 * 60 * 60); // 7 days

            // Create response
            AuthResponse authResponse = new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                jwtTokenProvider.getAccessTokenExpirationInSeconds()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed: " + e.getMessage());
            error.put("details", e.getClass().getSimpleName() + ": " + e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
                                  BindingResult bindingResult,
                                  HttpServletResponse response) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(errors);
            }

            // Authenticate user
            User user = userService.authenticateUser(
                loginRequest.getUsernameOrEmail(),
                loginRequest.getPassword()
            );

            // Generate tokens
            String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getUsername(), user.getEmail(), user.getRole()
            );
            String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), user.getUsername()
            );

            // Set secure cookies
            setCookie(response, "refreshToken", refreshToken, 7 * 24 * 60 * 60); // 7 days

            // Create response
            AuthResponse authResponse = new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                jwtTokenProvider.getAccessTokenExpirationInSeconds()
            );

            return ResponseEntity.ok(authResponse);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Refresh access token using refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
                                         BindingResult bindingResult,
                                         HttpServletResponse response) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(errors);
            }

            String refreshToken = refreshTokenRequest.getRefreshToken();

            // Validate refresh token
            if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid or expired refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // Extract user info from token
            Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

            // Get user from database
            User user = userService.findById(userId);
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // Generate new tokens
            String newAccessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getUsername(), user.getEmail(), user.getRole()
            );
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), user.getUsername()
            );

            // Set secure cookies
            setCookie(response, "refreshToken", newRefreshToken, 7 * 24 * 60 * 60); // 7 days

            // Create response
            AuthResponse authResponse = new AuthResponse(
                newAccessToken,
                newRefreshToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                jwtTokenProvider.getAccessTokenExpirationInSeconds()
            );

            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token refresh failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Logout endpoint (clears refresh token cookie)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear refresh token cookie using ResponseCookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        Map<String, String> message = new HashMap<>();
        message.put("message", "Logged out successfully");
        return ResponseEntity.ok(message);
    }

    /**
     * Set secure HTTP-only cookie for refresh token
     */
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // Use HTTPS in production
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}

