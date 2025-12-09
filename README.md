CodeClash

CodeClash is a modern, full-stack competitive programming platform inspired by Codeforces. Built with MERN stack, PostgreSQL, Prisma, and Turborepo, it allows users to practice, compete, and improve their algorithmic skills through coding contests and problem-solving challenges.

Features

User Authentication – Secure signup, login, and JWT-based sessions.

Problem Management – Add, edit, and categorize coding problems.

Contests & Submissions – Create contests, submit solutions, and track progress.

Leaderboard & Rankings – Real-time scoring and ranking of participants.

Admin Panel – Manage users, problems, and contests.

Reusable Architecture – Built with Turborepo for monorepo management, allowing scalable modular development.

Database Powered by PostgreSQL & Prisma – Efficient, type-safe ORM for data management.

Tech Stack

Frontend: React.js (optional Next.js for SSR)

Backend: Node.js + Express + TypeScript

Database: PostgreSQL

ORM: Prisma

Monorepo Management: Turborepo

Authentication: JWT + Bcrypt

Getting Started

Clone the repository:

git clone https://github.com/your-username/codeclash.git


Install dependencies:

npm install


Setup PostgreSQL (Docker recommended).

Configure .env with your DATABASE_URL.

Run migrations and generate Prisma client:

cd packages/db
npm run migrate
npm run dev


Start the backend and frontend servers:

npm run dev

CodeClash is ideal for developers looking to practice competitive programming, organize coding contests, or learn full-stack development with modern tools.
