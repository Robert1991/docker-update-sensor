package com.robertnator.docker.update.sensor.dao.socket;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class UnixSocketHttpClientFactory {

    public UnixSocketHttpClient createUnixSocketHttpClient(File socketFile) {
        ConnectionSocketFactory socketFactory = new UnixSocketConnectionFactory(socketFile);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", socketFactory)
            .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
            socketFactoryRegistry);
        return new UnixSocketHttpClient(HttpClients.custom()
            .setConnectionManager(connectionManager)
            .build());
    }
}
