#!/bin/bash

WORKING_DIR="/home/robert/automations/docker-update-sensor/"

./gradlew bootJar
scp build/libs/docker.update.sensor-1.0.1-SNAPSHOT.jar robert@rpn-home-server:$WORKING_DIR/build/libs/
scp Dockerfile robert@rpn-home-server:$WORKING_DIR/Dockerfile

ssh robert@rpn-home-server "cd $WORKING_DIR && docker build -t docker-update-sensor-test ."
ssh robert@rpn-home-server "docker run -v /var/run/docker.sock:/var/run/docker.sock -p 8090:8080 docker-update-sensor-test"