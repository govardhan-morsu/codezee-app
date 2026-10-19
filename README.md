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

### Filtering & pagination

`GET /api/tasks` now accepts optional query params:

| Param | Type | Notes |
|-------|------|-------|
| `status` | `TODO` / `IN_PROGRESS` / `DONE` | filter by status |
| `dueBefore` | `YYYY-MM-DD` | due date on or before |
| `dueAfter` | `YYYY-MM-DD` | due date on or after |
| `page` | int | zero-based, default `0` |
| `size` | int | default `20`, max `100` |

Response is a page object: `content`, `page`, `size`, `totalElements`, `totalPages`.

```bash
curl "http://localhost:8080/api/tasks?status=TODO&dueBefore=2026-12-31&page=0&size=10"
```

## Tests

```bash
mvn test
```
