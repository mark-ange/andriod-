const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const path = require('path');
const connectDB = require('./config/db');

// Load environment variables
dotenv.config();

// Connect to MongoDB
connectDB();

const app = express();

// Enable CORS for Android Emulator, Localhost, Mobile, and Web/Desktop Dashboard
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

// Body parsing Middleware
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// Health Check API
app.get('/api/health', (req, res) => {
  res.json({
    status: 'ONLINE',
    system: 'CityCare CDO Resident Portal API',
    timestamp: new Date().toISOString()
  });
});

// API Routes
app.use('/api/auth', require('./routes/authRoutes'));
app.use('/api/reports', require('./routes/reportRoutes'));
app.use('/api/schedules', require('./routes/scheduleRoutes'));
app.use('/api/notifications', require('./routes/notificationRoutes'));

// Root endpoint
app.get('/', (req, res) => {
  res.send('CityCare CDO Node.js / Express API Backend is Running...');
});

// Port configuration
const PORT = process.env.PORT || 5000;

app.listen(PORT, '0.0.0.0', () => {
  console.log(`====================================================`);
  console.log(`🚀 CityCare Backend API Server running on port ${PORT}`);
  console.log(`📡 Local endpoint: http://localhost:${PORT}`);
  console.log(`📱 Android Emulator endpoint: http://10.0.2.2:${PORT}`);
  console.log(`====================================================`);
});
