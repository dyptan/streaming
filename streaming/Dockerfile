FROM openjdk:8u111-jdk-alpine
VOLUME /tmp
ADD target/streaming-1.1-SNAPSHOT-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]