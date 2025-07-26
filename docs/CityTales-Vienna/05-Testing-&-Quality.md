---
title: 05 Testing & Quality
---
**Goal:** Ensure the product works as expected.

CityTales Vienna places a strong emphasis on quality through automated and manual testing strategies across all modules. The system has been tested for functionality, stability, performance, and user experience.

---

### **Backend Testing**

**🧪 Unit Tests**

* Coverage of all core services (auth, orchestration, scraping, OpenAI adapter)
* Includes JWT issuance, request validation, error handling

**🧪 Integration Tests**

* Spring Boot test configurations with real or test containers (Supabase, Neo4j)
* REST endpoint verification across services

---

### **AI & Fun Fact Service Testing**

**🧪 Python Flask Fun Fact Service**

* SentenceTransformer-based relevance scoring tested with mock data
* GPT fallback and caching logic verified via test payloads
* CSV logging and input/output pipelines tested for robustness

---

### **Frontend Testing**

**🧪 Angular Unit Tests**

* Includes forms, UI interactions, API communication mocks

**🧪 End-to-End (E2E) Testing**

* User onboarding flow
* Story and tour loading
* Offline caching behavior
* XP and gamification flows

---

### **Manual Testing**

**✅ Scenarios Tested**

* First-time user onboarding
* Creating and sharing tours
* Logging in and navigating with location tracking
* Fun fact generation and display
* Quiz flow with answer scoring
* XP calculation and leaderboard update

**✅ Test Devices**

* Mobile (Android, iOS)
* Desktop browsers (Chrome)
* Offline mode tested in airplane mode

---

### **05.5 CI/CD & Automation**

* **GitHub Actions** for build, test, and deploy workflows
* Docker containers built and validated per service
* Automatic test runs for every pull request
* Integration test suite runs before production deployment

---

### ✅ Summary

| Area | Status | Tools/Tech |
|------|--------|------------|
| Backend Unit Tests | ✅ Complete | JUnit, Mockito |
| Backend Integration Tests | ✅ Complete | Spring Test, Testcontainers |
| Frontend Unit Tests | ✅ Complete | Jasmine, Karma |
| E2E Tests | ✅ Complete | Protractor / Cypress (if used) |
| Fun Fact AI Service | ✅ Tested & Robust | Flask test client, PyTest |
| Manual Testing | ✅ Verified on Devices | Browser + Mobile (iOS/Android) |
| CI/CD Automation | ✅ Integrated | GitHub Actions, Docker Compose |

