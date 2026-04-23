## beeatlas-fdm-techradar-service

Сервис техрадаров (Tech Radar) — backend‑приложение, которое хранит и предоставляет информацию
о технологиях и паттернах, используемых в компании: их статусах, секторах, кольцах, историях
изменений и связанных процессах/продуктах.

### Стек

- **Язык/платформа**: Java 17, Spring Boot 2.7.x (`TechradarBackendApplication`)
- **БД**: PostgreSQL (схема `techradar`)
- **ORM / доступ к данным**: Spring Data JPA
- **Миграции БД**: Flyway (`src/main/resources/db/migration`)
- **Документация API**: Swagger / OpenAPI (аннотации `io.swagger.annotations.*`)
- **Сборка**: Maven
- **Контейнеризация**: Docker, `Dockerfile`, `docker-compose.yml`
- **Мониторинг**: Spring Actuator (`/actuator/health`, `/actuator/prometheus`)

### Основные возможности

- Управление технологиями (`/api/v1/tech`):
  - получение списка технологий и детальной информации с историей статусов;
  - создание, обновление, удаление технологий;
  - управление версиями технологий;
  - экспорт данных по технологиям в документ через интеграцию с document‑service.
- Управление паттернами проектирования (`/api/v1/pattern*`):
  - просмотр всех паттернов и групп;
  - CRUD для паттернов и групп паттернов;
  - получение дерева групп паттернов;
  - фильтрация паттернов с автоматической проверкой.
- Управление процессами (`/api/v1/processes`) и связями с технологиями.
- Интеграции с внешними сервисами:
  - notification‑service;
  - document‑service;
  - products‑service.

### Запуск через Docker Compose

#### Требования

- Установленный **Docker** и **docker-compose**.

#### Переменные окружения

Переменные для БД (см. `docker-compose.yml`):

- **TECHRADAR_POSTGRES_DB** – имя БД (по умолчанию `techradar`);
- **TECHRADAR_POSTGRES_USER** – пользователь БД (по умолчанию `postgres`);
- **TECHRADAR_POSTGRES_PASSWORD** – пароль БД (по умолчанию `postgres`);
- **TECHRADAR_POSTGRES_NODEPORT** – внешний порт Postgres (по умолчанию `5432`);
- **TECHRADAR_SERVICE_PORT** – внешний порт сервиса (по умолчанию `8081`).

Переменные интеграций (используются в клиентах через `@Value(...)`):

- **INTEGRATION_NOTIFICATION_SERVER_URL** → `integration.notification-server-url`
- **INTEGRATION_DOCUMENT_SERVER_URL** → `integration.document-server-url`
- **INTEGRATION_PRODUCTS_SERVER_URL** → `integration.products-server-url`

Пример фрагмента `docker-compose.yml`:

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

#### Команды

```bash
# сборка и запуск контейнеров в фоне
docker-compose up -d --build

# остановка и удаление контейнеров
docker-compose down
```

После запуска backend будет доступен по адресу:

- `http://localhost:8081` (если не менялся `TECHRADAR_SERVICE_PORT`).

### Локальный запуск (без Docker)

#### Требования

- Java 17;
- Maven;
- доступный PostgreSQL (БД/схема `techradar`).

#### Шаги

1. Настроить подключение к БД (через `application.properties` или env:
   `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).
2. Flyway‑миграции применятся автоматически при старте приложения.
3. Собрать и запустить:

```bash
mvn clean package -DskipTests
java -jar target/techradar-<version>.jar
```

### Миграции БД

Миграции находятся в `src/main/resources/db/migration` и автоматически применяются Flyway при
старте приложения. Ключевые миграции:

- **V1__Base_version.sql** – базовые таблицы (`Tech`, `Category`, `Ring`, `Sector`, …);
- **V2__fill_sector_table.sql** – наполнение справочников `Sector` и `Ring`, создание последовательностей;
- **V3__Technoradar_patch.sql**, **V4__Technoradar_patch.sql** и др. – донаполнение таблицы `tech` и корректировки;
- **V5__add_black_lists.sql**, **V6__fill_black_lists.sql**, **V10__migrate_blacklist_to_tech.sql** – работа с black‑list;
- **V11__recreate_history_tech_and_process.sql** – таблицы `history_tech` и `process` и их связи;
- **V15–V18** – таблицы паттернов (`pattern`, `pattern_group`, `pattern_tech`) и доп. колонки;
- **V19__refresh_processes_table.sql** – наполнение таблицы `process` (может зависеть от конкретных `tech_id`).

### Основные REST‑эндпоинты

Ниже приведены ключевые эндпоинты (не полный список).

- **Технологии** (`TechController`, базовый путь `"/api/v1/tech"`):
  - `GET /api/v1/tech` – список технологий (опционально только актуальные);
  - `GET /api/v1/tech/{id}` – технология с историей статусов;
  - `GET /api/v1/tech/by-ids?ids=1&ids=2` – технологии по списку ID;
  - `POST /api/v1/tech` – создание технологий;
  - `PATCH /api/v1/tech/{id}` – обновление технологии;
  - `DELETE /api/v1/tech/{id}` – удаление технологии;
  - версии технологии:
    - `POST /api/v1/tech/{tech_id}/version`
    - `PATCH /api/v1/tech/{tech_id}/version/{id_version}`
    - `DELETE /api/v1/tech/{tech_id}/version/{version_id}`
  - `POST /api/v1/tech/export/{doc_id}` – экспорт в документ.

- **Паттерны** (`PatternController`, базовый путь `"/api/v1"`):
  - `GET /api/v1/patterns` – все паттерны;
  - `GET /api/v1/patterns/tech/{tech_id}` – паттерны по технологии;
  - `GET /api/v1/pattern/{id}` – паттерн по id;
  - `GET /api/v1/patterns/auto-check` – паттерны с авто‑проверкой;
  - группы паттернов:
    - `GET /api/v1/pattern/group`
    - `GET /api/v1/pattern/group/tree`
    - `POST /api/v1/pattern/group`
    - `PATCH /api/v1/pattern/group/{id}`
    - `DELETE /api/v1/pattern/group/{id}`
  - паттерны:
    - `POST /api/v1/pattern`
    - `PATCH /api/v1/pattern/{id}`
    - `DELETE /api/v1/pattern/{id}`

- **Справочники**:
  - `GET /api/v1/sectors` – сектора;
  - `GET /api/v1/rings` – кольца;
  - `GET /api/v1/category` – категории.

- **Процессы**:
  - `GET /api/v1/processes` – список процессов.

### Health‑checks и мониторинг

- `GET /actuator/health` – проверка здоровья приложения;
- `GET /actuator/prometheus` – метрики Prometheus (если включены).

### Разработка

- Основной пакет: `ru.beeline.techradar.*`
- Доменные сущности: `ru.beeline.techradar.domain.*`
- Контроллеры: `ru.beeline.techradar.controller.*`
- Сервисы: `ru.beeline.techradar.service.*`
- Репозитории: `ru.beeline.techradar.repository.*`
- DTO/мапперы: `ru.beeline.techradar.dto.*`, `ru.beeline.techradar.maper.*`

Для локальной разработки можно использовать PostgreSQL из `docker-compose.yml`
или собственный экземпляр Postgres с той же схемой и миграциями.
