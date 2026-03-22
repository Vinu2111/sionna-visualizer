# Sionna Visualizer

**Make NVIDIA Sionna 6G simulation results visual, interactive, and shareable.**

A full-stack research dashboard that turns raw Sionna output into publication-ready charts — with one click.

[![Live Demo](https://img.shields.io/badge/Live%20Demo-Vercel-black?style=for-the-badge&logo=vercel)](https://your-vercel-url.vercel.app)
[![GitHub](https://img.shields.io/badge/GitHub-Public-181717?style=for-the-badge&logo=github)](https://github.com/Vinu2111/sionna-visualizer)
[![NVIDIA Sionna](https://img.shields.io/badge/NVIDIA-Sionna%206G-76B900?style=for-the-badge&logo=nvidia)](https://github.com/NVlabs/sionna)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17-DD0031?style=for-the-badge&logo=angular)](https://angular.io)
[![FastAPI](https://img.shields.io/badge/FastAPI-Python%203.10-009688?style=for-the-badge&logo=fastapi)](https://fastapi.tiangolo.com)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Managed-4169E1?style=for-the-badge&logo=postgresql)](https://www.postgresql.org)
[![Railway](https://img.shields.io/badge/Railway-Deployed-0B0D0E?style=for-the-badge&logo=railway)](https://railway.app)
[![AWS CCP](https://img.shields.io/badge/AWS-Cloud%20Practitioner-FF9900?style=for-the-badge&logo=amazonaws)](https://aws.amazon.com/certification/)
[![NVIDIA Aerial](https://img.shields.io/badge/NVIDIA-AI%20Aerial%206G%20Program-76B900?style=for-the-badge&logo=nvidia)](https://developer.nvidia.com/aerial-sdk)

---

## Live Demo

| Layer | URL |
|---|---|
| **Frontend** | [your-vercel-url.vercel.app](https://your-vercel-url.vercel.app) |
| **Backend API** | [your-railway-url.railway.app/api](https://your-railway-url.railway.app/api) |
| **API Docs** | [your-railway-url.railway.app/api-docs](https://your-railway-url.railway.app/api-docs) |
| **Health Check** | [your-railway-url.railway.app/actuator/health](https://your-railway-url.railway.app/actuator/health) |

---

## What Is This

Sionna Visualizer is a web dashboard that wraps NVIDIA Sionna — the open-source 6G simulation library — in a browser interface that any researcher can use without writing code. Run AWGN, beam pattern, path loss, and channel capacity simulations, see results as interactive charts, export to PNG/CSV/JSON, and share results via a public URL. Every simulation is saved permanently with full performance metadata so your work is always reproducible.

---

## Screenshots

### Dashboard — BER vs SNR Simulation
![Dashboard BER Simulation](screenshots/dashboard-ber.png)

### Beam Pattern Visualization
![Beam Pattern](screenshots/beam-pattern.png)

### Path Loss Breakdown — 3 Charts
![Path Loss](screenshots/path-loss.png)

### Channel capacity — Shannon Theorem
![Channel Capacity](screenshots/channel-capacity.png)

### Simulation History
![History](screenshots/history.png)

### Shareable Link — Public View
![Share View](screenshots/share-view.png)

### Custom Colormap Selector
![Colormap Selector](screenshots/colormap-selector.png)

---

## Features

### Simulations
| Feature | Description |
|---|---|
| **AWGN BER Simulation** | BPSK / QPSK / 16QAM / 64QAM — theoretical + Monte Carlo curves |
| **Beam Pattern Visualization** | ULA antenna polar chart — 8/16/32/64 antennas, 28/39/60/77 GHz |
| **Modulation Comparison** | All 4 modulations on one chart with crossover points |
| **Channel Capacity** | Shannon theorem C = B × log₂(1 + SNR), 4 bandwidth curves |
| **Path Loss Breakdown** | Per-ray FSPL — bar chart + scatter + delay chart + data table |
| **Simulation Comparison Tool** | Select 2 saved runs, overlay charts, compute winner |
| **Time Estimate Before Running** | Complexity label (Fast/Medium/Slow/Heavy) + actionable tips |
| **Custom Colormaps** | 7 palettes including Viridis, Grayscale, and colorblind-safe Publication |

### Platform
| Feature | Description |
|---|---|
| **JWT Authentication** | Login + register + protected routes via Spring Security |
| **Simulation History** | All past runs searchable, viewable, re-shareable |
| **Shareable Public Links** | One URL per simulation — no login needed for recipient |
| **Performance Metadata** | Duration (ms), memory (MB), CPU/GPU, Sionna version per run |
| **Public REST API** | 7 endpoints, API key system, 100 req/day per key |
| **PNG / CSV / JSON Export** | Export any chart or dataset in 3 formats |
| **Bulk Export** | Export all simulations from history in one action |
| **API Docs Page** | `/api-docs` with curl + Python examples |

### Infrastructure
| Feature | Description |
|---|---|
| **Railway + Vercel Deployment** | Auto-deploys on every GitHub push |
| **Docker** | `docker-compose up` starts all 4 services locally |
| **Simulation Caching** | ConcurrentHashMap cache — same parameters return instantly |
| **Railway Warmup** | `/warmup` endpoint keeps Python bridge alive, no cold starts |
| **Rate Limiting** | 10 simulations/min per IP |
| **Circuit Breaker** | Python bridge failure handled gracefully |
| **Security Headers** | CORS locked, input validation on all endpoints |
| **Health Endpoints** | Spring Actuator with `python-bridge: warm/cold` indicator |

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Browser                              │
│              Angular 17 + Angular Material                  │
│         Chart.js · D3.js · ng2-charts · JWT Auth            │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP/REST
┌─────────────────────▼───────────────────────────────────────┐
│                   Java Spring Boot 3                        │
│         REST API · Spring Security · JWT · JPA              │
│    SimulationService · Cache · Circuit Breaker · Actuator   │
└──────────┬──────────────────────────┬────────────────────────┘
           │ JPA/Hibernate            │ HTTP
┌──────────▼──────────┐   ┌──────────▼──────────────────────┐
│     PostgreSQL      │   │     Python 3.10 + FastAPI        │
│  Simulation Results │   │   NVIDIA Sionna SDK Bridge       │
│  Performance Data   │   │   AWGN · Beam · PathLoss · FSPL  │
│  API Keys · Users   │   │   tracemalloc · ColormapService  │
└─────────────────────┘   └──────────────────────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | Angular 17, Angular Material, Chart.js, D3.js, ng2-charts |
| **Backend** | Java 17, Spring Boot 3, Spring Security, JWT, Hibernate |
| **Bridge** | Python 3.10, FastAPI, NVIDIA Sionna SDK, tracemalloc |
| **Database** | PostgreSQL (managed on Railway) |
| **Deployment** | Railway (backend + Python + DB), Vercel (frontend) |
| **DevOps** | Docker, docker-compose, GitHub Actions auto-deploy |

---

## Quick Start

**Option 1 — Docker (recommended)**

```bash
git clone https://github.com/Vinu2111/sionna-visualizer.git
cd sionna-visualizer
docker-compose up
```

Open `http://localhost:4200`. All 4 services start automatically.

**Option 2 — Manual**

```bash
# Terminal 1 — Python bridge
cd python-bridge && pip install -r requirements.txt && uvicorn main:app --port 8001

# Terminal 2 — Java backend
cd backend && ./mvnw spring-boot:run

# Terminal 3 — Angular frontend
cd frontend && npm install && ng serve
```

---

## Public REST API

No login needed. Get an API key from the dashboard → Settings → API Keys.

**Run an AWGN simulation**
```bash
curl -X POST https://your-railway-url.railway.app/api/public/simulate \
  -H "X-API-Key: your-api-key" \
  -H "Content-Type: application/json" \
  -d '{"modulationType": "QPSK", "snrMin": 0, "snrMax": 20, "snrSteps": 21}'
```

**Get simulation estimate (no API key needed)**
```bash
curl -X POST https://your-railway-url.railway.app/api/simulations/estimate \
  -H "Content-Type: application/json" \
  -d '{"simulation_type": "AWGN", "parameters": {"modulation": "QPSK", "snr_steps": 21}}'
```

**List available colormaps (no API key needed)**
```bash
curl https://your-railway-url.railway.app/api/simulations/colormaps
```

Full documentation with Python examples: `/api-docs`

---

## Project Status

| Phase | Status | Days | Features |
|---|---|---|---|
| Phase 1 — Foundation | ✅ Complete | Days 1–10 | Landing, Auth, Dashboard, DB, Deploy |
| Phase 2 Sprint 1 — Research Features | ✅ Complete | Days 11–15 | Real Sionna math, Beam, Comparison |
| Phase 2 Sprint 2 — Platform | ✅ Complete | Days 16–19 | API, Export, Capacity, Hardening |
| Phase 2A — Forum Features | ✅ Complete | Days 20–24 | Metadata, PathLoss, Estimate, Colormap |
| Phase 2B — Advanced | 🔄 In Progress | Days 25+ | Ray direction, Coverage map, UE trajectory |

**47 features built · 24 days · 2 hours/day**

---

## Built By

**Vinayak Gote** — Java Backend Developer, Wipro, Pune, India

Computer Engineering, SPPU 2024 · CGPA 8.56 · 2nd Rank in B.E.

NVIDIA AI Aerial / 6G Developer Program member · AWS Certified Cloud Practitioner

Building in public — daily Hashnode articles + Twitter updates.

| | |
|---|---|
| 🌐 **Blog** | [vinayak6g.hashnode.dev](https://vinayak6g.hashnode.dev) |
| 🐦 **Twitter** | [@gote_vinayak](https://twitter.com/gote_vinayak) |
| 💼 **LinkedIn** | [linkedin.com/in/vinayakgote](https://linkedin.com/in/vinayakgote) |
| 🐙 **GitHub** | [github.com/Vinu2111](https://github.com/Vinu2111) |

---

## Community

- 📋 [NVIDIA Aerial Forums post](https://forums.developer.nvidia.com/aerial) — live
- 💬 [Sionna GitHub Show and Tell](https://github.com/NVlabs/sionna/discussions) — live
- 📝 [19+ Hashnode articles](https://vinayak6g.hashnode.dev) — build-in-public series

---

*Built with NVIDIA Sionna · Deployed on Railway + Vercel · 2026*
