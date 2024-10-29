FROM ghcr.io/sap/sapmachine:21-jdk-headless-gl-1592 as build
RUN apt-get update -q && apt-get install -y binutils
RUN mkdir /usr/src/glvd
COPY . /usr/src/glvd
WORKDIR /usr/src/glvd
RUN ./gradlew build -x asciidoctor -x test
RUN jar xf build/libs/glvd-0.0.1-SNAPSHOT.jar
RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 21  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    build/libs/glvd-0.0.1-SNAPSHOT.jar > deps.info
RUN jlink \
    --add-modules $(cat deps.info) \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /tinysapmachine

FROM ghcr.io/gardenlinux/gardenlinux:1592

EXPOSE 8080

ENV JAVA_HOME /user/java/jdk17
ENV PATH $JAVA_HOME/bin:$PATH
COPY --from=build /tinysapmachine $JAVA_HOME
RUN mkdir /glvd
COPY --from=build /usr/src/glvd/build/libs/glvd-0.0.1-SNAPSHOT.jar /glvd/
WORKDIR /glvd
ENTRYPOINT java -jar glvd-0.0.1-SNAPSHOT.jar
