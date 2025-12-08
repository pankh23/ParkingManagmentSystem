package com.apc.parking.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes for admin accounts
 * Run this main method to generate a hash for your admin password
 */
public class AdminPasswordHasher {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Change this password to your desired admin password
        String password = "admin123";
        
        String hashedPassword = encoder.encode(password);
        
        System.out.println("==========================================");
        System.out.println("Admin Password Hash Generator");
        System.out.println("==========================================");
        System.out.println("Original Password: " + password);
        System.out.println("BCrypt Hash: " + hashedPassword);
        System.out.println("==========================================");
        System.out.println("\nUse this hash in the SQL script to create admin user.");
        System.out.println("\nExample SQL:");
        System.out.println("INSERT INTO users (username, email, password, role, created_at, updated_at)");
        System.out.println("VALUES ('admin', 'admin@apcparking.com', '" + hashedPassword + "', 'ADMIN', NOW(), NOW());");
        System.out.println("==========================================");
    }
}

