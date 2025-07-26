---
title: Microservice Details
---
#### :desktop: Frontend

* **Angular 17**, written in **TypeScript**
* Uses **Taiga UI** for a modern component-based UI
* Connects via REST to backend services

#### :arrows_counterclockwise: Orchestrator

* Bridges frontend calls to the appropriate backend microservices
* Manages workflows like _story generation_, _tag assignment_, and _data collection_

#### :globe_with_meridians: Data Scraper

* Uses **JSoup** to extract structured data from **OpenStreetMap** and **Wikidata**
* Integrates with **Neo4j** to store graph-based relationships like tags, locations, and events
* Scheduled scraping via **Spring Scheduler**

#### :robot: OpenAI Adapter

* Spring Boot wrapper to call **OpenAI’s GPT-4**
* Supports prompt chaining and safe error handling
* Converts raw data into narrative-friendly outputs for stories

#### :tada: Fun Fact Generator (Python Service)

* **Flask API** with smart prompt engineering for GPT-4
* Uses **SentenceTransformer** to identify humorous/absurd parts of text
* Includes **Spacy (German NLP)** + fallback logic + logging
* Logs fun facts to CSV with score and source tracking