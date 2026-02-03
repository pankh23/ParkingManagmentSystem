# Database Setup – Fix Backend Crash (Suspended / Expired DB)

The backend crashes with **"Cannot create PoolableConnectionFactory"** and **`UnknownHostException: dpg-d5btajh5pdvs73bstt0g-a`** because the PostgreSQL database it uses is **suspended or expired** (e.g. Render free tier). The app cannot connect until the database is active again or pointed to a new one.

---

## 1. Fix on Render (Deployed Backend)

Your **parking-db** is suspended because the **free database has expired**. You have two options.

### Option A: Reactivate the existing database (paid)

1. In Render Dashboard → **PostgreSQL** → **parking-db**.
2. Click **"Upgrade database"** (or similar) and switch to a **paid plan**.
3. After the database is active again, **redeploy** the web service (or wait for it to reconnect).
4. No code or `render.yaml` changes needed; the service already uses `parking-db`.

### Option B: Create a new PostgreSQL and point the backend to it

1. In Render Dashboard → **New** → **PostgreSQL**.
2. Create a new database (e.g. **parking-db2**), choose a **plan** (free or paid).
3. After it’s created, open the new database and copy:
   - **Internal Database URL** (host looks like `dpg-xxxxx-a`).
4. Convert to JDBC format:
   - From: `postgres://USER:PASSWORD@HOST:PORT/DATABASE`
   - To: `jdbc:postgresql://HOST:PORT/DATABASE`
5. In Render → your **Web Service** (parking-system-backend) → **Environment**:
   - Set **SPRING_DATASOURCE_URL** = `jdbc:postgresql://NEW_HOST:5432/NEW_DATABASE`
   - Set **SPRING_DATASOURCE_USERNAME** = user from the new DB.
   - Set **SPRING_DATASOURCE_PASSWORD** = password from the new DB.
6. **Save** and let the service redeploy.

If you use **render.yaml** and want the new DB linked there, add a new entry under `databases` and point the web service env to it (or keep using manual env vars as above).

---

## 2. Run Locally (Without Render DB)

For local development, use a **local PostgreSQL** instance. Do **not** set `SPRING_DATASOURCE_URL` (or any env) to the old Render host (`dpg-d5btajh5pdvs73bstt0g-a`), or the backend will try to connect to the suspended DB and crash.

### Steps

1. **Install and start PostgreSQL** (e.g. Homebrew on macOS: `brew install postgresql@14` then `brew services start postgresql@14`).
2. **Create database and user** (one-time). From the project root, using the script provided:

   ```bash
   psql -U postgres -f src/main/resources/db/local-setup.sql
   ```
   (You can ignore "already exists" errors if you run it again.)

   Or run manually:

   ```sql
   CREATE USER parkinguser WITH PASSWORD 'parkingpass';
   CREATE DATABASE parkingdb OWNER parkinguser;
   \c parkingdb
   GRANT ALL PRIVILEGES ON DATABASE parkingdb TO parkinguser;
   GRANT ALL ON SCHEMA public TO parkinguser;
   ```

3. **Do not set** `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, or `SPRING_DATASOURCE_PASSWORD` in your local environment (so defaults in `application.properties` / `applicationContext.xml` are used).
4. **Start the backend:**

   ```bash
   ./start-backend.sh
   ```

Defaults used when env vars are **not** set:

- **URL:** `jdbc:postgresql://localhost:5432/parkingdb`
- **Username:** `parkinguser`
- **Password:** `parkingpass`

If you use a `.env` or copy-pasted Render env vars locally, remove or comment out any line that sets `SPRING_DATASOURCE_URL` to the Render host.

---

## 3. Summary

| Where        | Problem                          | Fix                                                                 |
|-------------|-----------------------------------|---------------------------------------------------------------------|
| **Render**  | parking-db suspended/expired      | Upgrade existing DB **or** create new PostgreSQL and set env vars.  |
| **Local**   | Backend still using Render DB URL | Use local PostgreSQL; do not set `SPRING_DATASOURCE_*` to Render.   |

After the database is active and the backend is pointed to it (on Render or local), the connection errors and backend crash should stop.
