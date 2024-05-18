package com.robertnator.docker.update.sensor.socket;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;

import static com.robertnator.docker.update.sensor.matcher.HttpUriRequestMatcher.argThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnixSocketHttpClientTest {

    @Mock
    private CloseableHttpClient httpClient;

    @InjectMocks
    private UnixSocketHttpClient objectUnderTest;

    @Test
    void testGet(@Mock(answer = Answers.RETURNS_DEEP_STUBS) CloseableHttpResponse httpResponse) throws Exception {
        when(httpResponse.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine().getReasonPhrase()).thenReturn("OK");
        when(httpClient.execute(argThat(new HttpGet(new URI("test")))))
                .thenReturn(httpResponse);

        HttpResponseWrapper actualHttpResponse = objectUnderTest.get(new URI("test"));

        assertThat(actualHttpResponse.getStatusCode(), equalTo(200));
        assertThat(actualHttpResponse.getReason(), equalTo("OK"));
    }

    @Test
    void testClose() throws IOException {
        objectUnderTest.close();

        verify(httpClient).close();
    }

    @AfterEach
    void verifyNoMoreInteractionsOnHttpClient() {
        verifyNoMoreInteractions(httpClient);
    }
}
