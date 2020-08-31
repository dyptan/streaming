#!/bin/bash
mvn clean package -DskipTests

docker build --rm -f streamer/Dockerfile -t diptan/streamer:latest streamer
docker build --rm -f trainer/Dockerfile -t diptan/trainer:latest trainer
docker build --rm -f web/Dockerfile -t diptan/web:latest web

docker push diptan/web
docker push diptan/trainer
docker push diptan/streamer

# docker run -p 8081:8081 --name trainer diptan/trainer 
# docker run -p 8080:8080 --name web diptan/web 
# docker run --name treamer diptan/streamer


