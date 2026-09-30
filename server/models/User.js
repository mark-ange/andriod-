import mongoose from 'mongoose';

const userSchema = new mongoose.Schema({
  full_name: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  password_hash: { type: String, required: true },
  role: { type: String, enum: ['Resident', 'Admin'], default: 'Resident' },
  barangay_id: { type: mongoose.Schema.Types.ObjectId, ref: 'Barangay' },
  contact_number: { type: String },
  created_at: { type: Date, default: Date.now }
});

export default mongoose.model('User', userSchema);