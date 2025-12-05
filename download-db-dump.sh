#!/bin/bash

LATEST_RUN_ID=$(gh run list --repo gardenlinux/glvd-data-ingestion --branch main --workflow 02-ingest-dump-snapshot.yaml --json databaseId --limit 1 | jq -r '.[0].databaseId')
gh run download $LATEST_RUN_ID -n glvd.sql --repo gardenlinux/glvd-data-ingestion
