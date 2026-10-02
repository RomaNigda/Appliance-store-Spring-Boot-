# Appliance Store

A Spring Boot REST API for managing an appliance store: catalog, manufacturers, clients and orders, with role-based access for employees and clients.

Built as the final test project for the EPAM AutoCode Java course.

## Features

- **Employees** manage all data: CRUD for employees, clients, appliances and manufacturers, and approve client orders.
- **Clients** create, view, update and delete their own orders.
- **JWT authentication** with role-based access: the role is derived from the table the user is found in (`EmployeeEntity` → `ROLE_EMPLOYEE`, `ClientEntity` → `ROLE_CLIENT`).
- **Internationalization (i18n)**: messages are available in two languages (Ukrainian and English).
- **Pagination** for list endpoints.
- **Validation** with Bean Validation.
- **Logging** through a custom `@Loggable` annotation and an AOP aspect.
- **Database migrations** with Flyway.

## Tech Stack

| Area          | Technology                     |
|---------------|--------------------------------|
| Language      | Java                           |
| Framework     | Spring Boot                    |
| Persistence   | Spring Data JPA, PostgreSQL    |
| Migrations    | Flyway                         |
| Security      | Spring Security, JWT, BCrypt   |
| Cross-cutting | Spring AOP (logging)           |
| Containers    | Docker, Docker Compose         |

## Domain Model

- `Manufacturer`
- `Appliance`: has a `Category` and a `PowerType` (enums) and belongs to a manufacturer
- `Orders`: placed by a client, approved by an employee
- `OrderRow`: an appliance with a quantity inside an order
- `EmployeeEntity` and `ClientEntity`: independent entities, each with its own id, name, email and password

## Project Structure

```
src/main/java/.../
├── aspect/        # @Loggable annotation and logging aspect
├── config/        # MainConfig, SecurityConfig
├── controller/    # REST controllers
├── dto/           # DTO layer (*Dto)
├── entity/        # JPA entities (*Entity)
├── model/         # Domain models
├── repository/    # Spring Data JPA repositories
└── service/       # Business logic
```

## Getting Started

### Prerequisites

- JDK 17+
- Maven (the wrapper `./mvnw` is included)
- Docker and Docker Compose (for the containerized setup)

### Run with Docker Compose (recommended)

```bash
git clone <repository-url>
cd appliance-store
docker compose up --build
```

This starts two containers:

| Service | Container     | Description                          | Port           |
|---------|---------------|--------------------------------------|----------------|
| `app`   | `java-app`    | Spring Boot API (`prod` profile)     | `8080`         |
| `db`    | `postgres-db` | PostgreSQL 15 (alpine)               | `5432`         |

- The app starts only after the database passes its healthcheck (`pg_isready`).
- Flyway applies the migrations automatically on startup.
- Database data is stored in the named volume `pgdata`, so it survives restarts.
- The app reaches the database at host `db`, not `localhost`.

The API is available at `http://localhost:8080`.

Useful commands:

```bash
docker compose logs -f app     # follow application logs
docker compose down            # stop containers, keep data
docker compose down -v         # stop containers and delete the database volume
```

### Configuration

The app is configured through environment variables set in `docker-compose.yml`:

| Variable                     | Value in Compose                      |
|------------------------------|---------------------------------------|
| `SPRING_PROFILES_ACTIVE`     | `prod`                                |
| `SPRING_DATASOURCE_URL`      | `jdbc:postgresql://db:5432/mydb`      |
| `SPRING_DATASOURCE_USERNAME` | `myuser`                              |
| `SPRING_DATASOURCE_PASSWORD` | `mypassword`                          |

> The credentials above are development defaults. Change them before any real deployment.

### Run locally without Docker

Start a PostgreSQL instance, set the same `SPRING_DATASOURCE_*` variables (with `localhost` as the host), then run:

```bash
./mvnw spring-boot:run
```

## Authentication

The API uses JWT (JSON Web Token) authentication:

1. A user logs in with email and password and receives a JWT.
2. The token is sent with every protected request in the `Authorization` header:

```
Authorization: Bearer <token>
```

A custom `UserDetailsService` looks the user up by email in the employee and client repositories, and passwords are stored with BCrypt. The role is taken from the table the user is found in (`ROLE_EMPLOYEE` or `ROLE_CLIENT`). CSRF protection is disabled for `/api/**`.

There are no pre-created accounts: the database schema is created by Flyway migrations and contains no seeded users.

## Tests

```bash
./mvnw test
```

## Author

Roma, Java backend developer in training.