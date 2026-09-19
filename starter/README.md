# Минимальный starter

Starter нужен только для экономии времени на первичной настройке. Он содержит Java 21, Spring Boot, Maven, подключения Web/JPA/Validation/Liquibase/PostgreSQL и Docker Compose с PostgreSQL.

В нём намеренно нет доменных сущностей, сервисов, контроллеров, готовой обработки ошибок и тестовой архитектуры.

Prerequisites:

- JDK 21;
- Maven 3.9+;
- Docker с Docker Compose.

Запуск базы и приложения:

```bash
docker compose up -d
mvn spring-boot:run
```

Запуск тестов:

```bash
mvn test
```

Можно изменить starter или создать проект самостоятельно, если выбранные решения объяснены в README.
