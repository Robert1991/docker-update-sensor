package com.robertnator.docker.update.sensor.dao.socket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnixSocketDaoTest {

    @Mock
    private UnixSocketHttpClientFactory unixSocketHttpClientFactory;

    @InjectMocks
    private UnixSocketDao daoUnderTest;

    @Mock
    private UnixSocketHttpClient httpClient;

    @Mock
    private File socketFile;

    @BeforeEach
    void setUp() {
        when(unixSocketHttpClientFactory.createUnixSocketHttpClient(socketFile))
            .thenReturn(httpClient);
    }

    @Test
    void testGet(@Mock HttpResponseWrapper httpResponse) throws IOException, UnixSocketException {
        when(httpResponse.getStatusCode()).thenReturn(200);
        when(httpResponse.getResponseAsString()).thenReturn("response");
        when(httpClient.get(URI.create("http://localhost/foo/test")))
            .thenReturn(httpResponse);

        assertThat(daoUnderTest.get(socketFile, "foo/test")).isEqualTo("response");
    }

    @Test
    void testGetWhenThereIsAnErrorClosingHttpClient(@Mock HttpResponseWrapper httpResponse) throws IOException {
        when(httpResponse.getStatusCode()).thenReturn(200);
        when(httpResponse.getResponseAsString()).thenReturn("response");
        when(httpClient.get(URI.create("http://localhost/foo/test")))
            .thenReturn(httpResponse);
        IOException expectedInnerException = new IOException("some io problem");
        doThrow(expectedInnerException).when(httpClient).close();

        assertThatException().isThrownBy(() -> daoUnderTest.get(socketFile, "foo/test"))
            .isInstanceOf(UnixSocketException.class)
            .withMessage("Unable to close http client.")
            .withCause(expectedInnerException);
    }

    @Test
    void testGetWhenThereIsAnErrorOnQueryToSocket() throws IOException {
        IOException expectedInnerException = new IOException("some error on query to socket");
        when(httpClient.get(URI.create("http://localhost/foo/test")))
            .thenThrow(expectedInnerException);

        assertThatException().isThrownBy(() -> daoUnderTest.get(socketFile, "foo/test"))
            .isInstanceOf(UnixSocketException.class)
            .withMessage("Error on GET query " + "http://localhost/foo/test" + " to: " + socketFile)
            .withCause(expectedInnerException);
    }

    @Test
    void testGetWhenResponseCanNotBeUnpacked(@Mock HttpResponseWrapper httpResponse) throws IOException {
        when(httpResponse.getStatusCode()).thenReturn(200);
        IOException expectedInnerException = new IOException("some problem creating client to socket");
        when(httpResponse.getResponseAsString())
            .thenThrow(expectedInnerException);
        when(httpClient.get(URI.create("http://localhost/foo/test")))
            .thenReturn(httpResponse);

        assertThatException().isThrownBy(() -> daoUnderTest.get(socketFile, "foo/test"))
            .isInstanceOf(UnixSocketException.class)
            .withMessage("Unable to unpack response from query to unix socket.")
            .withCause(expectedInnerException);
    }

    @Test
    void testGetWhenResponseHasErrorCode(@Mock HttpResponseWrapper httpResponse) throws IOException {
        when(httpResponse.getStatusCode()).thenReturn(500);
        when(httpResponse.getReason()).thenReturn("unable to read socket");
        when(httpClient.get(URI.create("http://localhost/foo/test")))
            .thenReturn(httpResponse);

        assertThatException().isThrownBy(() -> daoUnderTest.get(socketFile, "foo/test"))
            .isInstanceOf(UnixSocketException.class)
            .withMessage("Unexpected status code '500' on query 'http://localhost/foo/test' " +
                "to socket: " + socketFile + "\nReason: unable to read socket");
    }
}
