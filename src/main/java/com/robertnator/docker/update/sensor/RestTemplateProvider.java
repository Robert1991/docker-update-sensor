package com.robertnator.docker.update.sensor;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateProvider {

    public RestTemplate create() {
        return new RestTemplate();
    }
}
