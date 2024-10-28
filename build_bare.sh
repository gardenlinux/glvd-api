#!/bin/bash

GLVD_API_IMAGE_REPOSITORY=ghcr.io/gardenlinux/glvd-api
GLVD_API_IMAGE_TAG=latest

build () {
    local ARCH="${1}"; shift

    SHA_GLVD=$(podman pull -q --arch="$ARCH" $GLVD_API_IMAGE_REPOSITORY:$GLVD_API_IMAGE_TAG)
    podman save --format oci-archive "$SHA_GLVD" > glvd-"$ARCH".oci

    SHA_GL=$(podman pull -q --arch="$ARCH" ghcr.io/gardenlinux/gardenlinux:1592)
    podman save --format oci-archive "$SHA_GL" > gardenlinux-"$ARCH".oci

    ./unbase_oci --exclude exclude --include include --ldd-dependencies --print-tree gardenlinux-"$ARCH".oci glvd-"$ARCH".oci glvd_bare-"$ARCH".oci

    image="$(podman load < glvd_bare-"$ARCH".oci | awk '{ print $NF }')"
    podman tag "$image" $GLVD_API_IMAGE_REPOSITORY:$GLVD_API_IMAGE_TAG-linux"$ARCH"_bare
}

build amd64
build arm64
