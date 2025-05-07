#!/bin/bash

GLVD_API_IMAGE_REPOSITORY=${1:-ghcr.io/gardenlinux/glvd-api}
GLVD_API_IMAGE_TAG=${2:-latest}

build () {
    local ARCH="${1}"; shift

    SHA_GLVD=$(podman pull -q --arch="$ARCH" $GLVD_API_IMAGE_REPOSITORY:$GLVD_API_IMAGE_TAG)
    SHA_GL=$(podman pull -q --arch="$ARCH" ghcr.io/gardenlinux/gardenlinux:1592)

    ./unbase_oci --exclude exclude --include include --ldd-dependencies --print-tree podman:"$SHA_GL" podman:"$SHA_GLVD" podman:ghcr.io/gardenlinux/glvd-api:latest-linux${ARCH}_bare

}

build amd64
build arm64
