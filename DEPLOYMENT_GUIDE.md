# 🚀 Deployment Guide: Render (Backend) + Vercel (Frontend)

This guide will help you deploy the Parking Management System on Render (backend) and Vercel (frontend).

---

## ✅ Changes Made

The following files have been updated/created for deployment:

1. ✅ `application.properties` - Updated to use environment variables
2. ✅ `CORSConfig.java` - Updated to support dynamic allowed origins
3. ✅ `Dockerfile` - Created for Docker-based deployment on Render
4. ✅ `.dockerignore` - Created to exclude unnecessary files from Docker build
5. ✅ `Procfile` - Created for Render deployment (alternative method)
6. ✅ `render.yaml` - Created for Render infrastructure as code
7. ✅ `vercel.json` - Created for Vercel frontend deployment
8. ✅ `package.json` - Added `vercel-build` script

---

## 📋 Part 1: Deploy Backend on Render

### Step 1: Create PostgreSQL Database on Render

1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click **"New +"** → **"PostgreSQL"**
3. Configure:
   - **Name**: `parking-db`
   - **Database**: `parkingdb`
   - **User**: `parkinguser`
   - **Region**: Choose closest to you
   - **Plan**: Free (or paid for production)
4. Click **"Create Database"**
5. **Note down** the connection details (you'll need them)

### Step 2: Create Web Service for Backend

1. Click **"New +"** → **"Web Service"**
2. Connect your GitHub repository
3. Configure:
   - **Name**: `parking-system-backend`
   - **Region**: Same as database
   - **Branch**: `main` (or your default branch)
   - **Root Directory**: Leave empty
   - **Runtime**: `Docker` (select Docker from the list)
   - **Dockerfile Path**: `./Dockerfile` (or leave default if Dockerfile is in root)
   - **Plan**: Free (or paid)

**Note**: Since Java is not available as a direct runtime option, we're using Docker which will build and run the Java application. The Dockerfile uses a multi-stage build to create an optimized Java 17 image.

**Important**: Make sure the `Dockerfile` is committed and pushed to your GitHub repository. Render needs it to build your application.

### Step 3: Set Environment Variables

In Render Dashboard → Your Web Service → **Environment** tab, add:

```
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-d5btajh5pdvs73bstt0g-a:5432/parkingdb_q1ax
SPRING_DATASOURCE_USERNAME=parkinguser
SPRING_DATASOURCE_PASSWORD=5tJZwjLE6TIbnaSPAw1OG7NOoTY5EBpA
RAZORPAY_KEY_ID=your_production_key_id
RAZORPAY_KEY_SECRET=your_production_key_secret
JWT_SECRET=<generate a strong random string, minimum 32 characters>
ALLOWED_ORIGINS=https://your-vercel-app.vercel.app
MAIL_USERNAME=your_email@gmail.com (optional)
MAIL_PASSWORD=your_app_password (optional)
APP_EMAIL_ENABLED=false
PORT=10000
```

**Important Notes:**
- Use the **Internal Database URL** hostname (`dpg-d5btajh5pdvs73bstt0g-a`) when your backend service is on Render (same network)
- Convert PostgreSQL URL to JDBC format: `jdbc:postgresql://hostname:port/database`
- Format: `jdbc:postgresql://dpg-d5btajh5pdvs73bstt0g-a:5432/parkingdb_q1ax`

**Important Notes:**
- Get database credentials from your PostgreSQL service in Render
- Generate a strong JWT_SECRET (you can use: `openssl rand -base64 32`)
- Update `ALLOWED_ORIGINS` after deploying frontend on Vercel
- Render automatically sets `PORT` environment variable

### Step 4: Deploy

1. Click **"Create Web Service"**
2. Render will automatically build and deploy
3. Wait for deployment to complete (first build takes 5-10 minutes)
4. **Note your backend URL**: `https://parking-system-backend.onrender.com`

---

## 📋 Part 2: Deploy Frontend on Vercel

### Step 1: Deploy via Vercel Dashboard

1. Go to [Vercel Dashboard](https://vercel.com)
2. Sign up/Login with GitHub
3. Click **"Add New..."** → **"Project"**
4. Import your GitHub repository
5. Configure:
   - **Framework Preset**: `Create React App`
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build` (or leave default)
   - **Output Directory**: `build`
   - **Install Command**: `npm install`

### Step 2: Set Environment Variable

Before deploying, add environment variable:

- **Key**: `REACT_APP_API_URL`
- **Value**: `https://parking-system-backend.onrender.com/api`

**Important**: Replace `parking-system-backend.onrender.com` with your actual Render backend URL.

### Step 3: Deploy

1. Click **"Deploy"**
2. Wait for deployment (usually 2-3 minutes)
3. **Note your frontend URL**: `https://your-app.vercel.app`

### Step 4: Update Backend CORS

After getting your Vercel URL:

1. Go back to Render Dashboard → Your Backend Service → **Environment**
2. Update `ALLOWED_ORIGINS`:
   ```
   ALLOWED_ORIGINS=https://your-app.vercel.app
   ```
3. Render will automatically redeploy with the new CORS settings

---

## 🔧 Alternative: Deploy via CLI

### Backend (Render CLI)

```bash
# Install Render CLI
npm install -g render-cli

# Login
render login

# Deploy
render deploy
```

### Frontend (Vercel CLI)

```bash
# Install Vercel CLI
npm install -g vercel

# Login
vercel login

# Navigate to frontend
cd frontend

# Deploy
vercel --prod

# Set environment variable
vercel env add REACT_APP_API_URL
# Enter: https://parking-system-backend.onrender.com/api
```

---

## 🗄️ Database Initialization

After deployment, initialize your database:

### Option 1: Using Render Shell

1. Go to Render Dashboard → Your Database → **"Connect"**
2. Use the connection string to connect via psql or database client
3. Run SQL scripts from `src/main/resources/db/`:
   - `create-admin.sql`
   - `analytics-indexes.sql`

### Option 2: Using Database Client

Connect using the connection string and run:

```sql
-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_reservation_start_time ON reservations(start_time);
CREATE INDEX IF NOT EXISTS idx_reservation_end_time ON reservations(end_time);
CREATE INDEX IF NOT EXISTS idx_reservation_status ON reservations(status);
CREATE INDEX IF NOT EXISTS idx_payment_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payment_paid_at ON payments(paid_at);
```

---

## ✅ Testing Deployment

### Test Backend

```bash
# Health check
curl https://parking-system-backend.onrender.com/api/health

# Test parking lots endpoint
curl https://parking-system-backend.onrender.com/api/parking-lots
```

### Test Frontend

1. Visit your Vercel URL
2. Try logging in
3. Test creating a parking lot
4. Test making a reservation

---

## 🔐 Environment Variables Checklist

### Backend (Render)

- [ ] `SPRING_DATASOURCE_URL` (from database)
- [ ] `SPRING_DATASOURCE_USERNAME` (from database)
- [ ] `SPRING_DATASOURCE_PASSWORD` (from database)
- [ ] `RAZORPAY_KEY_ID` (your production key)
- [ ] `RAZORPAY_KEY_SECRET` (your production secret)
- [ ] `JWT_SECRET` (strong random string, min 32 chars)
- [ ] `ALLOWED_ORIGINS` (your Vercel URL)
- [ ] `MAIL_USERNAME` (optional, if using email)
- [ ] `MAIL_PASSWORD` (optional, if using email)
- [ ] `APP_EMAIL_ENABLED` (set to `false` if not using email)

### Frontend (Vercel)

- [ ] `REACT_APP_API_URL` (your Render backend URL + `/api`)

---

## ⚠️ Important Notes

### Render Free Tier Limitations

- Services spin down after 15 minutes of inactivity
- First request after spin-down may take 30-60 seconds
- Consider upgrading for production use

### Vercel Free Tier

- Unlimited deployments
- Automatic HTTPS
- Global CDN
- Perfect for production

### Security Recommendations

1. Use strong passwords for database and JWT
2. Never commit secrets to Git
3. Use environment variables for all sensitive data
4. Enable HTTPS (automatic on both platforms)
5. Regularly update dependencies

---

## 🐛 Troubleshooting

### Backend Not Starting on Render

- Check build logs in Render dashboard
- Verify Docker build is successful (check Dockerfile syntax)
- Ensure Dockerfile is in the root directory
- Check environment variables are set correctly
- Review application logs in Render dashboard
- Verify Java 17 is being used (check Dockerfile base image)
- If build fails, check Maven dependencies are resolving correctly

### Frontend Can't Connect to Backend

- Verify `REACT_APP_API_URL` is correct
- Check CORS configuration includes your Vercel URL
- Ensure backend is running (check Render dashboard)
- Check browser console for CORS errors

### Database Connection Issues

- Verify database is running on Render
- Check connection string format (use internal hostname for services on same network)
- Ensure database credentials are correct
- Check if database is accessible from your service
- **Important**: Make sure `SPRING_DATASOURCE_URL` uses the internal hostname format: `jdbc:postgresql://dpg-xxxxx:5432/database_name`
- Verify environment variables are set correctly in Render dashboard
- Check if database needs to be initialized (tables created)
- If using Render's internal database URL, ensure you're using the internal hostname, not external

### CORS Errors

- Make sure `ALLOWED_ORIGINS` includes your exact Vercel URL
- Include protocol (`https://`)
- No trailing slashes
- Redeploy backend after updating CORS

---

## 📚 Quick Reference

**Backend URL**: `https://parking-system-backend.onrender.com`  
**Frontend URL**: `https://your-app.vercel.app`  
**API Base**: `https://parking-system-backend.onrender.com/api`

---

## 🎉 Next Steps

1. ✅ Deploy backend on Render
2. ✅ Deploy frontend on Vercel
3. ✅ Update CORS with Vercel URL
4. ✅ Initialize database
5. ✅ Test the application
6. ✅ Set up custom domains (optional)
7. ✅ Configure monitoring (optional)

---

**Need Help?** Check the logs in Render and Vercel dashboards for detailed error messages.

