# Docker Update Sensor

The docker update sensor is a little rest service created with spring boot framework to check if there are new latest images available for docker containers you
are running. This tool therefore communicates with docker hub and the local docker socket on your machine to determine if your local docker image is outdated using
the image digests.
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

The web service only offers one endpoint you can communicate with using HTTP GET. It will tell you if there is a new latest image for your running docker container. If your
image name is e.g. "home-assistant/homeassistant" and the docker update sensor is running on a server "home-server" with exposed port 8080, request and response 
would look like this:

```bash
curl http://home-server:8080//api/check/update?image=homeassistant/home-assistant
```

Response:

```bash
{
  "updateAvailable" : true,
  "imageName" : "homeassistant/home-assistant",
  "latestVersionTag" : "2024.5",
  "latestVersionDigest" : "sha256:6f5eeb8360d9d58ff096c7259366993b4b01ebe11251c2b83c9329daad441b00",
  "updated" : "2024-05-17T13:31:44.530+00:00"
}
```