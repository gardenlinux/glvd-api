#!/bin/bash

podman compose --file unit-test-db-compose.yaml down --volumes --remove-orphans
