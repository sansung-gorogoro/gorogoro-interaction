# Repository Guidelines

## Project Overview

This project, named "lxp" but contained within the `gorogoro-interaction` repository, is a Spring Boot-based application designed to handle user interactions within an e-learning platform. It exposes a REST API for managing Q&A, reviews, and support tickets.

- **Primary Technologies**: Java 17, Spring Boot 3.5.x, Gradle, JUnit 5
- **Architectural Pattern**: Ports and Adapters

## Project Structure & Module Organization

- Source lives under `src/main/java/com/example/lxp`.
- The application is divided into three core domains:
    - **`qna`**: Manages questions and answers for courses.
    - **`review`**: Handles user reviews and ratings for courses.
    - **`support`**: Manages support tickets and user inquiries.
- Each domain follows a ports-and-adapters layout: `adapter`, `application`, and `domain` layers.
- Shared utilities (events, ports) sit in `common`.
- Entrypoint is `src/main/java/com/example/lxp/ProjectLxp3Application.java`.
- Configuration resides in `src/main/resources/application.yml`, pulling port and DB settings from `SERVER_PORT` and
  `DATASOURCE_*` env vars.
- Tests belong in `src/test/java` mirroring production packages; add fixtures/resources under `src/test/resources` when
  needed.

## Build, Test, and Development Commands

- `./gradlew clean build` – full compile + test cycle; resolves dependencies.
- `./gradlew test` – run the JUnit 5 suite only.
- `./gradlew bootRun` – start the Spring Boot API with local env overrides.
- `./gradlew dependencyCheckAnalyze` – (if plugin present) scan dependencies for CVEs before releases.

## Coding Style & Naming Conventions

- Java 17, Spring Boot 3.5.x; 4-space indentation. Keep controllers thin; push business logic to `application/service`
  and persistence to adapters/repositories.
- Use PascalCase for classes, camelCase for methods/fields, and UPPER_SNAKE for constants/enums. Follow existing ports (
  `*UseCase`, `*Port`) and adapters naming.
- Prefer meaningful exceptions via `BusinessException`/`ErrorCode`; surface client-safe messages.
- Run formatting if available (`./gradlew spotlessApply` or `./gradlew check`); otherwise match existing style.

### Error Code Naming Conventions

To ensure clarity and consistency, error codes should follow these guidelines:

- **`[RESOURCE]_[STATUS]`**: For expressing the status of a resource.
    - Example: `QUESTION_NOT_FOUND` (Question not found)
- **`[ACTION]_[STATUS]`**: For expressing the result of a specific action.
    - Example: `REPLY_FORBIDDEN` (Reply forbidden)
- **`[RESOURCE]_[PROPERTY]_[STATUS]`**: For expressing the status of a specific property of a resource.
    - Example: `QUESTION_TITLE_IS_BLANK` (Question title is blank)
- **`CANNOT_[ACTION]_[REASON]`**: For expressing the reason why a specific action cannot be performed.
    - Example: `CANNOT_REPLY_TO_REPLY` (Cannot reply to a reply)

## Testing Guidelines

- Use JUnit 5 (Spring Boot starter). Name test classes `*Test` and methods `should<Behavior>When<Condition>`.
- Add service/controller and error-path coverage; use `@DataJpaTest` with in-memory configs for persistence.
- Execute `./gradlew test` before pushing; include regression cases for bug fixes.

## Commit & Pull Request Guidelines

- Follow lightweight Conventional Commits (`feat:`, `fix:`, `refactor:`, `docs:`); add short scopes when helpful.
- PRs should describe purpose, key changes, and verification (`./gradlew test`); attach screenshots for client-facing
  behavior and link relevant issues. Call out breaking changes or new env vars.

## Security & Configuration Tips

- Do not commit secrets. Keep `DATASOURCE_URL` pointed to non-production DBs locally.
- Review `application.yml` for env-driven settings; prefer env overrides instead of hardcoding.
- Run dependency and vulnerability checks before shipping sensitive changes.
