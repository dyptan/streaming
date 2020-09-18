FROM openjdk:8-jre-slim
ADD target/dist/lib /libs
ADD target/dist/*.jar app.jar
ENTRYPOINT ["java", "-cp", "/libs/*:app.jar", "com.dyptan.Main"]