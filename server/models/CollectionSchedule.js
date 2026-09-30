import mongoose from 'mongoose';

const scheduleSchema = new mongoose.Schema({
  barangay_id: { type: mongoose.Schema.Types.ObjectId, ref: 'Barangay', required: true },
  purok_name: { type: String, required: true },
  collection_days: [{ type: String, required: true }],
  time_slot: { type: String, required: true },
  waste_type_target: { type: String, required: true },
  last_updated_by: { type: mongoose.Schema.Types.ObjectId, ref: 'User' }
});

export default mongoose.model('CollectionSchedule', scheduleSchema);