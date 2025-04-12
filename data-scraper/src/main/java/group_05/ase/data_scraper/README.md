### 🌐 Wikipedia APIs

#### 📘 Swagger Documentation
- [Wikimedia Swagger UI](https://en.wikipedia.org/api/rest_v1/#/)  
  Interactive documentation for the Wikimedia REST API.

#### 🛠️ Fine-Grained API Reference
- [MediaWiki Action API](https://en.wikipedia.org/w/api.php)  
  Detailed reference for all `action=query`, `list=search`, and other advanced usage.

---

### Current Functionality

#### `/api/prototype/batchSearch`

This endpoint can fetch every Wikidata article that is connected to the entity:  
**`VIENNA_HISTORY_WIKI_ID = "P7842"`**  
It performs this by fetching results in batches, determined by the set `batchSize`, and repeats the process `x` times.
