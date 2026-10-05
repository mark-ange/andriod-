import Barangay from '../models/Barangay.js';

export const getBarangays = async (req, res) => {
  try {
    const barangays = await Barangay.find();
    res.status(200).json({ success: true, data: barangays });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
};

export const createBarangay = async (req, res) => {
  try {
    const barangay = await Barangay.create(req.body);
    res.status(201).json({ success: true, data: barangay });
  } catch (error) {
    res.status(400).json({ success: false, message: error.message });
  }
};