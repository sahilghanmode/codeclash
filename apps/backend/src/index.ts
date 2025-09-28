import express from 'express';
import cookieParser from 'cookie-parser';
import cors from 'cors';
import authRoutes from './routes/authRoutes';

const app: express.Application = express();

const port: number = 3000;

app.use(express.json());
app.use(cookieParser());
app.use(cors());

app.get('/', (_req, _res) => {
    _res.send("TypeScript With Express");
});

app.use('/api/auth',authRoutes);


app.listen(port, () => {
    console.log(`TypeScript with Express 
         http://localhost:${port}/`);
});

