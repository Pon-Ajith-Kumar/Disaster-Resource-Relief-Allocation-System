<div align="center" style="
     padding: 20px;
     border-radius: 15px;
     background: rgba(56, 189, 248, 0.08);
     border: 1px solid rgba(56, 189, 248, 0.25);
">

### 🌍 **Disaster Relief Resource Management System** 🌍

</div>

A focused and scalable **Spring Boot backend** built to simplify the distribution of essential relief products during emergencies.  <br /><br />
The platform connects **Citizens**, **NGOs**, and **Administrators** in a streamlined workflow where: <br />
✅ Citizens can request **basic essential products** they need  
✅ NGOs can **create, update, and manage** these products on the platform  
✅ Admin monitors **all operations**, manages the **database**, and ensures smooth coordination

The system includes **secure authentication**, **request handling**, **product management**, **allocation & approval workflows**, and **role-based access control**, enabling efficient delivery of essential resources during crisis situations..

<br/>

> This README is written to be **step-by-step**, so anyone can clone, run locally.

---

## 📦 Tech Stack

- **Java** 17+
- **Spring Boot**
  - Web (REST)
  - Security (method‑level with `@PreAuthorize`)
  - Validation (`jakarta.validation`)
  - Scheduling & Async
- **Spring Data JPA** (MySQL 8)
- **Lombok**
- **Maven**

---

## 🧱 Domain Model (Tables)

The project uses four main tables (see `src/main/java/.../model/*`):

1. **users**
   - `id` (PK, auto)
   - `name`, `email` (unique), `password` (BCrypt)
   - `role` (enum: ADMIN / NGO / VOLUNTEER / CITIZEN)
   - `location`, `phone`
   - `createdAt`, `updatedAt` (timestamps)

2. **requests**
   - `id` (PK)
   - `title`, `description`
   - `requestedBy` (user id or requester name/email)
   - `location`
   - `quantity`
   - `status` (e.g., PENDING / APPROVED / REJECTED / FULFILLED)
   - `createdAt`, `updatedAt`

3. **resources**
   - `id` (PK)
   - `name`
   - `category` (e.g., food, medicine, shelter, logistics)
   - `quantityAvailable`
   - `location`
   - `updatedAt`

4. **allocation_logs**
   - `id` (PK)
   - `requestId` (FK → requests.id)
   - `resourceId` (FK → resources.id)
   - `quantity`
   - `notes`
   - `allocatedTime` (timestamp, auto)

> Note: The enum names and some column names come from the codebase. Minor naming differences in your DB are okay—JPA maps them automatically.

---

## 🔐 Security

- Passwords are stored using **BCrypt** (`PasswordEncoder` bean in `AppConfig`).
- **Method security** is enabled (`@EnableMethodSecurity`) and endpoints use `@PreAuthorize("hasRole('ADMIN')")` style guards.
- CORS is open to `*` by default (see `SecurityConfig`), and session policy is **stateless**.
- A lightweight dev‑friendly `DummyAuthFilter` exists; for production you should replace with JWT or session‑based auth if needed.

---

## 🗂️ Project Layout

