# ASE-Project: CityTales
### Group 05

---
Team coordinator - Ivan Lichner, 01226385, e1226385@student.tuwien.ac.at

Technical architect - Christoph Winkler, 11701292, e11701292@student.tuwien.ac.at

Test coordinator - Valentin Kastberger, 11905155, e11905155@student.tuwien.ac.at

Documentation coordinator - Bianca Träger, 01326815, bianca.traeger@tuwien.ac.at

UI architect - Hariton Buçka, 01529018, e1529018@student.tuwien.ac.at

DevOps coordinator - Simon Ripphausen, 12444081, e12444081@student.tuwien.ac.at` 


---

### About

Just run the global docker-compose and enjoy.
This web-app lets you create tours, consume information and interact with friends in and around vienna.

### Disclaimer about the Data

The Data which the application builds on was retrieved from: https://www.geschichtewiki.wien.gv.at/Wien_Geschichte_Wiki 
and processed on our end.

### Basic Project Setup

All Maven/Spring projects can be initialized using the [Spring Initializr](https://start.spring.io/).

- **Spring Boot**: 3.4.4 (latest stable release)
- **Java**: 17
- **Group**: `group-05.ase`

To integrate the new module, add a reference to it in the top-level `pom.xml`. This allows you to run `mvn ...` from the top-level directory.

Each sub-project has its own `Dockerfile`, which is then referenced in the `docker-compose.yml`.

### What you need to adapt:
- NEO4J_PASSWORD=*** (in your env file and the application.props where it is used)
- OPEN_AI_API_KEY=*** where it is used


### URLs for different components
- Frontend: localhost:4200
- DataScraper: localhost:8080
- Neo4J Web-UI: localhost:7474
- UserDB: localhost:8090
- Qdrant: localhost:6333 (http interface)




