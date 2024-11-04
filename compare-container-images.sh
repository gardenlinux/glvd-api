#!/bin/bash

# This script is intended to compare the various ways to build container images
# This is not intended to be merged on the main branch or to be used in ci

podman build -q -t ghcr.io/gardenlinux/glvd-api:compare_full_image -f Containerfile.original .

BARE_SHA=$(podman build -q -t ghcr.io/gardenlinux/glvd-api:compare_jlink_image .)

GLVD_API_IMAGE_REPOSITORY=ghcr.io/gardenlinux/glvd-api
GLVD_API_IMAGE_TAG=compare_jlink_image

build_bare () {
    local ARCH="${1}"; shift

    podman save --format oci-archive "$BARE_SHA" > glvd-"$ARCH".oci

    SHA_GL=$(podman pull -q --arch="$ARCH" ghcr.io/gardenlinux/gardenlinux:1592)
    podman save --format oci-archive "$SHA_GL" > gardenlinux-"$ARCH".oci

    ./unbase_oci --exclude exclude --include include --ldd-dependencies gardenlinux-"$ARCH".oci glvd-"$ARCH".oci glvd_bare-"$ARCH".oci

    image="$(podman load < glvd_bare-"$ARCH".oci | awk '{ print $NF }')"
    podman tag "$image" $GLVD_API_IMAGE_REPOSITORY:$GLVD_API_IMAGE_TAG-linux"$ARCH"_bare
}

build_bare arm64

echo Comparision of container image types for glvd-api
echo Size of the Spring Boot jar:
du -sch build/libs/glvd-0.0.1-SNAPSHOT.jar | grep jar
echo Compare image sizes:
podman images | grep compare | tac
