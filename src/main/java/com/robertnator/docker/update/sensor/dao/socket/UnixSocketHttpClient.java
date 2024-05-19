package com.robertnator.docker.update.sensor.socket;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

public class UnixSocketHttpClient implements Closeable {

    private final CloseableHttpClient httpClient;

    public UnixSocketHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpResponseWrapper get(URI uri) throws IOException {
        return new HttpResponseWrapper(httpClient.execute(new HttpGet(uri)));
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}
