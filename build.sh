#!/bin/bash
mvn clean package -DskipTests -pl web
# docker build --rm -f "streaming/Dockerfile" -t diptan/streaming:latest streaming
docker build --rm -f "web/Dockerfile" -t diptan/web:latest web
docker push diptan/web
kubectl delete -f docker/k8s/web-deployment.yaml
kubectl apply -f docker/k8s/web-deployment.yaml

# kubectl delete -f docker/k8s/streaming-deployment.yaml
# kubectl apply -f docker/k8s