# CodeClash

An online code execution platform for competitive programming practice. Write, compile, and run code in multiple languages directly in your browser.

## Features

### Currently Implemented
- **Online Code Editor** — Monaco-based editor with syntax highlighting
- **Multi-Language Support** — Java, Python, C, C++, JavaScript
- **Real-Time Execution** — Compile and run code with instant output
- **Custom Input** — Provide stdin input for your programs
- **Execution Metrics** — Track execution time for each run

### Planned Features
- User authentication
- Problem management
- Code submissions & history
- Contests & leaderboards

## Tech Stack

| Layer | Technology |
|-------|------------|
| **Frontend** | React 19, Vite, TypeScript, TailwindCSS, Monaco Editor |
| **Backend** | Spring Boot 3.2, Java 17 |
| **Code Execution** | OS processes (python3, javac, node, gcc, g++) |
| **Monorepo** | Turborepo + npm workspaces |

## Architecture

```
┌─────────────────────────┐     ┌─────────────────────────┐
│   Frontend (React)      │     │  Backend (Spring Boot)  │
│   localhost:5173        │────▶│  localhost:4000         │
├─────────────────────────┤     ├─────────────────────────┤
│ • Monaco Code Editor    │     │ • POST /api/execute     │
│ • Language Selector     │     │ • GET  /api/health      │
│ • Input/Output Panel    │     │                         │
└─────────────────────────┘     └─────────────────────────┘
                                          │
                                          ▼
                                ┌─────────────────────────┐
                                │   Code Execution        │
                                ├─────────────────────────┤
                                │ 1. Create temp file     │
                                │ 2. Compile (if needed)  │
                                │ 3. Execute process      │
                                │ 4. Return output        │
                                │ 5. Cleanup temp files   │
                                └─────────────────────────┘
```

## Project Structure

```
codeclash/
├── apps/
│   ├── frontend/                 # React + Vite + TailwindCSS
│   │   └── src/
│   │       ├── components/
│   │       │   ├── CodeExecution.tsx
│   │       │   ├── CodeEditor.tsx
│   │       │   ├── InputOutput.tsx
│   │       │   └── LanguageSelector.tsx
│   │       └── App.tsx
│   │
│   └── backend-springboot/       # Spring Boot API
│       └── src/main/java/com/codeclash/
│           ├── controller/
│           ├── service/
│           ├── model/
│           └── repository/
│
├── packages/                     # Shared packages
│   ├── db/                       # Prisma schema
│   ├── ui/                       # Shared UI components
│   └── typescript-config/
│
├── package.json
└── turbo.json
```

## Getting Started

### Prerequisites
- Node.js >= 18
- Java 17
- Maven 3.8+
- Compilers: `python3`, `gcc`, `g++` (for code execution)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/sahilghanmode/codeclash.git
   cd codeclash
   ```

2. **Install frontend dependencies**
   ```bash
   cd apps/frontend
   npm install
   ```

3. **Start the backend** (in one terminal)
   ```bash
   cd apps/backend-springboot
   mvn spring-boot:run
   ```
   Backend runs on `http://localhost:4000`

4. **Start the frontend** (in another terminal)
   ```bash
   cd apps/frontend
   npm run dev
   ```
   Frontend runs on `http://localhost:5173`

5. **Open your browser** and go to `http://localhost:5173`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/execute` | Execute code |

### Execute Code Request
```json
{
  "code": "print('Hello, World!')",
  "language": "python",
  "input": ""
}
```

### Execute Code Response
```json
{
  "success": true,
  "output": "Hello, World!",
  "executionTime": 42,
  "timestamp": "2024-12-16T15:00:00"
}
```

## Supported Languages

| Language | Compiler/Interpreter |
|----------|---------------------|
| Java | `javac` + `java` |
| Python | `python3` |
| JavaScript | `node` |
| C | `gcc` |
| C++ | `g++` |

## Configuration

Backend configuration in `apps/backend-springboot/src/main/resources/application.properties`:

```properties
server.port=4000
```

## Security Considerations

> **Warning:** This project executes arbitrary user code on the server. For production deployment, consider:
> - Sandboxed execution (Docker containers)
> - Rate limiting
> - Authentication on code execution endpoints
> - Resource limits (CPU, memory, time)

## License

MIT

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
