FROM ghcr.io/gardenlinux/glvd-postgres:latest

COPY glvd.sql /docker-entrypoint-initdb.d/glvd.sql
