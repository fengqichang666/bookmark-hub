# Bookmark Hub

Bookmark Hub is a monorepo with:

- `frontend`: React + Vite UI
- `backend`: Spring Boot + Flyway + JPA API

## Current Status

- The backend now includes auth, dashboard, category, bookmark, member, and bookmark HTML import APIs.
- The frontend now includes login, dashboard, bookmark management, category management, member management, and import pages.
- Automated backend tests, frontend tests, and frontend production build pass in the current workspace.
- A real local backend startup still requires a running MySQL 8 instance on `localhost:3306`.

## Prerequisites

- Node.js 20+
- Java 21
- MySQL 8

## Local Development

### 1. Prepare the database

Create the local database:

```sql
CREATE DATABASE bookmark_hub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

The backend defaults to:

- database host: `localhost`
- database name: `bookmark_hub`
- username: `root`
- password: `root`

If your local credentials are different, set `DB_USERNAME` and `DB_PASSWORD` before starting the backend.

### 2. Start the backend

PowerShell example:

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:DB_USERNAME='root'
$env:DB_PASSWORD='root'

Set-Location 'D:\workspace\bookmark-hub\backend'
.\mvnw.cmd spring-boot:run
```

The backend runs on `http://localhost:8080`.

Flyway will automatically apply:

- `V1__init_schema.sql`
- `V2__seed_admin.sql`
- `V3__management_tables.sql`
- `V4__import_records.sql`

### 3. Start the frontend

```powershell
Set-Location 'D:\workspace\bookmark-hub\frontend'
npm install
npm run dev
```

The frontend runs on `http://localhost:5173`.

The Vite dev server proxies `/api` requests to `http://localhost:8080`.

## Default Account

- username: `admin`
- password: `Admin@123456`

## Backend API Smoke Test

Use PowerShell to verify the current auth flow after the backend is running:

```powershell
Invoke-RestMethod -Method Post `
  -Uri 'http://localhost:8080/api/auth/login' `
  -ContentType 'application/json' `
  -Body '{"username":"admin","password":"Admin@123456"}'
```

After you receive a token, you can call `/api/auth/me`:

```powershell
$token = (
  Invoke-RestMethod -Method Post `
    -Uri 'http://localhost:8080/api/auth/login' `
    -ContentType 'application/json' `
    -Body '{"username":"admin","password":"Admin@123456"}'
).token

Invoke-RestMethod -Method Get `
  -Uri 'http://localhost:8080/api/auth/me' `
  -Headers @{ Authorization = "Bearer $token" }
```

You can also verify the dashboard overview:

```powershell
Invoke-RestMethod -Method Get `
  -Uri 'http://localhost:8080/api/dashboard/overview' `
  -Headers @{ Authorization = "Bearer $token" }
```

## Checks

### Frontend

```powershell
Set-Location 'D:\workspace\bookmark-hub\frontend'
npm run test
npm run build
```

### Backend

```powershell
Set-Location 'D:\workspace\bookmark-hub\backend'
.\mvnw.cmd test
```
