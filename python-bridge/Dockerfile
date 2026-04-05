FROM python:3.10-slim

WORKDIR /app

# Copy requirements first (Docker layer caching)
COPY requirements.txt .

# Install all Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy all source files
COPY . .

# Expose the port Railway expects
EXPOSE 8001

# Start the FastAPI bridge
# We use single worker to minimize memory usage on Railway free tier
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8001", "--workers", "1"]
