FROM python:3.11-slim

WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        libglib2.0-0 libsm6 libxrender1 libxext6 && \
    rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
RUN pip install --no-cache-dir sentence-transformers
RUN python -m spacy download de_core_news_sm

# Copy your code
COPY . .

EXPOSE 5005

# Run your app - Flask on 0.0.0.0 for Docker
CMD ["python", "pymlservice.py"]
