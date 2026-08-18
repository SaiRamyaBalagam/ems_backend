# EMS Backend

A Spring Boot REST API for an Employee Management System — employee/department/attendance/leave/salary CRUD, JWT-based auth with 5-role RBAC, and Kafka event streaming for cross-cutting concerns (notifications, audit logging, live dashboard metrics).

Frontend: [ems_frontend](https://github.com/SaiRamyaBalagam/ems_frontend) (Angular)

## Features

- **Auth & RBAC** — JWT auth (`jjwt`), 5 roles (`ADMIN`, `HR`, `FINANCE`, `MANAGER`, `EMPLOYEE`) with per-endpoint access control. Employee accounts are auto-provisioned/deprovisioned when an employee is added/removed.
- **Employee lifecycle** — full CRUD, plus soft-delete: deactivating an employee disables their login and hides them from active listings without destroying their leave/salary/attendance history. Deactivated employees can be listed and reactivated separately.
- **Departments, Attendance, Leave Requests, Salaries** — full CRUD with role-gated actions (e.g. leave approval, salary payout).
- **Event-driven architecture** — domain events (`EmployeeCreated`, `EmployeeDeleted`, `LeaveApplied`, `LeaveApproved`, `SalaryPaid`, etc.) are published via Spring's `ApplicationEventPublisher` after each transaction commits, then forwarded to Kafka. Three independent consumer groups react to the same event stream:
  - `notification-service` — logs/simulates user-facing notifications
  - `audit-service` — persists every event to an `audit_logs` table
  - `dashboard-service` — maintains live headcount, pending-leave-approval count, and payroll totals
- **Dashboard API** — live metrics computed from the Kafka-driven read models above, not recomputed on every request.

## Tech Stack

Java 21 · Spring Boot 3.5 · Spring Security · Spring Data JPA · PostgreSQL · Spring Kafka · JWT (`jjwt`) · Lombok · Maven

## Architecture Note

Rather than services calling each other directly for side effects (e.g. "when an employee is deleted, also update the dashboard and write an audit log"), state changes are published as domain events and consumed independently by each interested service. This keeps the core CRUD services free of side-effect logic and makes it straightforward to add new consumers (e.g. a future email-notification service) without touching existing code.

## Getting Started

**Prerequisites:** Java 21, Maven, PostgreSQL, Docker (for Kafka)

```bash
# 1. Start Kafka + Kafka UI
docker compose up -d

# 2. Create the database
createdb ems_db

# 3. Set required environment variables
#    (the app reads these instead of hardcoding secrets)
setx DB_PASSWORD "your-postgres-password"
setx JWT_SECRET "a-long-random-secret"

# 4. Run
./mvnw spring-boot:run
```

On first run against an empty database, a bootstrap admin account is seeded automatically (`admin@ems.local` / `Admin@123`) — change the password after logging in.

Kafka UI (topic inspection) is available at `http://localhost:8081` once `docker compose up -d` is running.
