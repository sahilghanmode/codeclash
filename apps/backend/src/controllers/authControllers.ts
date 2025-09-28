import { PrismaClient } from '@prisma/client';
import nodemailer from 'nodemailer';

const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'thorsthorkel@gmail.com',
        pass: 'zksl gett jana gtue'
    }
});

const prisma = new PrismaClient();

export const signupController = async (req: any, res: any) => {
    try {
        const { username, email, password } = req.body;

        const existingUser=await prisma.user.findUnique({
            where:{email}
        })
        if(existingUser){
            return res.status(400).json({success:false, message:"User already exists"});
        }

        const user = await prisma.user.create({
            data: { username, email, password }
        });

        return res.status(201).json({success:true, message:"User created successfully", user});

    } catch (error) {
        console.log(error)
        return res.status(500).json({error: "Internal Server Error"});
    }
}