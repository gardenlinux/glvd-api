# GLVD API

This repository implements the HTTP API for GLVD using Java Spring Boot.

## Prerequisites

- [SapMachine JDK 25](https://sap.github.io/SapMachine/) (recommended)
- [Podman Desktop/Machine](https://podman.io)
- [Gradle](https://gradle.org/) (included via wrapper, no need to install)

## Database Setup

GLVD API requires a PostgreSQL database. The repository provides scripts to manage database containers using Podman:

- **For application runtime:**  
    - Start: `./start-db-for-app.sh`
    - Stop: `./stop-db-for-app.sh`
- **For unit tests:**  
    - Start: `./start-db-for-test.sh`
    - Stop: `./stop-db-for-test.sh`

Both containers can run simultaneously (they bind to different ports).

## Building the Application

To build the app, ensure the test database is running:

```bash
./start-db-for-test.sh
./gradlew build
```

## Running the Application Locally

1. Get a dump of the Database (this needs the GitHub `gh` cli and `jq`)

```bash
./download-db-dump.sh
```

2. Start the application database:

```bash
./start-db-for-app.sh
```

3. Build and run the Spring Boot app:

```bash
./gradlew bootRun
```

4. After startup, check readiness:

```
curl http://localhost:8080/actuator/health
# Should return status code 200
```

5. Open http://localhost:8080 in your web browser to use the UI

## Example Requests

Find example API requests in the `api-examples` folder.  
They are created with [Bruno](https://www.usebruno.com), an open source HTTP client.

## API Documentation

The API is documented [here](https://gardenlinux.github.io/glvd-api/).  
Requests and responses are generated from tests using [Spring REST Docs](https://spring.io/projects/spring-restdocs).  
Note: Adapt the hostname in the docs for your local setup.

## Running Tests

Tests require the test database container:

```bash
./start-db-for-test.sh
./gradlew test --info
```

You can also run tests in your Java IDE if the test DB container is running.

## Stopping Containers

Stop the database containers when done:

```bash
./stop-db-for-app.sh
./stop-db-for-test.sh
```
