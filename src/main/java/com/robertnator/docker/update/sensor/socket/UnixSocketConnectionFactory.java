package com.robertnator.docker.update.sensor.socket;

import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class UnixSocketConnectionFactory implements ConnectionSocketFactory {
    private final File socketFile;

    public UnixSocketConnectionFactory(File socketFile) {
        this.socketFile = socketFile;
    }

    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        return AFUNIXSocket.newInstance();
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket sock, HttpHost host, InetSocketAddress remoteAddress,
            InetSocketAddress localAddress, HttpContext context)
            throws IOException {
        if (!(sock instanceof AFUNIXSocket unixSocket)) {
            throw new IllegalArgumentException("Socket not of type AFUNIXSocket");
        }
        try {
            unixSocket.connect(AFUNIXSocketAddress.of(socketFile), connectTimeout);
        } catch (SocketTimeoutException e) {
            throw new ConnectTimeoutException(e.getMessage());
        }
        return unixSocket;
    }
}
