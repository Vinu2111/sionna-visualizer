# Sionna Visualizer Architecture

The Sionna Visualizer uses a 3-layer architecture designed for a clear separation of concerns, scalability, and performance in interacting with the NVIDIA Sionna 6G simulation pipeline.

## 1. Frontend: Angular 17
- **Tech Stack:** Angular 17, Angular Material, Chart.js, D3.js, SCSS.
- **Role:** Provides the interactive web dashboard for users to visually configure, run, and explore the results of 6G simulations.
- **Responsibility:** Sending configuration payloads to the backend API and rendering dynamic charts, topologies, and complex D3.js visualizations.

## 2. Backend: Java 17 + Spring Boot 3
- **Tech Stack:** Java 17, Spring Boot 3, Spring Web, Spring Security, Spring Data JPA, JWT.
- **Role:** Handles application logic, database access, user authentication (JWT), and communication orchestration.
- **Responsibility:** Exposes RESTful APIs consumed by the Angular frontend, stores users and simulation configurations in PostgreSQL, and acts as a gateway proxy to translate client requests for the Python bridge over internal HTTP calls.

## 3. Bridge: Python 3.10 + FastAPI
- **Tech Stack:** Python 3.10, FastAPI, Pydantic, NVIDIA Sionna (future).
- **Role:** The execution layer for 6G machine learning and ray tracing simulations.
- **Responsibility:** Exposing rapid, performant REST paths (via FastAPI) specifically tailored to receive simulation parameters, run NVIDIA Sionna compute jobs using TensorFlow/Keras seamlessly, and return raw mathematical matrix/tensor results structured as JSON to the Java Backend. 

## Database
- PostgreSQL stores relational data like User accounts, Simulation Runs, and persistent application state.
