# CityCare CDO - Backend API Service

This is the Node.js / Express & MongoDB REST API server for **CityCare CDO** (System Architecture Tier 2: Application Tier & Tier 3: Data Tier).

---

## 🏗️ System Architecture Overview

```
+-----------------------------------------------------------------------+
|                       1. CLIENT TIER (User Interfaces)                |
|  +----------------------------+     +-------------------------------+ |
|  |       Web / Desktop        |     |       Kotlin Mobile App       | |
|  |     Dashboard (Admin)      |     |     (Community Residents)     | |
|  +--------------+-------------+     +---------------+---------------+ |
+-----------------|-----------------------------------|-----------------+
                  |                                   |
                  +-----------------+   +-------------+
                                    |   |
+-----------------------------------|---|-------------------------------+
|                       2. APPLICATION TIER (Server & Logic)            |
|  +-----------------------------------------------------------------+  |
|  |                   Node.js / Express API Server                   |  |
|  |            (JWT Authentication & Incident Processing)           |  |
|  +--------------------------------+--------------------------------+  |
+-----------------------------------|-----------------------------------+
                                    |
+-----------------------------------|-----------------------------------+
|                       3. DATA & EXTERNAL SERVICES                     |
|  +--------------------------------+--------------------------------+  |
|  |                       MongoDB Database                          |  |
|  |               (Users, Incident Reports, Schedules)              |  |
|  +-----------------------------------------------------------------+  |
+-----------------------------------------------------------------------+
```

---

## 📁 Project Structure

```
backend/
├── config/
│   └── db.js                 # MongoDB Connection Logic
├── middleware/
│   └── authMiddleware.js     # JWT Authorization & Admin Role Enforcement
├── models/
│   ├── User.js               # Resident & Admin User Model
│   ├── Report.js             # Waste Incident Report Model
│   ├── Schedule.js           # Garbage Collection Schedule Model
│   └── Notification.js       # Push Notification History Model
├── routes/
│   ├── authRoutes.js         # Register, Login, Profile endpoints
│   ├── reportRoutes.js       # Incident Report CRUD endpoints
│   ├── scheduleRoutes.js     # Waste Collection Schedule endpoints
│   └── notificationRoutes.js # Notification endpoints
├── .env                      # Environment Variables
├── .env.example              # Environment Configuration Template
├── package.json              # Node.js dependencies
├── README.md                 # Documentation & Connection Guide
└── server.js                 # Express Application Entry Point
```

---

## ⚡ Quick Start Guide

### 1. Prerequisites
- [Node.js](https://nodejs.org/) (v16 or higher)
- [MongoDB](https://www.mongodb.com/try/download/community) (Local instance running on `mongodb://127.0.0.1:27017` or MongoDB Atlas URI)

### 2. Installation
Open your terminal in the `backend/` folder:
```bash
cd backend
npm install
```

### 3. Running the Backend Server
Start in production mode:
```bash
npm start
```
Or start in development mode with auto-reload (Nodemon):
```bash
npm run dev
```

Server output:
```text
🚀 CityCare Backend API Server running on port 5000
📡 Local endpoint: http://localhost:5000
📱 Android Emulator endpoint: http://10.0.2.2:5000
MongoDB Connected: 127.0.0.1
```

---

## 🔌 How to Connect Frontends to this Backend

### A. Connecting the Kotlin Android Mobile App
1. **Host Address on Android Emulator:**
   - In Android Emulator, `10.0.2.2` points directly to your computer's `localhost`.
   - The Android app is already configured to talk to `http://10.0.2.2:5000/api` in `CityCareApiService.kt`.

2. **Connecting from a Physical Android Phone:**
   - Find your local IP address (e.g., `192.168.1.15` via `ipconfig` on Windows or `ifconfig` on Mac/Linux).
   - In `CityCareApiService.kt`, change `BASE_URL` to:
     ```kotlin
     var BASE_URL = "http://192.168.1.15:5000/api"
     ```
   - Ensure your phone and PC are on the same Wi-Fi network.

3. **Sample Request from Kotlin Mobile App:**
   ```kotlin
   // Submit incident report to backend
   val isSuccess = CityCareApiService.uploadReportToCloud(reportData)
   ```

---

### B. Connecting the Desktop / Web Admin Dashboard
Whether your desktop backend/dashboard is built using React, Vue, Electron, Node, Python, or Java C# desktop framework:

1. **API Base URL:**
   ```javascript
   const API_BASE_URL = "http://localhost:5000/api";
   ```

2. **Fetching All Incident Reports for Admin Review:**
   ```javascript
   // GET http://localhost:5000/api/reports
   const response = await fetch("http://localhost:5000/api/reports");
   const data = await response.json();
   console.log("All Reports:", data.data);
   ```

3. **Updating Incident Status / Assigning Dispatch Driver:**
   ```javascript
   // PUT http://localhost:5000/api/reports/#CDO-2026-0412/status
   const response = await fetch("http://localhost:5000/api/reports/#CDO-2026-0412/status", {
     method: "PUT",
     headers: {
       "Content-Type": "application/json"
     },
     body: JSON.stringify({
       status: "ASSIGNED",
       unitAssigned: "CLENRO Truck #4"
     })
   });
   ```

---

## 📡 API Endpoints Summary

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/health` | Server Health & Online Status |
| `POST` | `/api/auth/register` | Register new resident account |
| `POST` | `/api/auth/login` | Login user & receive JWT token |
| `GET` | `/api/auth/profile` | Get current user profile (JWT protected) |
| `GET` | `/api/reports` | Get all incident waste reports |
| `POST` | `/api/reports` | Submit a new waste concern report |
| `PUT` | `/api/reports/:id/status` | Update report status (`PENDING`, `ASSIGNED`, `RESOLVED`) |
| `GET` | `/api/schedules` | Get collection schedule by barangay |
| `GET` | `/api/notifications` | Get push notification list |

---