```
relief-system/
├── app.log
├── HELP.md
├── LICENSE
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── disasterrelief
│   │   │           └── relief_system
│   │   │               ├── config
│   │   │               │   ├── AppConfig.java
│   │   │               │   ├── AsyncConfig.java
│   │   │               │   ├── DummyAuthFilter.java
│   │   │               │   └── SecurityConfig.java
│   │   │               ├── controller
│   │   │               │   ├── AdminController.java
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── RequestController.java
│   │   │               │   └── ResourceController.java
│   │   │               ├── DisasterReliefSystemApplication.java
│   │   │               ├── dto
│   │   │               │   ├── AllocationDetails.java
│   │   │               │   ├── ApiResponse.java
│   │   │               │   ├── AuthResponse.java
│   │   │               │   ├── DashboardStats.java
│   │   │               │   ├── LoginRequest.java
│   │   │               │   └── RegisterRequest.java
│   │   │               ├── model
│   │   │               │   ├── AllocationLog.java
│   │   │               │   ├── Request.java
│   │   │               │   ├── Resource.java
│   │   │               │   └── User.java
│   │   │               ├── repository
│   │   │               │   ├── AllocationLogRepository.java
│   │   │               │   ├── RequestRepository.java
│   │   │               │   ├── ResourceRepository.java
│   │   │               │   └── UserRepository.java
│   │   │               └── service
│   │   │                   ├── AllocationService.java
│   │   │                   ├── DashboardService.java
│   │   │                   ├── RequestService.java
│   │   │                   ├── ResourceService.java
│   │   │                   └── UserService.java
│   │   └── resources
│   │       ├── application.properties
│   │       ├── static
│   │       │   └── index.html
│   │       └── templates
│   └── test
│       └── java
│           └── com
│               └── disasterrelief
│                   └── relief_system
│                       └── DisasterReliefSystemApplicationTests.java
└── target
    ├── classes
    │   ├── application.properties
    │   ├── com
    │   │   └── disasterrelief
    │   │       └── relief_system
    │   │           ├── config
    │   │           │   ├── AppConfig.class
    │   │           │   ├── AsyncConfig.class
    │   │           │   ├── DummyAuthFilter.class
    │   │           │   └── SecurityConfig.class
    │   │           ├── controller
    │   │           │   ├── AdminController.class
    │   │           │   ├── AuthController.class
    │   │           │   ├── RequestController.class
    │   │           │   └── ResourceController.class
    │   │           ├── DisasterReliefSystemApplication.class
    │   │           ├── dto
    │   │           │   ├── AllocationDetails.class
    │   │           │   ├── ApiResponse.class
    │   │           │   ├── AuthResponse.class
    │   │           │   ├── DashboardStats.class
    │   │           │   ├── LoginRequest.class
    │   │           │   └── RegisterRequest.class
    │   │           ├── model
    │   │           │   ├── AllocationLog.class
    │   │           │   ├── Request$Priority.class
    │   │           │   ├── Request$Status.class
    │   │           │   ├── Request.class
    │   │           │   ├── Resource$ResourceCategory.class
    │   │           │   ├── Resource.class
    │   │           │   ├── User$UserRole.class
    │   │           │   └── User.class
    │   │           ├── repository
    │   │           │   ├── AllocationLogRepository.class
    │   │           │   ├── RequestRepository.class
    │   │           │   ├── ResourceRepository.class
    │   │           │   └── UserRepository.class
    │   │           └── service
    │   │               ├── AllocationService.class
    │   │               ├── DashboardService.class
    │   │               ├── RequestService.class
    │   │               ├── ResourceService.class
    │   │               └── UserService.class
    │   └── static
    │       └── index.html
    ├── generated-sources
    │   └── annotations
    ├── generated-test-sources
    │   └── test-annotations
    ├── maven-archiver
    │   └── pom.properties
    ├── maven-status
    │   └── maven-compiler-plugin
    │       ├── compile
    │       │   └── default-compile
    │       │       ├── createdFiles.lst
    │       │       └── inputFiles.lst
    │       └── testCompile
    │           └── default-testCompile
    │               ├── createdFiles.lst
    │               └── inputFiles.lst
    ├── relief-system-0.0.1-SNAPSHOT.jar
    ├── relief-system-0.0.1-SNAPSHOT.jar.original
    ├── surefire-reports
    │   ├── com.disasterrelief.relief_system.DisasterReliefSystemApplicationTests.txt
    │   └── TEST-com.disasterrelief.relief_system.DisasterReliefSystemApplicationTests.xml
    └── test-classes
        └── com
            └── disasterrelief
                └── relief_system
                    └── DisasterReliefSystemApplicationTests.class

```

---

## 🚀 Quick Start (Local, MySQL)

### 1) Prerequisites
- **JDK 17+**
- **Maven 3.8+**
- **MySQL 8** running locally
- (Optional) **Postman** / curl

### 2) Create the MySQL database
```sql
CREATE DATABASE IF NOT EXISTS disaster_relief_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

### 3) Configure local properties
Open `src/main/resources/application.properties` and set your local DB creds:
```properties
spring.application.name=DisasterReliefSystem

spring.datasource.url=jdbc:mysql://localhost:3306/disaster_relief_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_LOCAL_DB_USER
spring.datasource.password=YOUR_LOCAL_DB_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Enable scheduling
spring.task.scheduling.pool.size=2

# Respect PORT env (used by Render/heroku‑style) but default to 8080 locally
server.port=${PORT:8080}
```

### 4) Run the app
```bash
./mvnw spring-boot:run
# or
mvn spring-boot:run
```
Visit: `http://localhost:8080`

---

