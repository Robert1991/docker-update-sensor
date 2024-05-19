package com.robertnator.docker.update.sensor.dao.socket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpResponseWrapper {

    private final HttpResponse response;

    public HttpResponseWrapper(HttpResponse response) {
        this.response = response;
    }

    public int getStatusCode() {
        return response.getStatusLine()
            .getStatusCode();
    }

    public String getReason() {
        return response.getStatusLine()
            .getReasonPhrase();
    }

    public String getResponseAsString() throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity);
        }
        return "";
    }
}
