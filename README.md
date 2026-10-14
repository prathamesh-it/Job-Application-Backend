# Job Application Tracker – REST API

A secure backend service for tracking job applications. Users register, log in, manage companies, and track their own applications through every hiring stage. Access is protected with **JWT authentication** and **role-based and ownership-based authorization**.

> Backend-only project. No frontend is included.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Data Model](#data-model)
- [Security and Authorization](#security-and-authorization)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
- [Usage Example](#usage-example)
- [Error Handling](#error-handling)
- [Future Improvements](#future-improvements)
- [Author](#author)

---

## Features

- User registration and login with **BCrypt** password hashing
- **Stateless JWT authentication** (`Authorization: Bearer <token>`)
- **Role-based access control (RBAC)** with `USER` and `ADMIN` roles
- **Ownership checks** so users can only modify their own data
- Full CRUD for users, companies, and job applications
- Pagination and sorting on list endpoints
- Job application filters by status, company, position, and salary range
- DTO and mapper layer so passwords and internal fields are never exposed
- Request validation with Bean Validation
- Centralized exception handling with consistent JSON error responses

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| Security | Spring Security, JWT (JJWT 0.12.6) |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Utilities | Lombok |

---

## Architecture

The application follows a layered architecture:

```
Client
  │
  ▼
JwtAuthenticationFilter   → validates the token, sets the logged-in user
  │
  ▼
Controller                → handles HTTP requests, reads the current user
  │
  ▼
Service                   → business logic, role and ownership checks
  │
  ▼
Repository (Spring Data JPA)
  │
  ▼
PostgreSQL
```

**Authentication flow**

1. `POST /api/v1/users/login` verifies the email and password through Spring Security's `AuthenticationManager`.
2. On success, `JwtService` issues a signed token containing the email, user id, and role.
3. For every later request, `JwtAuthenticationFilter` validates the token, loads the user, and places it in the `SecurityContext`.
4. Controllers receive the caller through `@AuthenticationPrincipal`, and services apply role and ownership rules.

---

## Project Structure

```
src/main/java/com/JobApplication/JobApplication
├── Controller        # REST controllers (Auth, User, Company, JobApplication)
├── Service           # Business logic and authorization rules
├── Repository        # Spring Data JPA repositories
├── Entity            # JPA entities (User, Company, JobApplication)
├── DTOs              # Request and response objects
├── Mappers           # Entity <-> DTO conversion
├── Enum              # Role, ApplicationType
├── Security          # SecurityConfig, JwtService, JwtAuthenticationFilter, CustomUserDetails
└── Exceptions        # Custom exceptions and GlobalExceptionHandler

src/main/resources
└── application.properties
```

---

## Data Model

```
User (1) ───────< (N) JobApplication (N) >─────── (1) Company
  │                                                    │
  └──────────────── creates (1 : N) ───────────────────┘
```

| Entity | Key Fields |
|---|---|
| **User** | id, name, email (unique), password (hashed), phone, role |
| **Company** | id, name, website, location, createdBy |
| **JobApplication** | id, position, applicationDate, status, jobUrl, salary, notes, user, company |

**Application statuses:** `APPLIED`, `SCREENING`, `INTERVIEW`, `OFFER`, `REJECTED`, `WITHDRAWN`

**Cascade behavior**

- Deleting a user deletes that user's job applications. Companies they created are kept, with the owner set to `null`.
- Deleting a company deletes all job applications linked to it.

---

## Security and Authorization

Identity is always taken from the **JWT**, never from the request body.

| Resource / Action | USER | Company Owner | ADMIN |
|---|---|---|---|
| View all users | Yes | - | Yes |
| Update user | Own account only | - | Own account only |
| Delete user | Own account only | - | Any user |
| View companies | Yes | - | Yes |
| Create company | Yes (becomes owner) | - | Yes |
| Edit company | No | Own company only | No |
| Delete company | No | Own company only | Any company |
| View job applications | Own only | - | All |
| Create job application | Yes (for self) | - | No |
| Edit job application | Own only | - | No |
| Delete job application | Own only | - | Any |

Notes:

- Registration always creates a `USER`. Clients cannot choose a role.
- There is a single admin, created manually in the database (see setup).
- Unauthenticated requests return **401**. Authenticated but forbidden requests return **403**.

---

## API Endpoints

Base path: `/api/v1`. All endpoints except register and login require a Bearer token.

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/users/register` | Register a new user |
| POST | `/users/login` | Log in and receive a JWT |

### Users

| Method | Endpoint | Description |
|---|---|---|
| GET | `/users?page=0&size=3` | List users (paginated) |
| GET | `/users/{id}` | Get a user by id |
| PUT | `/users/{id}` | Update own account |
| DELETE | `/users/{id}` | Delete own account (admin: any) |

### Companies

| Method | Endpoint | Description |
|---|---|---|
| POST | `/companies` | Create a company |
| GET | `/companies?page=0&size=3` | List companies (paginated) |
| GET | `/companies/{id}` | Get a company by id |
| PUT | `/companies/{id}` | Update (owner only) |
| DELETE | `/companies/{id}` | Delete (owner or admin) |

### Job Applications

| Method | Endpoint | Description |
|---|---|---|
| POST | `/job-applications` | Create an application (USER) |
| GET | `/job-applications?page=0&size=3&sortBy=id&direction=asc` | List with pagination and sorting |
| GET | `/job-applications/{id}` | Get by id |
| PUT | `/job-applications/{id}` | Update (owner only) |
| DELETE | `/job-applications/{id}` | Delete (owner or admin) |
| GET | `/job-applications/status-check?status=APPLIED` | Filter by status |
| GET | `/job-applications/company?companyId=1` | Filter by company |
| GET | `/job-applications/position?position=java` | Search by position |
| GET | `/job-applications/salary?minSalary=500000&maxSalary=1500000` | Filter by salary range |

Regular users see only their own applications on every list and filter endpoint. Admins see all.

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+ (or use the included `mvnw` wrapper)
- PostgreSQL 14+
- Git

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/<your-repo-name>.git
cd <your-repo-name>
```

### 2. Create the database

```sql
CREATE DATABASE job_application_db;
```

### 3. Configure `application.properties`

Edit `src/main/resources/application.properties`:

```properties
spring.application.name=JobApplication

spring.datasource.url=jdbc:postgresql://localhost:5432/job_application_db
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=YOUR_BASE64_SECRET
jwt.expiration-ms=3600000
```

Generate a secure secret (must decode to at least 32 bytes):

```bash
openssl rand -base64 32
```

> Never commit real credentials or the JWT secret. Use environment variables in any shared or deployed setup.

### 4. Run the application

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The API starts at `http://localhost:8080`.

### 5. Create the admin user

Register a normal user first, then promote that account:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'your-email@example.com';
```

---

## Usage Example

**Register**

```http
POST /api/v1/users/register
Content-Type: application/json

{
  "name": "Rohit",
  "email": "rohit@example.com",
  "password": "secret123",
  "phone": "9876543210"
}
```

**Login**

```http
POST /api/v1/users/login
Content-Type: application/json

{
  "email": "rohit@example.com",
  "password": "secret123"
}
```

Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

**Call a protected endpoint**

```http
GET /api/v1/companies
Authorization: Bearer <token>
```

**Create a job application** (the owner is taken from the token)

```http
POST /api/v1/job-applications
Authorization: Bearer <token>
Content-Type: application/json

{
  "position": "Java Backend Developer",
  "companyId": 1,
  "status": "APPLIED",
  "salary": 900000,
  "jobUrl": "https://example.com/jobs/123",
  "notes": "Referred by a friend"
}
```

---

## Error Handling

Errors are returned as JSON through a global exception handler.

| Status | When |
|---|---|
| 400 | Validation failure |
| 401 | Missing, invalid, or expired token, or wrong credentials |
| 403 | Authenticated but not allowed (role or ownership check failed) |
| 404 | Resource not found |
| 409 | Email already registered |

---

## Future Improvements

- Refresh tokens and token revocation (logout)
- Swagger / OpenAPI documentation
- Unit and integration tests (JUnit, Mockito, Testcontainers)
- Docker and Docker Compose setup for the app and PostgreSQL
- Externalized configuration with environment variables and profiles (dev, prod)
- Database migrations with Flyway or Liquibase
- Admin endpoints for role management
- Replace `System.out.println` with SLF4J logging
- Return empty lists for filters instead of "not found" errors
- Whitelist valid `sortBy` fields
- Rate limiting on login to prevent brute-force attempts
- CI/CD pipeline with GitHub Actions
- Email verification and password reset
- Application status history and analytics endpoints

---

## Author

**Prathamesh Nistane**

- GitHub: [https://github.com/prathamesh-it](https://www.linkedin.com/in/prathamesh-nistane/)
- LinkedIn: [https://www.linkedin.com/in/prathamesh-nistane/](https://github.com/prathamesh-it)