## 🌱 Sample Users & Roles

Use the **Auth API** to register users. Admins can list users via `/api/admin/users`.  
Roles available (based on code): `ADMIN`, `NGO`, `VOLUNTEER`, `CITIZEN`.

---

## 📡 REST API (Summary)

> Paths taken from controllers (some parameters omitted for brevity). Most endpoints return `ApiResponse` or the relevant DTO/entity.

### Auth (`/api/auth`)
- `POST /register` → Register a new user (name, email, password, role)
- `POST /login` → Login; validates email/password; returns `AuthResponse` (user + message)
- `GET /me` → Get current auth user

### Requests (`/api/requests`)
- `GET /` → List all requests
- `GET /{id}` → Get a request by id
- `GET /user/{userId}` → Requests created by a specific user
- `GET /status/{status}` → Filter by status (e.g., PENDING)
- `GET /pending` → Convenience list of pending
- `POST /add` → Create a new request (validated body)
- `PUT /{id}/status` → Update a request’s status
- `DELETE /{id}` → Delete a request

> Some mutations may be guarded, e.g., only `ADMIN`/`NGO` may approve or delete—enforced by `@PreAuthorize` in controller/service.

### Resources (`/api/resources`)
- `GET /` → List all resources
- `GET /available` → List resources with quantityAvailable > 0
- `GET /{id}` → Get a resource
- `GET /category/{category}` → Filter by category
- `POST /add` → Create new resource
- `PUT /{id}` → Update resource (e.g., adjust stock)
- `DELETE /{id}` → Delete a resource

### Admin (`/api/admin`)
- `GET /dashboard` → Aggregated counts (`DashboardStats`: users, requests by status, resources, etc.)
- `POST /allocate` → Auto‑allocate a suitable resource to a request; creates an **allocation_logs** entry
- `GET /allocations` → List all allocations
- `GET /allocations/range?from=YYYY-MM-DD&to=YYYY-MM-DD` → Allocations in a date range
- `GET /users` → List all users / users by role

### Example JSONs

**Register**
```json
POST /api/auth/register
{
  "name": "Ajith",
  "email": "ajith@example.com",
  "password": "StrongPass@123",
  "role": "ADMIN",
  "location": "Chennai",
  "phone": "9876543210"
}
```

**Create Request**
```json
POST /api/requests/add
{
  "title": "Need 50 blankets",
  "description": "Flood‑affected area, Ward‑12",
  "requestedBy": 1,
  "location": "Chennai",
  "quantity": 50
}
```

**Create Resource**
```json
POST /api/resources/add
{
  "name": "Blanket",
  "category": "shelter",
  "quantityAvailable": 200,
  "location": "Central Depot"
}
```

**Allocate (Admin)**
```json
POST /api/admin/allocate
{
  "requestId": 12,
  "resourceId": 7,
  "quantity": 50,
  "notes": "Auto‑allocated by system"
}
```

---

## 🧪 cURL Smoke Test

```bash
# Health check (if you added a GET /actuator/health or root index.html)
curl -i http://localhost:8080/

# Register
curl -X POST http://localhost:8080/api/auth/register   -H "Content-Type: application/json"   -d '{"name":"Admin","email":"admin@example.com","password":"Pass@123","role":"ADMIN"}'

# Login
curl -X POST http://localhost:8080/api/auth/login   -H "Content-Type: application/json"   -d '{"email":"admin@example.com","password":"Pass@123"}'

# Create resource
curl -X POST http://localhost:8080/api/resources/add   -H "Content-Type: application/json"   -d '{"name":"Rice","category":"food","quantityAvailable":100,"location":"Main Depot"}'

# Create request
curl -X POST http://localhost:8080/api/requests/add   -H "Content-Type: application/json"   -d '{"title":"Need rice","description":"40 families","requestedBy":1,"location":"Ward-3","quantity":40}'

# Allocations list (admin)
curl -G http://localhost:8080/api/admin/allocations
```

---
## 🧭 Troubleshooting

- **Port already in use**: change `server.port` or kill the process using 8080.
- **Access denied for user (MySQL)**: verify user/password and that the DB user has privileges and host access.
- **Dialect/driver errors**: keep using MySQL 8 dialect; ensure your JDBC URL is correct.
- **CORS issues**: check `SecurityConfig.corsConfigurationSource()` and your frontend origin.

---

## 📜 License

See `LICENSE` in the repository.

---
