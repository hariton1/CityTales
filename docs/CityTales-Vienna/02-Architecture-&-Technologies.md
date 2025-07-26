---
title: 02 Architecture & Technologies
---
**Goal:** Explore how the system works and what it’s built with.

### System Architecture

CityTales Vienna is built as a **modular microservice system** using REST-based communication and containerized deployment. Its architecture balances scalability, separation of concerns, and rapid development.

**Key Components:**

* **Frontend** (Angular + Taiga UI)
* **User Authentication Service** (Spring Boot + JWT + Supabase DB)
* **Orchestrator Service** (Spring Boot REST controller to coordinate backend calls)
* **Data Scraper Service** (Spring Boot + Neo4j, extracts and structures urban data)
* **OpenAI Adapter** (Spring Boot, wraps GPT-4 calls with safeguards)
* **Fun Fact Generator** (Python + Flask + SentenceTransformer + GPT-4)
* **Databases**: Supabase (PostgreSQL) and Neo4j (graph-based tagging)