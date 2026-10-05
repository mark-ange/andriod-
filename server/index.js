import express from 'express';
import cors from 'cors';
import barangayRoutes from './routes/barangayRoutes.js';

const app = express();

app.use(cors());
app.use(express.json());

app.use('/api/barangays', barangayRoutes);

app.get('/', (req, res) => {
  res.send('CityCare API Running...');
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));