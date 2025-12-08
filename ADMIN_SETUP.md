# Admin Account Setup Guide

## Security Implementation

✅ **Admin signup is now BLOCKED** - Users cannot sign up as ADMIN through the API
✅ **Admin accounts must be created manually** in the database

## How to Create Admin Account

### Method 1: Using SQL Script (Recommended)

1. **Generate a BCrypt password hash** for your admin password:
   - Use online tool: https://bcrypt-generator.com/
   - Or use the Java utility: `AdminPasswordHasher.java`
   - Password: `admin123` (or your chosen password)

2. **Run the SQL script** in PostgreSQL:
   ```bash
   psql -U parkinguser -d parkingdb -f src/main/resources/db/create-admin.sql
   ```

3. **Or manually execute** in psql:
   ```sql
   INSERT INTO users (username, email, password, role, created_at, updated_at)
   VALUES (
       'admin',
       'admin@apcparking.com',
       '$2a$10$YOUR_BCRYPT_HASH_HERE',  -- Replace with generated hash
       'ADMIN',
       NOW(),
       NOW()
   );
   ```

### Method 2: Using Java Utility

1. **Compile and run** the password hasher:
   ```bash
   mvn compile
   # Then run AdminPasswordHasher.java main method
   ```

2. **Copy the generated hash** and use it in the SQL script

### Default Admin Credentials (After Setup)

- **Username**: `admin`
- **Email**: `admin@apcparking.com` (or your chosen email)
- **Password**: `admin123` (or your chosen password)
- **Role**: `ADMIN`

## Verify Admin Account

After creating the admin account, verify it exists:

```sql
SELECT id, username, email, role, created_at 
FROM users 
WHERE role = 'ADMIN';
```

## Login as Admin

1. Go to the login page
2. Enter admin credentials:
   - Username/Email: `admin` or `admin@apcparking.com`
   - Password: `admin123` (or your chosen password)
3. You should now see the **admin dashboard** with full privileges

## Security Notes

⚠️ **Important Security Practices:**

1. **Change the default password** after first login
2. **Use a strong password** (minimum 12 characters, mix of letters, numbers, symbols)
3. **Never commit admin credentials** to version control
4. **Use environment variables** for production admin setup
5. **Limit admin account access** - only create admin accounts for trusted administrators

## Testing Admin Signup Prevention

Try to sign up as admin via API (should fail):

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "hacker",
    "email": "hacker@example.com",
    "password": "password123",
    "role": "ADMIN"
  }'
```

Expected response:
```json
{
  "error": "You cannot sign up as admin. Admin accounts must be created by system administrators."
}
```

