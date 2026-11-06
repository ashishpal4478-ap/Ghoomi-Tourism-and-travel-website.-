# GHOOMI — Java Spring Boot Backend Service

**Version:** 0.1.0-SNAPSHOT  
**Runtime:** Java 21 LTS  
**Framework:** Spring Boot 3.4.x  
**Database:** MySQL 8.4 LTS (InnoDB)  
**Migration Engine:** Flyway  

---

## 1. Prerequisites

Before running the backend service, ensure your development environment has the following installed:
* **JDK 21 LTS** (e.g. Eclipse Adoptium OpenJDK 21)
* **Apache Maven 3.9+**
* **MySQL Server 8.4 LTS**

Verify in your terminal:
```bash
java -version
mvn -version
mysql --version
```

---

## 2. Database Setup

1. **Start the MySQL Server:**
   Ensure your local MySQL service is running on port `3306`.

2. **Create the Database:**
   Log into MySQL and execute:
   ```sql
   CREATE DATABASE ghoomi_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
   ```

3. **Flyway Migrations:**
   Migrations run automatically on application startup.  
   The initial Partition 1 schema is located at:  
   `src/main/resources/db/migration/V1__ghoomi_partition1_initial_schema.sql` (defining all 24 core tables).

---

## 3. Configuration & Environment Variables

Copy `.env.example` to `.env` or set the following environment variables:

| Variable | Default Value | Description |
|---|---|---|
| `SERVER_PORT` | `8080` | HTTP port for backend REST API |
| `SPRING_PROFILES_ACTIVE` | `local` | Active Spring profile (`local`, `test`, `prod`) |
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `ghoomi_dev` | Database name |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | *(empty)* | Database password |
| `SPRING_FLYWAY_ENABLED` | `true` | Enable/disable Flyway migrations |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000` | Allowed origins for frontend Next.js client |

Alternatively, customize properties directly in `src/main/resources/application-local.yml`.

---

## 4. Building and Running

### Build & Compile:
```bash
cd backend
mvn clean compile
```

### Run Tests:
```bash
mvn test
```

### Start the Application:
```bash
mvn spring-boot:run
```
By default, the server starts at `http://localhost:8080`.

---

## 5. Verification & Health Check

Once started, test the service health:
```bash
curl http://localhost:8080/api/v1/health
```

Expected JSON response:
```json
{
  "success": true,
  "message": "Ghoomi backend is operational",
  "data": {
    "status": "UP",
    "service": "ghoomi-backend",
    "version": "0.1.0-SNAPSHOT",
    "environment": "local",
    "timestamp": "2026-11-06T10:45:00.000Z"
  },
  "timestamp": "2026-11-06T10:45:00.000Z"
}
```

---

## 6. Modular Package Structure

```text
com.ghoomi
├── GhoomiApplication.java
├── common/
│   ├── config/          # Security, Web CORS, Jackson configurations
│   ├── exception/       # Global exception handling & API error mapping
│   └── response/        # Standard ApiResponse<T> envelope
└── modules/
    ├── health/          # System health check endpoint
    ├── auth/            # Identity, registration, login, OTP & password recovery
    ├── user/            # User profile, saved travellers & emergency contacts
    ├── destination/     # Curated destinations, attractions, dining & activities
    ├── trip/            # Trip CRUD, multi-day scheduling & version concurrency
    ├── budget/          # Deterministic integer paise budget calculations
    ├── ai/              # AI structured itinerary generation & manual fallback
    ├── provider/        # Travel supplier capability registry & adapters
    ├── booking/         # Booking intents, status transitions & confirmations
    ├── payment/         # Payment attempts, webhooks & gateway reconciliation
    ├── safety/          # Official emergency directory & resources
    ├── support/         # Customer support tickets & incident handling
    └── audit/           # Audit logs & privileged operation tracking
```

