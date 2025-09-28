import {Router} from 'express';
import { signupController } from '../controllers/authControllers';


const authRoutes = Router();

authRoutes.post('/signup',signupController)


export default authRoutes;
