FROM openjdk:8-jre-slim
ADD target/dist/lib /libs
ADD target/dist/conf /conf
ADD target/dist/Trainer.jar app.jar
ENTRYPOINT ["java", "-cp", "/libs/*:/conf/:app.jar", "com.dyptan.TrainerGateway"]
EXPOSE 8081