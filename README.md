# GLVD API

This repo implements the HTTP API for GLVD.

> [!NOTE]  
> This is heavily work in progress.

## Local Setup

Required/recommended software:
- [SAPMachine JDK 21](https://sap.github.io/SapMachine/)
- [Podman Desktop/Machine](https://podman.io)
- [Podman Compose](https://github.com/containers/podman-compose)

A local setup including the database with sample data can be setup using podman compose.

This requires building the jar file first.

See `compose-up.sh` for the required steps to bring up a local environment.

After about a minute you should be able to perform an HTTP GET request on http://localhost:8080/readiness and get a response with status code 200.

## Example Requests

Find example requests to play with the API in the `api-examples` folder.
They are created with [Bruno](https://www.usebruno.com), an open source and easy to use HTTP client.

## API Docs

The API is documented [here](https://gardenlinux.github.io/glvd-api/).

Those requests and responses are generated from tests automatically using [Spring REST Docs](https://spring.io/projects/spring-restdocs).
Note that you will need to adapt the hostname given in those docs.

## Running the tests

Since the tests depend on our specific postgres image with a defined set of data, we make use of a container for providing that.

Starting this container can be done using the `start-db-for-test.sh` script.

Once this is up, the tests can be run via Gradle or via any java IDE.
