#!/bin/bash

podman compose --file unit-test-db-compose.yaml up --build --wait --detach
