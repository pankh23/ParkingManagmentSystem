-- Local development: create user and database for APC Parking Management System.
-- Run once (as postgres superuser):
--   psql -U postgres -f src/main/resources/db/local-setup.sql
-- Or run the commands below manually in psql.

-- Create user (skip if already exists)
CREATE USER parkinguser WITH PASSWORD 'parkingpass';

-- Create database (skip if already exists)
CREATE DATABASE parkingdb OWNER parkinguser;

-- Connect to parkingdb and grant privileges
\c parkingdb

GRANT ALL PRIVILEGES ON DATABASE parkingdb TO parkinguser;
GRANT ALL ON SCHEMA public TO parkinguser;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO parkinguser;
