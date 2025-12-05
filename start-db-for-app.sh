#!/bin/bash

podman build -f DevDb.Containerfile -t glvd-postgres-app-image
podman run --rm --publish 5432:5432 --env POSTGRES_USER=glvd --env POSTGRES_DB=glvd --env POSTGRES_PASSWORD=glvd --name=glvd-postgres-app  localhost/glvd-postgres-app-image:latest
