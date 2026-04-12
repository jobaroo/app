# Jobaroo

## Modules
- `common/shared`: shared domain DTOs for both runtimes (`auth`, `job`, `pagination`).
- `server`: http4s backend. Entry: `com.jobaroo.Application`. Wiring: `modules/Database`, `modules/Core`, `modules/HttpApi`. Routes: `/api/health`, `/api/auth`, `/api/jobs`. Core services: `Jobs`, `Users`, `Auth`, `Tokens`, `Emails`, `Stripe`.
- `app`: Scala.js + Tyrian SPA. Entry: `com.jobaroo.App`. `pages/` holds route-level screens, `components/` app-level UI, `core/` router/session/browser state.
- `app/src/main/scala/com/jobaroo/ui`: pure typed UI vocabulary and presets. No Tyrian dependency.
- `app/src/main/scala/com/jobaroo/tyrianui`: Tyrian adapter layer built on top of `ui`.
- `sql/init.sql`: creates Postgres DB `board` and tables `jobs`, `users`, `tokens`.
- `docker-compose.yml`: local Postgres service `db` on `localhost:5432`.

## Commands
- SDKMAN init + project JDK: `source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk env`
- SDKMAN verify active JDK: `sdk current java`
- SDKMAN install missing candidates from `.sdkmanrc`: `sdk env install`
- SDKMAN restore defaults after leaving repo: `sdk env clear`
- SDKMAN status: `.sdkmanrc` pins `java=17.0.17-tem`. `sdkman_auto_env=true` may switch automatically on `cd`, but do not rely on it; verify `sdk current java` or `java -version` before running SBT.
- All SBT commands below assume the SDKMAN init command has already been run in that shell.
- Start DB: `docker compose up -d db`
- Stop DB: `docker compose down`
- Compile project: `sbt compile`
- Run backend: `sbt server/run`
- Backend URL: `http://localhost:8080`
- Healthcheck: `http://localhost:8080/api/health`
- UI live reload, terminal 1: `sbt ~app/fastLinkJS`
- UI live reload, terminal 2: `cd app && npm start`
- UI URL: usually `http://localhost:1234`; if busy, Parcel picks another port and prints it.
- Dev UI talks to: `http://localhost:8080`
- Playwright CLI workspace init: `playwright-cli install`
- Open the live UI in Playwright CLI: `playwright-cli open http://localhost:1234`
- Capture a UI snapshot: `playwright-cli snapshot`
- Capture a full-page screenshot: `playwright-cli screenshot --filename .playwright-cli/<name>.png --full-page`
- Close the Playwright session: `playwright-cli close`
- Frontend production bundle: `sbt app/fullLinkJS && cd app && npm run build`
- Tests: `sbt test`
- Staging Docker image: `sbt stagingBuild/docker:publishLocal`
