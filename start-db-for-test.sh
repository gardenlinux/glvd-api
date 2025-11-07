#!/bin/bash

podman build -f TestDb.Containerfile -t glvd-postgres-unit-test-image
podman run --rm --publish 9876:5432 --env POSTGRES_USER=glvd --env POSTGRES_DB=glvd --env POSTGRES_PASSWORD=glvd --name=glvd-postgres-unit-test  localhost/glvd-postgres-unit-test-image:latest