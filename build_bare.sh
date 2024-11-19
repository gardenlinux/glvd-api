#!/bin/bash

set -x

GLVD_API_IMAGE_REPOSITORY=ghcr.io/gardenlinux/glvd-api
GLVD_API_IMAGE_TAG=latest

build () {
    local ARCH="${1}"; shift

    # SHA_GLVD=$(podman pull -q --arch="$ARCH" $GLVD_API_IMAGE_REPOSITORY:$GLVD_API_IMAGE_TAG)
    # SHA_GL=$(podman pull -q --arch="$ARCH" ghcr.io/gardenlinux/gardenlinux:1592)

    # ./unbase_oci --arch "$ARCH" --exclude exclude --include include --ldd-dependencies --print-tree podman:ghcr.io/gardenlinux/gardenlinux:1592@sha256:$SHA_GL podman:ghcr.io/gardenlinux/glvd-api:latest@sha256:$SHA_GLVD :latest-linux${ARCH}_bare

    ./unbase_oci --arch "$ARCH" --exclude exclude --include include --ldd-dependencies --print-tree podman:ghcr.io/gardenlinux/gardenlinux:1592 podman:ghcr.io/gardenlinux/glvd-api:latest :latest-linux${ARCH}_bare

}

build amd64
build arm64
