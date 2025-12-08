-- Script to update existing admin user to ADMIN role
-- Run this if you already have an admin account with USER role

-- Update existing admin account to ADMIN role
UPDATE users 
SET 
    role = 'ADMIN',
    updated_at = NOW()
WHERE username = 'admin' OR email = 'admin@example.com';

-- Verify the update
SELECT id, username, email, role, created_at, updated_at 
FROM users 
WHERE username = 'admin' OR email = 'admin@example.com';

