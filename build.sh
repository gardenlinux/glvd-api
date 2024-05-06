#!/usr/bin/env bash

set -eufo pipefail

./gradlew bootJar

podman build --tag ghcr.io/gardenlinux/glvd-api:edge .

podman save --format oci-archive ghcr.io/gardenlinux/glvd-api:edge > glvd.oci


podman pull ubuntu:22.04
podman save --format oci-archive ubuntu:22.04 > ubuntu.oci

./unbase_oci --exclude exclude --include include --ldd-dependencies --print-tree ubuntu.oci glvd.oci glvd_bare.oci

image="$(podman load < glvd_bare.oci | awk '{ print $NF }')"
podman tag "$image" ghcr.io/gardenlinux/glvd-api:edge_bare
