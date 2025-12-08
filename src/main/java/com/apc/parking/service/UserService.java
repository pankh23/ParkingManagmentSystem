package com.apc.parking.service;

import com.apc.parking.model.User;
import com.apc.parking.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Username validation pattern (alphanumeric and underscore, 3-50 chars)
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,50}$"
    );

    /**
     * Register a new user with password hashing and validation
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public User registerUser(String username, String email, String password, String role) {
        // Validate inputs
        validateUsername(username);
        validateEmail(email);
        validatePassword(password);

        // Check if username already exists
        if (userDao.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userDao.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Prevent users from registering as ADMIN
        if (role != null && role.equalsIgnoreCase("ADMIN")) {
            throw new IllegalArgumentException("You cannot register as admin. Admin accounts must be created by system administrators.");
        }

        // Sanitize inputs to prevent XSS
        username = sanitizeInput(username);
        email = sanitizeInput(email).toLowerCase().trim();

        // Create new user (force USER role for all signups)
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashPassword(password));
        user.setRole("USER"); // Always set to USER, ignore any role parameter

        // Save user (transaction managed by @Transactional on UserDao)
        return userDao.save(user);
    }

    /**
     * Authenticate user by username/email and password
     */
    public User authenticateUser(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || password == null) {
            throw new IllegalArgumentException("Username/email and password are required");
        }

        // Find user by username or email
        User user = userDao.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userDao.findByEmail(usernameOrEmail);
        }

        if (user == null) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return user;
    }

    /**
     * Find user by username
     */
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    /**
     * Find user by email
     */
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    /**
     * Find user by ID
     */
    public User findById(Long id) {
        return userDao.findById(id);
    }

    /**
     * Hash password using BCrypt
     */
    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Validate username format
     */
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Username must be 3-50 characters and contain only letters, numbers, and underscores");
        }
    }

    /**
     * Validate email format
     */
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        String emailLower = email.toLowerCase().trim();
        if (!EMAIL_PATTERN.matcher(emailLower).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Validate password strength
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (password.length() > 100) {
            throw new IllegalArgumentException("Password must not exceed 100 characters");
        }
    }

    /**
     * Sanitize input to prevent XSS attacks
     * Remove potentially dangerous characters
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Remove HTML tags and script content
        return input.replaceAll("<[^>]*>", "")
                   .replaceAll("javascript:", "")
                   .replaceAll("on\\w+=", "")
                   .trim();
    }
}

