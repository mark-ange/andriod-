import mongoose from 'mongoose';

const wasteReportSchema = new mongoose.Schema({
  reporter_id: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  barangay_id: { type: mongoose.Schema.Types.ObjectId, ref: 'Barangay', required: true },
  waste_type: { type: String, required: true },
  description: { type: String, required: true },
  latitude: { type: Number, required: true },
  longitude: { type: Number, required: true },
  photo_binary_data: { type: String, required: true },
  status: { type: String, enum: ['Pending', 'Assigned', 'Resolved'], default: 'Pending' }
}, { timestamps: { createdAt: 'created_at', updatedAt: 'updated_at' } });

export default mongoose.model('WasteReport', wasteReportSchema);