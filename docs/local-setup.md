# Local setup

This page is for running the API on your laptop. You need Java 21, Maven (or the `mvnw` wrapper in this repo), and a `.env` file in the project root so Spring can read database, Redis, JWT, and Gemini settings.

Copy `.env.example` to `.env` and replace the placeholders with real values.

| Variable | What it is for |
| --- | --- |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | PostgreSQL connection (Neon works; any Postgres with SSL is fine) |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD` | Upstash Redis over TLS |
| `JWT_SECRET` | Signing key for tokens. Use at least 32 characters. |
| `GEMINI_API_KEY` | Key for the Gemini HTTP API |

Start the application with:

```
./mvnw spring-boot:run
```

On Windows, `mvn spring-boot:run` works if Maven is installed. The API listens on `http://localhost:8080`. You can confirm it is up with `GET /api/health`.

To run tests:

```
./mvnw test
```

GitHub Actions only runs `AuthControllerTest` and `ChatControllerTest`. A full local `mvn test` also runs `BedrockAiApplicationTests`, which boots the Spring context.
