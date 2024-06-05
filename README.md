# Docker Update Sensor

The docker update sensor is a little rest service created with spring boot framework to check if there are new images available for docker containers you are running. 
This tool therefore communicates with docker hub and the local docker socket on your machine to determine if your local docker image is outdated using the image 
digests.
This tool is useful if you are running for example a docker mirco service based smart home system and want to get informed about new updates of the single micro
services you are using such as a [Home-Assistant and Docker based smart home infrastructure](https://github.com/Robert1991/homeassistant_docker_compose).

## Running the service

Use the following docker run command to get the service running:


```bash
docker run -v /var/run/docker.sock:/var/run/docker.sock -p 8080:8080 robert1991/docker-update-sensor
```

- It needs access to the docker socket on your local machine, normally: `/var/run/docker.sock`
- It exposes one port where the api will be callable

Or see the docker compose file within this repository on how to run the service.

## Usage

The web service only offers one endpoint you can communicate with using HTTP GET. It will tell you if there is a new latest image for the docker container image you previously pulled
from docker hub. If your image name is e.g. "home-assistant/homeassistant" or "mariadb" and the docker update sensor is running on a server "home-server" with exposed port 8080, 
request and response would look like this:

```bash
curl http://home-server:8080//api/check/update?image=homeassistant/home-assistant
```

Response:

```bash
{
  "updateAvailable" : true,
  "imageName" : "homeassistant/home-assistant",
  "latestVersionTag" : "2024.5.5",
  "latestVersionTags" : [ "2024.5", "stable", "2024.5.5" ],
  "latestVersionDigest" : "sha256:031d355a2e52e82fc33cd6854753fb19fc5d6a31af6f0f54c277d6f118ad993e",
  "currentVersionTag" : "2024.5.2",
  "currentVersionTags" : [ "2024.5.2" ],
  "currentVersionDigest" : "homeassistant/home-assistant@sha256:dfe252fdd57c929e678e741690e97099a38f9adff8ae87a4a7972a7b8ed61a32",
  "updated" : "2024-05-24T20:44:26.118+00:00"
}
```

This only works if you pulled the image from docker hub with the `latest` tag. If you pulled a docker image with a specific version tag, you'll have to use it in the query:

```bash
curl http://home-server:8080//api/check/update?image=mariadb:11
```

This will compare the digest of your local mariadb image with the latest found on docker hub. This allows you to compare multiple docker images if you have more than 
one image of the same docker container running e.g. `mariadb:latest` and `mariadb:11`.