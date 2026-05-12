FROM python:3.10-slim
WORKDIR /app
COPY python-bridge/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY python-bridge/ .
EXPOSE 8001
CMD ["sh", "-c", "uvicorn main:app --host 0.0.0.0 --port ${PORT:-8001} --workers 1"]
