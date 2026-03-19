# Base Image
FROM python:3.10-slim

# Set working directory
WORKDIR /app

# Copy requirements and install
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy all python files
COPY . .

# Expose python port
ENV PORT=8001
EXPOSE ${PORT}

# Run the app
CMD ["sh", "-c", "uvicorn main:app --host 0.0.0.0 --port ${PORT}"]
