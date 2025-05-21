# CityTales Auth Module

This module handles user authentication (register/login) for the CityTales Vienna platform.

## Features
- Email-based registration and login
- Password hashing with Spring Security
- REST endpoints for `/api/auth/register` and `/api/auth/login`
- Basic unit and integration tests included

## Build & Run

### Prerequisites
- Java 17+
- Maven
- Docker

### Run locally

```bash
mvn spring-boot:run
```

### Build JAR

```bash
mvn clean package
```

### Run with Docker

```bash
docker build -t citytales-auth .
docker run -p 8081:8081 citytales-auth
```

## Endpoints

| Method | Endpoint             | Description         |
|--------|----------------------|---------------------|
| POST   | `/api/auth/register` | User registration   |
| POST   | `/api/auth/login`    | User login          |

## Environment Variables
None required for basic operation.
