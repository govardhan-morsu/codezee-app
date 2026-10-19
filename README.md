# Task Manager API

Small Spring Boot REST API for managing tasks. Built for the Codzee take-home.

## Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- H2 (in-memory)

## Run

```bash
mvn spring-boot:run
```

App starts on `http://localhost:8080`. H2 console is at `/h2-console` (JDBC URL `jdbc:h2:mem:taskdb`, user `sa`, blank password).

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/tasks` | List all tasks |
| GET | `/api/tasks/{id}` | Get one task |
| POST | `/api/tasks` | Create a task |
| PUT | `/api/tasks/{id}` | Update a task |
| DELETE | `/api/tasks/{id}` | Delete a task |

### Example create

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Write README",
    "description": "Document how to run the app",
    "status": "TODO",
    "dueDate": "2026-11-01"
  }'
```

### Task fields

- `id` — generated
- `title` — string
- `description` — string
- `status` — `TODO` | `IN_PROGRESS` | `DONE` (defaults to `TODO`)
- `dueDate` — `YYYY-MM-DD`

## Tests

```bash
mvn test
```
