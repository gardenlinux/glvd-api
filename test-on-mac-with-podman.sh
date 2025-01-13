#!/usr/bin/env bash

# As documented here https://java.testcontainers.org/supported_docker_environment/#podman

 export DOCKER_HOST=unix://$(podman machine inspect --format '{{.ConnectionInfo.PodmanSocket.Path}}')
 export TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock
 export TESTCONTAINERS_RYUK_DISABLED=true

 ./gradlew --info test asciidoctor
