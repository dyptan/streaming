FROM openjdk:8-jre-slim
VOLUME /tmp
ADD target/StreamTransformer/lib /libs
ADD target/StreamTransformer/StreamTransformer.jar app.jar
ADD entrypoint.sh entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]