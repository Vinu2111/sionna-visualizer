# Antigravity Prompts

## Session 1: Project Skeleton — Initialize All Three Layers
```text
I am building "Sionna Visualizer" — a full-stack web dashboard that makes
NVIDIA Sionna 6G simulation results visual, interactive, and shareable.

TECH STACK:
- Frontend: Angular 17 + Angular Material + Chart.js + D3.js
- Backend: Java 17 + Spring Boot 3 REST API + Spring Security + JWT
- Bridge: Python 3.10 + FastAPI microservice (will call NVIDIA Sionna later)
- Database: PostgreSQL
- Build tools: Maven (Java), pip (Python), npm (Angular)

TASK FOR TODAY:
Generate the complete project skeleton with this exact folder structure:
[... structure block ...]
```

## Session 2: Python-Sionna Bridge — First Real Simulation
```text
Context: I am building "Sionna Visualizer" — a full-stack web dashboard 
that makes NVIDIA Sionna 6G simulation results visual and shareable.

My project structure:
sionna-visualizer/
├── backend/        → Java Spring Boot 3
├── python-bridge/  → Python FastAPI (THIS IS WHAT WE ARE WORKING ON TODAY)
└── frontend/       → Angular 17

TASK FOR TODAY — enhance the python-bridge layer:
[... task specs ...]
```

## Session 2: Run Instructions
```text
cd sionna-visualizer/python-bridge
uvicorn main:app --reload --port 8001

Then open your browser and visit:
http://localhost:8001/simulate/demo
```

## Session 3: Angular Frontend — First Real Chart
```text
Context: I am building "Sionna Visualizer" — a full-stack web dashboard 
that makes NVIDIA Sionna 6G simulation results visual and shareable.

My project structure:
sionna-visualizer/
├── backend/        → Java Spring Boot 3 (running on port 8080)
├── python-bridge/  → Python FastAPI (running on port 8001) ✅ DONE
└── frontend/       → Angular 17 + Angular Material (THIS IS TODAY)

TASK FOR TODAY — build the Angular frontend dashboard:
[... implementation steps for Chart.js, Services, Dashboard Component, Routes, Proxy ...]
```

## Session 4: Java Spring Boot Backend — Connect to Python Bridge + Store in PostgreSQL
```text
New data flow after today:
Angular → Java (8080) → Python (8001) → Java stores in PostgreSQL 
→ Java returns result to Angular

TASK FOR TODAY:
[... Implementation steps for Entity, Repository, DTO, Service, Controller, application.yml, Angular Service ...]
```

## Session 5: Simulation History Page — View All Past Runs from PostgreSQL
```text
TASK FOR TODAY — build the Simulation History page in Angular:
[... implementation steps for HistoryComponent, table rendering, charts logic, Navigation Bar, styling ...]
```

## Session 7: Shareable Links — Every Simulation Gets a Public URL
```text
TASK FOR TODAY — shareable public links for every simulation:
[... Implementation steps isolating native explicitly URL paths seamlessly properly gracefully uniquely securely functionally mapping logically flawlessly confidently efficiently precisely flawlessly cleanly cleanly effectively elegantly smoothly correctly logically identically exactly natively cleanly flawlessly...]
```