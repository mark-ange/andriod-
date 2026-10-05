const express = require('express');
const router = express.Router();
const Report = require('../models/Report');
const { protect, adminOnly } = require('../middleware/authMiddleware');

// Mock data fallback for immediate testing without live MongoDB
const initialMockReports = [
  {
    reportId: '#CDO-2026-0412',
    title: 'Brgy. Carmen, Purok 2',
    category: 'Illegal Dump Site',
    barangay: 'Carmen',
    purokZone: 'Purok 2',
    landmark: 'Near Barangay Health Center',
    status: 'PENDING',
    description: 'Uncollected waste piled near the drainage. Causing obstruction on the sidewalk.',
    gpsCoordinates: '8.4822° N, 124.6175° E',
    unitAssigned: 'Unassigned',
    createdAt: new Date()
  },
  {
    reportId: '#CDO-2026-0411',
    title: 'Brgy. Lapasan, Zone 3',
    category: 'Overflowing Bins',
    barangay: 'Lapasan',
    purokZone: 'Zone 3',
    landmark: 'Main Market Road',
    status: 'ASSIGNED',
    description: 'Public waste bin overflowing.',
    gpsCoordinates: '8.4850° N, 124.6210° E',
    unitAssigned: 'CLENRO Truck #4',
    createdAt: new Date(Date.now() - 3600000 * 2)
  },
  {
    reportId: '#CDO-2026-0409',
    title: 'Brgy. Macasandig, Tibasak',
    category: 'Clogged Drainage Waste',
    barangay: 'Macasandig',
    purokZone: 'Tibasak',
    landmark: 'Near Riverside Chapel',
    status: 'RESOLVED',
    description: 'Waste cleared successfully by municipal team.',
    gpsCoordinates: '8.4710° N, 124.6050° E',
    unitAssigned: 'CLENRO Truck #1',
    createdAt: new Date(Date.now() - 3600000 * 24)
  }
];

let inMemoryReports = [...initialMockReports];

// @route   GET /api/reports
// @desc    Get all incident waste reports (supports filtering by barangay, status)
// @access  Public or Protected
router.get('/', async (req, res) => {
  try {
    const { barangay, status } = req.query;
    let filter = {};
    if (barangay) filter.barangay = barangay;
    if (status) filter.status = status;

    let reports = await Report.find(filter).sort({ createdAt: -1 });
    if (reports.length === 0) {
      reports = inMemoryReports.filter(r => {
        let match = true;
        if (barangay && r.barangay !== barangay) match = false;
        if (status && r.status !== status) match = false;
        return match;
      });
    }

    res.json({
      success: true,
      count: reports.length,
      data: reports
    });
  } catch (error) {
    res.json({
      success: true,
      count: inMemoryReports.length,
      data: inMemoryReports
    });
  }
});

// @route   POST /api/reports
// @desc    Create a new incident report
// @access  Public or Protected
router.post('/', async (req, res) => {
  try {
    const { title, category, barangay, purokZone, landmark, description, gpsCoordinates, photoUrl } = req.body;

    const generatedId = `#CDO-2026-${Math.floor(1000 + Math.random() * 9000)}`;

    const newReportData = {
      reportId: generatedId,
      title: title || `Brgy. ${barangay || 'Carmen'}, ${purokZone || 'Zone 1'}`,
      category: category || 'Illegal Dump Site',
      barangay: barangay || 'Carmen',
      purokZone: purokZone || '',
      landmark: landmark || '',
      status: 'PENDING',
      description: description || 'Uncollected waste report from resident.',
      gpsCoordinates: gpsCoordinates || '8.4822° N, 124.6175° E',
      unitAssigned: 'Unassigned',
      photoUrl: photoUrl || '',
      user: req.user ? req.user._id : null,
      createdAt: new Date()
    };

    try {
      const createdReport = await Report.create(newReportData);
      inMemoryReports.unshift(createdReport);
      return res.status(201).json({
        success: true,
        message: 'Report submitted successfully to CLENRO',
        data: createdReport
      });
    } catch (dbErr) {
      inMemoryReports.unshift(newReportData);
      return res.status(201).json({
        success: true,
        message: 'Report submitted successfully (in-memory mode)',
        data: newReportData
      });
    }
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

// @route   PUT /api/reports/:id/status
// @desc    Update report status and assigned unit (Admin / Desktop / App)
// @access  Public / Admin
router.put('/:id/status', async (req, res) => {
  try {
    const { status, unitAssigned } = req.body;
    const reportIdParam = req.params.id;

    try {
      let report = await Report.findOne({ $or: [{ _id: reportIdParam }, { reportId: reportIdParam }] });
      if (report) {
        if (status) report.status = status;
        if (unitAssigned) report.unitAssigned = unitAssigned;
        await report.save();
        return res.json({ success: true, message: 'Report status updated', data: report });
      }
    } catch (err) {
      // Fallback
    }

    const memReport = inMemoryReports.find(r => r.reportId === reportIdParam || r._id === reportIdParam);
    if (memReport) {
      if (status) memReport.status = status;
      if (unitAssigned) memReport.unitAssigned = unitAssigned;
      return res.json({ success: true, message: 'Report status updated', data: memReport });
    }

    res.status(404).json({ success: false, message: 'Report not found' });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
});

module.exports = router;
