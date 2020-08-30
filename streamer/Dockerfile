FROM openjdk:8-jre-slim
ADD target/StreamTransformer/lib /libs
ADD target/StreamTransformer/StreamTransformer.jar app.jar
ENTRYPOINT ["java", "-cp", "/libs/*:app.jar", "com.dyptan.Main"]