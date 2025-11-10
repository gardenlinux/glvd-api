FROM ghcr.io/sap/sapmachine:25-jdk-headless-gl-1592 as build
RUN apt-get update -q && apt-get install -y binutils
RUN mkdir /usr/src/glvd
COPY . /usr/src/glvd
WORKDIR /usr/src/glvd
COPY build/libs/glvd-dev.jar glvd-dev.jar
RUN jar xf glvd-dev.jar
RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 25  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    glvd-dev.jar > deps.info
RUN jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --output /tinysapmachine

FROM ghcr.io/gardenlinux/gardenlinux:1592

EXPOSE 8080

COPY --from=build /tinysapmachine /jre
COPY --from=build /usr/src/glvd/glvd-dev.jar /

CMD ["/jre/bin/java", "-jar", "/glvd-dev.jar"]
