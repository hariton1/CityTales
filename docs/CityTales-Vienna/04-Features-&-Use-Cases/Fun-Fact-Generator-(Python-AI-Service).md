This module is fully implemented in Python and deployed as a Flask API. It:

* Extracts chunks from texts using **SpaCy (German model)**
* Filters and scores them using **SentenceTransformer**
* Sends best chunk to **OpenAI GPT-4** for rewriting
* Caches, scores, logs, and responds with the best fun fact

**Highlights:**

* Automatic fallback logic with humorous prewritten facts
* CSV logging and scoring
* Smart cache using SHA-256 hashing for repeated inputs