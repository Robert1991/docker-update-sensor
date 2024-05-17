package com.robertnator.docker.update.sensor.socket;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.File;

public class UnixSocketHttpClientFactory {
    public UnixSocketHttpClient createUnixSocketHttpClient(File socketFile) {
        ConnectionSocketFactory socketFactory = new UnixSocketConnectionFactory(socketFile);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", socketFactory)
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        return new UnixSocketHttpClient(HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build());
    }
}
