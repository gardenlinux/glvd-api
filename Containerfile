FROM ghcr.io/sap/sapmachine:25-jdk-headless-gl-1877 as build

ARG GLVD_VERSION=dev

RUN apt-get update -q && apt-get install -y binutils
RUN mkdir /usr/src/glvd
COPY . /usr/src/glvd
WORKDIR /usr/src/glvd
COPY build/libs/glvd-${GLVD_VERSION}.jar glvd.jar
RUN jar xf glvd.jar
RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 25  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    glvd.jar > deps.info
RUN jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --output /tinysapmachine

FROM ghcr.io/gardenlinux/gardenlinux:1877.10

ARG GLVD_VERSION=dev

LABEL org.opencontainers.image.source="https://github.com/gardenlinux/glvd-api"
LABEL org.opencontainers.image.description="Garden Linux Vulnerability Database API"
LABEL org.opencontainers.image.version=$GLVD_VERSION

EXPOSE 8080

COPY --from=build /tinysapmachine /jre
COPY --from=build /usr/src/glvd/glvd.jar /
COPY version.txt /

CMD ["/jre/bin/java", "-jar", "/glvd.jar"]
