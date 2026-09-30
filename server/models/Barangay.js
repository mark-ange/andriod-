import mongoose from 'mongoose';

const barangaySchema = new mongoose.Schema({
  barangay_name: { type: String, required: true },
  city: { type: String, default: 'Cagayan de Oro' },
  contact_person: { type: String, required: true },
  office_number: { type: String, required: true }
});

export default mongoose.model('Barangay', barangaySchema);