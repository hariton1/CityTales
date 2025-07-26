The **Data Scraper Service** handles extraction of public data and tagging logic:

* Collects metadata from **OpenStreetMap** and **Wikidata**
* Maps entities to **Neo4j** graph structure
* Supports creation of tag nodes and connections (e.g. locations → events → keywords)

**Features:**

* Graph traversal and lookup
* Dynamic data expansion
* REST endpoints for tag fetch and assignment