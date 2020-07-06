FROM openjdk:8-jre-slim
# COPY trainedModel/ /streaming/trainedModel
ADD target/ModelTrainer/lib /libs
ADD target/ModelTrainer/TrainerRest.jar app.jar
ENTRYPOINT ["java", "-cp", "/libs/*:app.jar", "com.dyptan.akka.AkkaHTTP"]
EXPOSE 8088