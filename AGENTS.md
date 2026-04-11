# Jobaroo

## Modules
- `common/shared`: shared domain DTOs for both runtimes (`auth`, `job`, `pagination`).
- `server`: http4s backend. Entry: `com.jobaroo.Application`. Wiring: `modules/Database`, `modules/Core`, `modules/HttpApi`. Routes: `/api/health`, `/api/auth`, `/api/jobs`. Core services: `Jobs`, `Users`, `Auth`, `Tokens`, `Emails`, `Stripe`.
- `app`: Scala.js + Tyrian SPA. Entry: `com.jobaroo.App`. `pages/` holds route-level screens, `components/` reusable UI, `core/` router/session/browser state.
- `sql/init.sql`: creates Postgres DB `board` and tables `jobs`, `users`, `tokens`.
- `docker-compose.yml`: local Postgres service `db` on `localhost:5432`.

## Commands
- SDKMAN / Java: `source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk env`
- SDKMAN status: working. `.sdkmanrc` pins `java=17.0.17-tem`; SDKMAN auto-env is enabled. Plain shells can still start on default Java `25.0.2`, so verify `java -version` shows `17.0.17` before running SBT.
- Start DB: `docker compose up -d db`
- Stop DB: `docker compose down`
- Compile project: `sbt compile`
- Run backend: `sbt server/run`
- Backend URL: `http://localhost:8080`
- Healthcheck: `http://localhost:8080/api/health`
- UI live reload, terminal 1: `sbt ~app/fastLinkJS`
- UI live reload, terminal 2: `cd app && npm start`
- UI URL: `http://localhost:1234`
- Dev UI talks to: `http://localhost:8080`
- Frontend production bundle: `sbt app/fullLinkJS && cd app && npm run build`
- Tests: `sbt test`
- Staging Docker image: `sbt stagingBuild/docker:publishLocal`
