FROM ghcr.io/sap/sapmachine:21-jre-headless-gl-1592

RUN mkdir /opt/glvd
COPY build/libs/glvd-0.0.1-SNAPSHOT.jar /opt/glvd/glvd.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/glvd/glvd.jar"]
