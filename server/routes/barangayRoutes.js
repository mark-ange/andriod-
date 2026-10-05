import express from 'express';
import { getBarangays, createBarangay } from '../controllers/barangayController.js';

const router = express.Router();

router.route('/')
  .get(getBarangays)
  .post(createBarangay);

export default router;