# Sionna Visualizer

> **The world's first open-source web dashboard for NVIDIA Sionna 6G wireless simulation results.**

Turn raw Sionna output into publication-ready charts, share results via a single URL, and collaborate with researchers worldwide — all from your browser.

[![Live Demo](https://img.shields.io/badge/Live%20Demo-sionna--visualizer.vercel.app-black?style=for-the-badge&logo=vercel)](https://sionna-visualizer.vercel.app)
[![GitHub Stars](https://img.shields.io/github/stars/Vinu2111/sionna-visualizer?style=for-the-badge&logo=github)](https://github.com/Vinu2111/sionna-visualizer)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](https://opensource.org/licenses/MIT)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen?style=for-the-badge)](https://github.com/Vinu2111/sionna-visualizer/pulls)
[![NVIDIA Sionna](https://img.shields.io/badge/NVIDIA-Sionna%206G-76B900?style=for-the-badge&logo=nvidia)](https://github.com/NVlabs/sionna)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-DD0031?style=for-the-badge&logo=angular)](https://angular.io)
[![FastAPI](https://img.shields.io/badge/FastAPI-Python%203.10-009688?style=for-the-badge&logo=fastapi)](https://fastapi.tiangolo.com)

---

## What Is Sionna Visualizer?

**The problem:** NVIDIA Sionna is the most powerful open-source 6G simulation library (200,000+ downloads), but every researcher has to write Python scripts, manually generate matplotlib charts, and email CSV files to share results. There is no visual, collaborative, web-based way to work with Sionna simulations.

**The solution:** Sionna Visualizer wraps Sionna in a full-stack web dashboard. Run AWGN, beam pattern, path loss, channel capacity, and channel model simulations from a browser UI. See results as interactive Chart.js charts. Export to PNG/CSV/JSON for publications. Share any simulation via a single public URL. Every simulation is saved permanently with full performance metadata for reproducibility.

**Who it's for:** 6G researchers, wireless communications students, telecom engineers, and anyone working with NVIDIA Sionna who wants a faster, more visual workflow.

**What makes it unique:**
1. **Publication Pipeline** — IEEE/Nature-ready figure export, LaTeX parameter tables, reproducibility packages
2. **Platform Neutrality** — CDL/TDL channel models (3GPP standard), SigMF signal import, multi-simulator comparison
3. **AI-Powered Analysis** — Natural language simulation with Claude AI, BER anomaly detection, THz atmospheric modeling

---

## Live Demo

| Layer | URL |
|---|---|
| **Frontend** | [sionna-visualizer.vercel.app](https://sionna-visualizer.vercel.app) |
| **Backend API** | [sionna-visualizer-production-50ae.up.railway.app/api](https://sionna-visualizer-production-50ae.up.railway.app/api) |
| **API Docs** | [sionna-visualizer-production-50ae.up.railway.app/api-docs](https://sionna-visualizer-production-50ae.up.railway.app/api-docs) |
| **Health Check** | [sionna-visualizer-production-50ae.up.railway.app/actuator/health](https://sionna-visualizer-production-50ae.up.railway.app/actuator/health) |

---

## Features — 61 Features Across 5 Phases

### 🔬 Core Simulation Engine (Phase 1 & 2)
| Feature | Description |
|---|---|
| **AWGN BER Simulation** | BPSK / QPSK / 16QAM / 64QAM — theoretical + Monte Carlo curves |
| **Beam Pattern Visualization** | ULA antenna polar chart — 8/16/32/64 antennas, 28/39/60/77 GHz |
| **Modulation Comparison** | All 4 modulations on one chart with crossover analysis |
| **Channel Capacity** | Shannon theorem C = B × log₂(1 + SNR), interactive multi-bandwidth |
| **Path Loss Breakdown** | Per-ray FSPL — bar chart + scatter + delay chart + data table |
| **Simulation Comparison** | Select 2 saved runs, overlay charts, compute winner |
| **Time Estimate** | Complexity label (Fast/Medium/Slow/Heavy) + tips before running |
| **Custom Colormaps** | 7 palettes — Viridis, Grayscale, colorblind-safe Publication |
| **Ray Direction Analysis** | Azimuth/elevation ray visualization for MIMO channels |
| **UE Trajectory** | Mobile user equipment path visualization with handover analysis |
| **SINR Beamforming** | Steering angle optimization with interference mapping |
| **Measurement Overlay** | Calibration overlay with real-world measurement comparison |

### 📊 Platform Features (Phase 2)
| Feature | Description |
|---|---|
| **JWT Authentication** | Login + register + protected routes via Spring Security |
| **Simulation History** | All past runs searchable, viewable, re-shareable |
| **Shareable Public Links** | One URL per simulation — no login needed for viewer |
| **Performance Metadata** | Duration (ms), memory (MB), CPU/GPU, Sionna version per run |
| **Public REST API** | 7 endpoints, API key system, 100 req/day per key |
| **PNG / CSV / JSON Export** | Export any chart or dataset in 3 formats |
| **Bulk Export** | Export all simulations from history in one action |
| **API Docs Page** | `/api-docs` with curl + Python examples |
| **Rate Limiting** | 10 simulations/min per IP via Bucket4j |
| **Circuit Breaker** | Python bridge failure handled gracefully via Resilience4j |

### 📄 Publication Pipeline (Sprint 3A)
| Feature | Description |
|---|---|
| **IEEE/Nature Figure Wizard** | Export charts with journal-specific formatting, DPI, and font requirements |
| **LaTeX Parameter Table Generator** | Auto-generate LaTeX tables from simulation parameters for papers |
| **Reproducibility Package** | Download a complete ZIP with data, config, and scripts to reproduce results |

### 🔀 Platform Neutrality (Sprint 3B)
| Feature | Description |
|---|---|
| **CDL/TDL Channel Models** | 3GPP-standard CDL-A through CDL-E and TDL channel model simulations |
| **SigMF Signal Import** | Import and analyze SigMF-compliant signal recordings |
| **Multi-Simulator Comparison** | Upload CSV results from MATLAB/ns-3/other simulators, compare with Sionna |

### 👥 Community Features (Sprint 3C)
| Feature | Description |
|---|---|
| **Public Results Gallery** | Browse, search, and fork community-published simulation results |
| **Experiment Tagging + Search** | Organize simulations with tags, full-text search, batch operations |
| **Collaborative Team Workspace** | Create teams, invite members, share simulations, comment & annotate |

### 🇮🇳 Indian Institutional (Sprint 3D)
| Feature | Description |
|---|---|
| **TTDF Progress Dashboard** | Telecom Technology Development Fund milestone tracking with KPIs |
| **Bharat 6G Alliance Reporting** | Organization registration, PoC management, quarterly compliance reports |

### 🤖 AI Features (Sprint 3E)
| Feature | Description |
|---|---|
| **Natural Language Simulation** | Describe a simulation in plain English → Claude AI converts to parameters → runs it |
| **AI Anomaly Detection on BER** | Physics-based + AI analysis of BER curves to detect errors automatically |
| **THz Atmospheric Sliders** | ITU-R P.676/P.838 physics model for THz link budget with interactive sliders |

---

## Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                         Browser                               │
│               Angular 17 + Angular Material                   │
│          Chart.js · D3.js · ng2-charts · JWT Auth             │
└──────────────────────┬────────────────────────────────────────┘
                       │ HTTP/REST
┌──────────────────────▼────────────────────────────────────────┐
│                   Java Spring Boot 3                           │
│          REST API · Spring Security · JWT · JPA                │
│     SimulationService · Cache · Circuit Breaker · Actuator     │
│     Claude AI · FigureExport · TTDF · Gallery · Workspace      │
└───────────┬───────────────────────────┬───────────────────────┘
            │ JPA/Hibernate             │ HTTP
┌───────────▼───────────┐   ┌───────────▼───────────────────────┐
│      PostgreSQL        │   │     Python 3.10 + FastAPI          │
│  19 Flyway Migrations  │   │   NVIDIA Sionna SDK Bridge         │
│  All Simulation Data   │   │   AWGN · Beam · PathLoss · CDL/TDL │
│  Users · API Keys      │   │   THz Physics · SigMF · Colormaps  │
└────────────────────────┘   └───────────────────────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | Angular 17, Angular Material, Chart.js, D3.js, ng2-charts |
| **Backend** | Java 17, Spring Boot 3, Spring Security, JWT, Hibernate, Flyway |
| **Bridge** | Python 3.10, FastAPI, NVIDIA Sionna SDK, NumPy, SciPy |
| **AI** | Anthropic Claude API (natural language + anomaly detection) |
| **Database** | PostgreSQL with 19 Flyway migrations |
| **Deployment** | Railway (backend + Python + DB), Vercel (frontend) |
| **DevOps** | Docker, docker-compose, GitHub auto-deploy |

---

## Getting Started

### Prerequisites
- Node.js 18+
- Java 17+
- Python 3.10+
- PostgreSQL 14+
- Docker (optional, recommended)

### Quick Start with Docker

```bash
git clone https://github.com/Vinu2111/sionna-visualizer.git
cd sionna-visualizer
cp .env.example .env
# Edit .env with your actual values
docker-compose up
```

Open `http://localhost:4200`. All 4 services start automatically.

### Manual Setup

#### 1. Database
```bash
# Create PostgreSQL database
createdb sionna_visualizer
# Or via psql:
psql -U postgres -c "CREATE DATABASE sionna_visualizer;"
```

#### 2. Backend (Spring Boot)
```bash
cd backend
# Set environment variables (see Environment Variables section)
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/sionna_visualizer
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_password
export JWT_SECRET=$(openssl rand -hex 32)
./mvnw spring-boot:run
```
Backend runs at `http://localhost:8080`

#### 3. Python Bridge (FastAPI)
```bash
cd python-bridge
pip install -r requirements.txt
uvicorn main:app --port 8001
```
Bridge runs at `http://localhost:8001`

#### 4. Frontend (Angular)
```bash
cd frontend
npm install
ng serve
```
Frontend runs at `http://localhost:4200`

---

## API Documentation

Full documentation available at [`/api-docs`](https://sionna-visualizer-production-50ae.up.railway.app/api-docs)

### Example API Calls

**Run an AWGN simulation:**
```bash
curl -X POST https://sionna-visualizer-production-50ae.up.railway.app/api/public/simulate \
  -H "X-API-Key: your-api-key" \
  -H "Content-Type: application/json" \
  -d '{"modulationType": "QPSK", "snrMin": 0, "snrMax": 20, "snrSteps": 21}'
```

**Get simulation estimate (no API key needed):**
```bash
curl -X POST https://sionna-visualizer-production-50ae.up.railway.app/api/simulations/estimate \
  -H "Content-Type: application/json" \
  -d '{"simulation_type": "AWGN", "parameters": {"modulation": "QPSK", "snr_steps": 21}}'
```

**List available colormaps (no API key needed):**
```bash
curl https://sionna-visualizer-production-50ae.up.railway.app/api/simulations/colormaps
```

**Python SDK example:**
```python
import requests

response = requests.post(
    "https://sionna-visualizer-production-50ae.up.railway.app/api/public/simulate",
    headers={"X-API-Key": "your-api-key"},
    json={"modulationType": "QPSK", "snrMin": 0, "snrMax": 20, "snrSteps": 21}
)
result = response.json()
print(f"BER at 10 dB: {result['simulatedBer'][10]:.2e}")
```

---

## Environment Variables

| Variable | Description | Example |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC connection string | `jdbc:postgresql://localhost:5432/sionna_visualizer` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `your_secure_password` |
| `JWT_SECRET` | Secret key for JWT signing (64+ chars) | `openssl rand -hex 32` |
| `JWT_EXPIRATION` | JWT token expiry in milliseconds | `86400000` (24 hours) |
| `PYTHON_BRIDGE_URL` | FastAPI bridge URL | `http://localhost:8001/simulate/demo` |
| `ANTHROPIC_API_KEY` | Claude AI API key for NL simulation | `sk-ant-...` |
| `CORS_ALLOWED_ORIGIN` | Frontend URL for CORS | `https://sionna-visualizer.vercel.app` |

See `.env.example` for the complete template.

---

## Deployment

### Deploy Backend to Railway

1. Create a new Railway project at [railway.app](https://railway.app)
2. Add a **PostgreSQL** plugin — Railway provisions the database automatically
3. Connect your GitHub repo → Railway auto-detects Spring Boot
4. Set environment variables in Railway dashboard:
   - `SPRING_DATASOURCE_URL` — from Railway PostgreSQL plugin
   - `SPRING_DATASOURCE_USERNAME` — from Railway PostgreSQL plugin
   - `SPRING_DATASOURCE_PASSWORD` — from Railway PostgreSQL plugin
   - `JWT_SECRET` — generate a strong random string
   - `PYTHON_BRIDGE_URL` — your Python bridge Railway URL
   - `ANTHROPIC_API_KEY` — your Claude API key
   - `CORS_ALLOWED_ORIGIN` — your Vercel frontend URL
5. Deploy — Railway builds and runs `./mvnw spring-boot:run`

### Deploy Frontend to Vercel

1. Import repo at [vercel.com](https://vercel.com)
2. Set **Framework Preset** to Angular
3. Set **Output Directory** to `dist/sionna-visualizer/browser`
4. Deploy — Vercel auto-builds on every push

`vercel.json` is already configured for SPA routing.

### Deploy Python Bridge to Railway

1. Add a new service in your Railway project
2. Point to the `python-bridge/` directory
3. Railway auto-detects the `Dockerfile`
4. Set `PORT=8001` environment variable
5. Deploy

---

## Project Structure

```
sionna-visualizer/
├── frontend/                    # Angular 17 application
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/      # Reusable UI components
│   │   │   ├── pages/           # Route-level page components
│   │   │   ├── services/        # HTTP services and state
│   │   │   ├── models/          # TypeScript interfaces
│   │   │   ├── guards/          # Route guards (auth)
│   │   │   └── interceptors/    # HTTP interceptors (JWT)
│   │   └── environments/        # Dev/prod API URLs
│   ├── angular.json
│   └── package.json
│
├── backend/                     # Spring Boot 3 application
│   ├── src/main/java/com/sionnavisualizer/
│   │   ├── config/              # Security, CORS config
│   │   ├── controller/          # REST API controllers
│   │   ├── dto/                 # Request/response DTOs
│   │   ├── model/               # JPA entity models
│   │   ├── repository/          # Spring Data JPA repos
│   │   ├── service/             # Business logic services
│   │   ├── filter/              # JWT, rate limiting, security
│   │   ├── exception/           # Global exception handlers
│   │   └── util/                # JWT utilities
│   └── src/main/resources/
│       ├── application.yml      # Spring config (env vars)
│       └── db/migration/        # 19 Flyway SQL migrations
│
├── python-bridge/               # FastAPI simulation bridge
│   ├── main.py                  # FastAPI app + all routes
│   ├── models.py                # Pydantic request/response models
│   ├── sionna_runner.py         # Core Sionna simulation engine
│   ├── beam_pattern.py          # ULA beam pattern calculations
│   ├── channel_capacity.py      # Shannon capacity calculations
│   ├── channel_model_simulator.py # CDL/TDL 3GPP models
│   ├── thz_atmospheric_calculator.py # ITU-R P.676/P.838 physics
│   ├── sigmf_analyzer.py        # SigMF signal analysis
│   ├── requirements.txt
│   └── Dockerfile
│
├── docker-compose.yml           # Full-stack local development
├── .env.example                 # Environment variable template
└── README.md
```

---

## Contributing

We welcome contributions! Here's how:

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feat/amazing-feature`
3. **Make** your changes
4. **Commit** with conventional commits: `git commit -m "feat: add amazing feature"`
5. **Push** to your branch: `git push origin feat/amazing-feature`
6. **Open** a Pull Request

### Commit Convention
- `feat:` — new feature
- `fix:` — bug fix
- `docs:` — documentation
- `chore:` — maintenance
- `refactor:` — code improvement
- `perf:` — performance improvement

---

## Built By

**Vinayak Gote** — Java Backend Developer at Wipro, Pune, India

Computer Engineering, SPPU 2024 · CGPA 8.56 · 2nd Rank in B.E.

NVIDIA AI Aerial / 6G Developer Program Member · AWS Certified Cloud Practitioner

| | |
|---|---|
| 🐙 **GitHub** | [github.com/Vinu2111](https://github.com/Vinu2111) |
| 💼 **LinkedIn** | [linkedin.com/in/vinayakgote](https://linkedin.com/in/vinayakgote) |
| 🌐 **Blog** | [vinayak6g.hashnode.dev](https://vinayak6g.hashnode.dev) |
| 🐦 **Twitter** | [@gote_vinayak](https://twitter.com/gote_vinayak) |

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Acknowledgements

- **NVIDIA Sionna Team** — for building the world's best open-source 6G simulation library
- **Merlin Nimier-David** — Sionna core maintainer, for guidance and support
- **NVIDIA AI Aerial Developer Program** — for the 6G developer community
- **Anthropic** — for Claude AI powering natural language simulation

---

*Built with NVIDIA Sionna · Deployed on Railway + Vercel · 61 features · 2026*
