#!/bin/bash

./gradlew bootJar
scp build/libs/docker.update.sensor-0.0.1-SNAPSHOT.jar robert@rpn-home-server:/home/robert/

ssh robert@rpn-home-server "java -jar docker.update.sensor-0.0.1-SNAPSHOT.jar"