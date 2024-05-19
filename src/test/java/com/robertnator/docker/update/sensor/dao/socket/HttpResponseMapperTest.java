package com.robertnator.docker.update.sensor.socket;

import com.robertnator.docker.update.sensor.dao.socket.HttpResponseWrapper;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HttpResponseMapperTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    private HttpResponse httpResponse;

    @InjectMocks
    private HttpResponseWrapper objectUnderTest;

    @Mock
    private MockedStatic<EntityUtils> mockedEntityUtils;

    @Test
    void testGetStatusCode() {
        when(httpResponse.getStatusLine().getStatusCode()).thenReturn(401);

        assertThat(objectUnderTest.getStatusCode(), equalTo(401));
    }

    @Test
    void testGetStatusReason() {
        when(httpResponse.getStatusLine().getReasonPhrase()).thenReturn("reason");

        assertThat(objectUnderTest.getReason(), equalTo("reason"));
    }

    @Test
    void testGetResponseAsString() throws IOException {
        mockedEntityUtils.when(() -> EntityUtils.toString(httpResponse.getEntity()))
            .thenReturn("response");

        assertThat(objectUnderTest.getResponseAsString(), equalTo("response"));
    }

    @Test
    void testGetResponseAsStringWhenEmpty() throws IOException {
        when(httpResponse.getEntity()).thenReturn(null);

        assertThat(objectUnderTest.getResponseAsString(), is(emptyString()));
    }
}
