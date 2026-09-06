# API

This page lists the HTTP endpoints the backend exposes. On your machine the base URL is `http://localhost:8080`. In AWS you use the load balancer DNS from `terraform output alb_dns_name` on port 80.

Routes under `/api/auth/register`, `/login`, `/refresh`, `/logout`, and `/api/health` are public. Everything else expects a header in this form:

`Authorization: Bearer <accessToken>`

Register and login respond with an `accessToken` and a `refreshToken`. Send the access token on chat and profile calls. Use the refresh token only on `/api/auth/refresh` and `/api/auth/logout`.

## Public routes

| Method | Path | What it does |
| --- | --- | --- |
| GET | `/api/health` | Returns a simple up status. The ALB uses this as a health check. |
| POST | `/api/auth/register` | Creates a user. Body fields are username, email, and password. |
| POST | `/api/auth/login` | Returns tokens. Body fields are email and password. |
| POST | `/api/auth/refresh` | Rotates tokens. Body field is refreshToken. |
| POST | `/api/auth/logout` | Clears the stored refresh token. Body field is refreshToken. |

## Authenticated routes

| Method | Path | What it does |
| --- | --- | --- |
| GET | `/api/auth/me` | Returns the logged-in user |
| GET | `/api/auth/users` | Returns a paginated list of users |
| DELETE | `/api/auth/me` | Deletes the logged-in user |
| POST | `/api/chat` | Sends a prompt. Body fields are prompt and an optional sessionId. |
| GET | `/api/chat/sessions` | Lists the user's sessions. You can pass a `search` query. |
| GET | `/api/chat/history/{sessionId}` | Returns messages in that session |
| DELETE | `/api/chat/history/{sessionId}` | Deletes that session |

## Register example

```
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"talha\",\"email\":\"talha@example.com\",\"password\":\"secret1\"}"
```
