## beeatlas-fdm-techradar-service

Tech Radar service — a backend application that stores and exposes information
about technologies and patterns used in the company: their status, sectors, rings,
change history, and related processes/products.

### Stack

- **Language/platform**: Java 17, Spring Boot 2.7.x (`TechradarBackendApplication`)
- **Database**: PostgreSQL (schema `techradar`)
- **ORM / data access**: Spring Data JPA
- **DB migrations**: Flyway (`db/migration`)
- **API documentation**: Swagger / OpenAPI (`io.swagger.annotations.*`)
- **Build**: Maven
- **Containerization**: Docker, `Dockerfile`, `docker-compose.yml`
- **Monitoring**: Spring Actuator (`/actuator/health`, `/actuator/prometheus`)

### Main features

- Technology management (`/api/v1/tech`):
  - fetch list of technologies and detailed info with status history;
  - create, update, delete technologies;
  - manage technology versions;
  - export technology data to a document via integration with the document service.
- Design pattern management (`/api/v1/pattern*`):
  - view all patterns and groups;
  - full CRUD for patterns and pattern groups;
  - get pattern group tree;
  - filter patterns that have automatic checks configured.
- Process management (`/api/v1/process*`) and links between processes and technologies (via migrations and services).
- Integrations with external services:
  - notification service;
  - document service;
  - products service.

### Running with Docker Compose

#### Requirements

- Installed **Docker** and **docker-compose**.

#### Environment variables

Database‑related variables (see `docker-compose.yml`):

- **TECHRADAR_POSTGRES_DB** – database name (default `techradar`);
+- **TECHRADAR_POSTGRES_USER** – database user (default `postgres`);
+- **TECHRADAR_POSTGRES_PASSWORD** – database password (default `postgres`);
+- **TECHRADAR_SERVICE_PORT** – external service port (default `8081`).

External integrations (can be set to stub values):

- **INTEGRATION_NOTIFICATION_SERVER_URL** – notification service URL;
- **INTEGRATION_DOCUMENT_SERVER_URL** – document service URL;
- **INTEGRATION_PRODUCTS_SERVER_URL** – products service URL.

Example (see `docker-compose.yml`):

```yaml
services:
  techradar-backend:
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://techradar-postgres:5432/techradar
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_JPA_HIBERNATE_DDL_AUTO: none
      SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA: techradar
      SPRING_FLYWAY_DEFAULT_SCHEMA: techradar
      SPRING_FLYWAY_BASELINE_ON_MIGRATE: "true"
      SPRING_FLYWAY_CLEAN_DISABLED: "true"
      INTEGRATION_NOTIFICATION_SERVER_URL: "http://notification-service"
      INTEGRATION_DOCUMENT_SERVER_URL: "https://document-service"
      INTEGRATION_PRODUCTS_SERVER_URL: "https://products-service"
```

#### Commands

```bash
# build and run containers in background
docker-compose up -d --build

# stop and remove containers
docker-compose down
```

After startup the backend will be available at:

- `http://localhost:8081` (if `TECHRADAR_SERVICE_PORT` was not changed).

### Local run (without Docker)

#### Requirements

- Java 17;
- Maven;
- accessible PostgreSQL instance (create DB/schema `techradar`).

#### Steps

1. Configure `application.properties` or override DB settings via environment variables
   (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).
2. Flyway migrations will be applied automatically on application startup.
3. Build and run:

```bash
mvn clean package -DskipTests
java -jar target/techradar-<version>.jar
```

### Database migrations

Migrations are located in `src/main/resources/db/migration` and are applied automatically
by Flyway on application startup. Key migrations:

- **V1__Base_version.sql** – base tables (`Tech`, `Category`, `Ring`, `Sector`, …);
- **V2__fill_sector_table.sql** – initial data for `Sector` and `Ring`, create sequences;
- **V3__Technoradar_patch.sql**, **V4__Technoradar_patch.sql**, etc. – additional data for `tech` and fixes;
- **V5__add_black_lists.sql**, **V6__fill_black_lists.sql**, **V10__migrate_blacklist_to_tech.sql** – blacklist management;
- **V11__recreate_history_tech_and_process.sql** – `history_tech` and `process` tables and relations;
- **V15–V18** – pattern tables (`pattern`, `pattern_group`, `pattern_tech`) and extra columns;
- **V19__refresh_processes_table.sql** – initial data for `process` table.

### Main REST endpoints

Below are only the key ones; see Swagger/OpenAPI for a full list.

- **Technologies** (`TechController`, base path `"/api/v1/tech"`):
  - `GET /api/v1/tech` – list technologies (optionally only actual ones);
  - `GET /api/v1/tech/{id}` – technology with status history;
  - `POST /api/v1/tech` – create technologies;
  - `PATCH /api/v1/tech/{id}` – partial update of a technology;
  - `DELETE /api/v1/tech/{id}` – delete a technology;
  - version management: `POST /api/v1/tech/{tech_id}/version`, `PATCH /api/v1/tech/{tech_id}/version/{id_version}`, `DELETE /api/v1/tech/{tech_id}/version/{version_id}`;
  - `POST /api/v1/tech/export/{doc_id}` – export technology data to a document.

- **Patterns** (`PatternController`, base path `"/api/v1"`):
  - `GET /api/v1/patterns` – all patterns;
  - `GET /api/v1/patterns/tech/{tech_id}` – patterns for a specific technology;
  - `GET /api/v1/pattern/{id}` – pattern by id;
  - `GET /api/v1/patterns/auto-check` – patterns with automatic checks;
  - `GET /api/v1/pattern/group` – pattern groups;
  - `GET /api/v1/pattern/group/tree` – pattern group tree;
  - `POST /api/v1/pattern` – create a pattern;
  - `PATCH /api/v1/pattern/{id}` – update a pattern;
  - `DELETE /api/v1/pattern/{id}` – delete a pattern;
  - analogous CRUD operations for pattern groups (`/pattern/group`).

### Health checks and monitoring

- `GET /actuator/health` – basic application health check;
- `GET /actuator/prometheus` – Prometheus metrics (if enabled).

### Development

- Main package: `ru.beeline.techradar.*`
- Domain entities: `ru.beeline.techradar.domain.*`
- Controllers: `ru.beeline.techradar.controller.*`
- Services: `ru.beeline.techradar.service.*`
- Repositories: `ru.beeline.techradar.repository.*`
- DTOs/mappers: `ru.beeline.techradar.dto.*`, `ru.beeline.techradar.maper.*`

For local development it is recommended to use the PostgreSQL Docker container from `docker-compose.yml`
or your own PostgreSQL instance with the same schema and migrations.
