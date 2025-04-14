#!/bin/bash

# pull images as a workaround because there is some issue with the podman compose up --pull flag

podman pull ghcr.io/gardenlinux/glvd-init
podman pull ghcr.io/gardenlinux/glvd-postgres
podman pull ghcr.io/gardenlinux/glvd-api
