FROM ghcr.io/gardenlinux/glvd-postgres:latest

COPY src/test/resources/test-data/01-schema.sql /docker-entrypoint-initdb.d/01-schema.sql
COPY src/test/resources/test-data/02-sample-data.sql /docker-entrypoint-initdb.d/02-sample-data.sql
