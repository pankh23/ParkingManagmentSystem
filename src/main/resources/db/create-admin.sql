-- Script to create admin user manually in the database
-- Run this script directly in PostgreSQL to create an admin account
-- 
-- IMPORTANT: Generate a BCrypt hash for your password first!
-- Use: https://bcrypt-generator.com/ (rounds: 10)
-- Or use the Java utility: AdminPasswordHasher.java
--
-- Example: For password "admin123", generate hash and replace below

-- Delete existing admin if exists (optional - uncomment if needed)
-- DELETE FROM users WHERE username = 'admin' OR email = 'admin@apcparking.com';

-- Insert admin user
-- NOTE: Replace the password hash below with a properly generated BCrypt hash
-- Default password: "admin123"
-- Example hash for "admin123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO users (username, email, password, role, created_at, updated_at)
VALUES (
    'admin',
    'admin@apcparking.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- BCrypt hash for "admin123" (10 rounds)
    'ADMIN',
    NOW(),
    NOW()
)
ON CONFLICT (username) DO UPDATE
SET 
    email = EXCLUDED.email,
    password = EXCLUDED.password,
    role = EXCLUDED.role,
    updated_at = NOW()
ON CONFLICT (email) DO UPDATE
SET 
    username = EXCLUDED.username,
    password = EXCLUDED.password,
    role = EXCLUDED.role,
    updated_at = NOW();

-- Verify the admin was created
SELECT id, username, email, role, created_at FROM users WHERE role = 'ADMIN';
