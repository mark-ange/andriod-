import mongoose from 'mongoose';
import dotenv from 'dotenv';

dotenv.config();

import Barangay from './models/Barangay.js';
import CollectionSchedule from './models/CollectionSchedule.js';
import WasteReport from './models/WasteReport.js';
import User from './models/User.js';

const seedData = async () => {
  try {
    await mongoose.connect(process.env.MONGO_URI);
    console.log('MongoDB Connected for Seeding...');

    await Barangay.deleteMany({});
    await CollectionSchedule.deleteMany({});
    await WasteReport.deleteMany({});
    await User.deleteMany({});

    // 1. Create CLENRO Admin User
    const adminUser = await User.create({
      full_name: 'CLENRO Admin',
      email: 'admin@clenro-cdo.gov.ph',
      password_hash: 'password123',
      security_pin: '123456', // or pin if your schema uses 'pin'
      role: 'Admin'           // or 'CLENRO Admin' depending on your schema enum
    });

    // 2. Create Resident User
    const user = await User.create({
      full_name: 'Juan Dela Cruz',
      email: 'juan@example.com',
      password_hash: 'password123',
      role: 'Resident'
    });

    console.log('Mock Users Created!');

    // 3. Create Barangay Iponan
    const iponan = await Barangay.create({
      barangay_name: 'Iponan',
      city: 'Cagayan de Oro',
      population: 30000,
      contact_person: 'Barangay Captain',
      office_number: '09170000000'
    });

    console.log('Barangay Created:', iponan.barangay_name);

    // 4. Create Collection Schedules
    await CollectionSchedule.create([
      {
        barangay_id: iponan._id,
        purok_name: 'Zone 1',
        dayOfWeek: 'Monday',
        waste_type_target: 'Biodegradable',
        time_slot: '08:00 AM - 11:00 AM'
      },
      {
        barangay_id: iponan._id,
        purok_name: 'Zone 2',
        dayOfWeek: 'Wednesday',
        waste_type_target: 'Non-Biodegradable',
        time_slot: '08:00 AM - 11:00 AM'
      },
      {
        barangay_id: iponan._id,
        purok_name: 'Zone 3',
        dayOfWeek: 'Friday',
        waste_type_target: 'Recyclable',
        time_slot: '01:00 PM - 04:00 PM'
      }
    ]);

    console.log('Schedules Created!');

    // 5. Create Mock Waste Report
    await WasteReport.create({
      reporter_id: user._id,
      barangay_id: iponan._id,
      location: 'Zone 2, Iponan Main Road',
      latitude: 8.4822,
      longitude: 124.6472,
      waste_type: 'Biodegradable',
      photo_binary_data: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==',
      status: 'Pending',
      description: 'Overflowing bins near the chapel.'
    });

    console.log('Mock Waste Report Created!');
    console.log('Data successfully seeded!');
    process.exit(0);
  } catch (error) {
    console.error('Error seeding data:', error);
    process.exit(1);
  }
};

seedData();