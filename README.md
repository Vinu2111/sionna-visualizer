# Sionna Visualizer

> A full-stack web dashboard that makes NVIDIA Sionna 6G simulation 
> results visual, interactive, and shareable for researchers and 
> developers worldwide.

![Java 17](https://img.shields.io/badge/Java-17-blue)
![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3-green)
![Angular 17](https://img.shields.io/badge/Angular-17-red)
![Python 3.10](https://img.shields.io/badge/Python-3.10-blue)
![FastAPI](https://img.shields.io/badge/FastAPI-teal)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue)
![License](https://img.shields.io/badge/License-MIT-blue)
![NVIDIA Sionna](https://img.shields.io/badge/NVIDIA-Sionna-76B900)

## What Is This?

NVIDIA Sionna is the world's most downloaded 6G research library. It outputs raw Python data. This dashboard makes those results visual, interactive, and shareable.

## Features

- BER vs SNR interactive chart from real Sionna simulations
- JWT authentication — secure login and registration
- Simulation history — every run saved to PostgreSQL
- Shareable links — share any simulation result via public URL
- Three-tier architecture — Angular + Java + Python + PostgreSQL

## Architecture

```text
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│   Angular 17    │────▶│  Java Spring Boot │────▶│  Python FastAPI  │
│   Frontend      │◀────│  REST API :8080   │◀────│  Sionna :8001   │
└─────────────────┘     └──────────────────┘     └─────────────────┘
                                │
                         ┌──────▼──────┐
                         │ PostgreSQL  │
                         │  Database   │
                         └─────────────┘
```

## Quick Start (Local)

1. Clone the repo
2. Copy `.env.example` to `.env` and fill in values
3. Run: `docker-compose up`
4. Open: `http://localhost:4200`

## Manual Setup (Without Docker)

### Prerequisites

- Java 17
- Python 3.10
- Node.js 20+
- PostgreSQL database running locally

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Python Bridge

```bash
cd python-bridge
pip install -r requirements.txt
uvicorn main:app --reload --port 8001
```

### Frontend

```bash
cd frontend
npm install
ng serve
```

## Environment Variables

```env
# ─── Java Spring Boot Backend ───────────────────────
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/sionna_visualizer
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret_minimum_32_characters_long
JWT_EXPIRATION=86400000
PYTHON_BRIDGE_URL=http://localhost:8001/simulate/demo

# ─── Python FastAPI Bridge ───────────────────────────
PORT=8001

# ─── Frontend ────────────────────────────────────────
# Set this in frontend/src/environments/environment.prod.ts
# apiUrl: '/api'  ← for same-domain deployment
# apiUrl: 'https://your-backend-url.railway.app'  ← for Railway
```

## Built By

Vinayak — Java Backend Developer, Pune, India
NVIDIA AI Aerial / 6G Developer Program Member
Hashnode: vinayak6g.hashnode.dev
Twitter: @gote_vinayak

## License

MIT License — free to use, modify, and distribute.
