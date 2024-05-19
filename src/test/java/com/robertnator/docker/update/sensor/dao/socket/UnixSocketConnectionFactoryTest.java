package com.robertnator.docker.update.sensor.dao.socket;

import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class UnixSocketConnectionFactoryTest {

    @Mock
    private File socketFile;

    @InjectMocks
    private UnixSocketConnectionFactory factoryUnderTest;

    @Mock
    private MockedStatic<AFUNIXSocketAddress> mockedAFUNIXSocketAddress;

    @Test
    void testCreateSocket(@Mock HttpContext httpContext) throws IOException {
        assertThat(factoryUnderTest.createSocket(httpContext), instanceOf(AFUNIXSocket.class));

        verifyNoInteractions(httpContext);
    }

    @Test
    void testConnectSocket(@Mock AFUNIXSocketAddress expectedSocketAddress, @Mock AFUNIXSocket unixSocket,
        @Mock HttpHost httpHost, @Mock InetSocketAddress remoteAddress,
        @Mock InetSocketAddress localAddress, @Mock HttpContext context) throws IOException {
        mockedAFUNIXSocketAddress.when(() -> AFUNIXSocketAddress.of(socketFile)).thenReturn(expectedSocketAddress);

        Socket actualSocket = factoryUnderTest.connectSocket(123, unixSocket, httpHost, remoteAddress, localAddress,
            context);

        assertThat(actualSocket, is(unixSocket));
        verify(unixSocket).connect(expectedSocketAddress, 123);
        verifyNoInteractions(httpHost, remoteAddress, localAddress, context);
        verifyNoMoreInteractions(unixSocket);
    }

    @Test
    void testConnectSocketTimeoutException(@Mock AFUNIXSocketAddress expectedSocketAddress,
        @Mock AFUNIXSocket unixSocket, @Mock HttpHost httpHost, @Mock InetSocketAddress remoteAddress,
        @Mock InetSocketAddress localAddress, @Mock HttpContext context) throws IOException {
        mockedAFUNIXSocketAddress.when(() -> AFUNIXSocketAddress.of(socketFile)).thenReturn(expectedSocketAddress);

        doThrow(new SocketTimeoutException("timeout"))
            .when(unixSocket).connect(expectedSocketAddress, 123);

        var thrown = assertThrows(ConnectTimeoutException.class, () -> factoryUnderTest.connectSocket(123,
            unixSocket, httpHost, remoteAddress, localAddress, context));

        assertThat(thrown.getMessage(), is("timeout"));
        verifyNoInteractions(httpHost, remoteAddress, localAddress, context);
        verifyNoMoreInteractions(unixSocket);
    }

    @Test
    void testConnectSocketUnsupportedSocket(@Mock Socket socket, @Mock HttpHost httpHost,
        @Mock InetSocketAddress remoteAddress, @Mock InetSocketAddress localAddress, @Mock HttpContext context) {
        var thrown = assertThrows(IllegalArgumentException.class,
            () -> factoryUnderTest.connectSocket(123, socket, httpHost, remoteAddress, localAddress,
                context));
        assertThat(thrown.getMessage(), equalTo("Socket not of type AFUNIXSocket"));
        verifyNoInteractions(socket, httpHost, remoteAddress, localAddress, context);
    }
}
