# Microservice-A Development Setup

## Quick Start

### Option 1: Run with TestContainers (Recommended)
```bash
cd microservice-a
gradlew.bat test
```
This automatically spins up PostgreSQL in a container for testing.

### Option 2: Run with Docker PostgreSQL
```bash
# Start PostgreSQL database
docker compose -f docker-compose-dev.yml up -d

# Run application locally
cd microservice-a
gradlew.bat bootRun --args="--spring.profiles.active=local"
```

### Option 3: Build and run JAR
```bash
cd microservice-a
gradlew.bat clean build
java --enable-preview -jar build/libs/microservice-a-1.0.0.jar
```

## Access Points
- **Application**: http://localhost:8080/api/v1
- **Health Check**: http://localhost:8081/actuator/health
- **Database**: postgresql://localhost:5432/microservice_a_db (postgres/password)

## API Examples
```bash
# Get all products
curl http://localhost:8080/api/v1/products

# Create product
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","description":"Test","price":99.99,"quantity":10}'
```

## Tech Stack
- **Java 25** with OpenJDK
- **Gradle 9.1.0** 
- **Spring Boot 3.3.5**
- **PostgreSQL 15** (via TestContainers or Docker)
- **TestContainers** for integration testing