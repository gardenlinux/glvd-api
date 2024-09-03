#!/bin/bash

./gradlew bootJar
podman compose up --build --force-recreate --pull
