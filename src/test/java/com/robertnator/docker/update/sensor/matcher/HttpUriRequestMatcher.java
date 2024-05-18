package com.robertnator.docker.update.sensor.matcher;

import org.apache.http.client.methods.HttpUriRequest;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;

public class HttpUriRequestMatcher extends TypeSafeMatcher<HttpUriRequest> {

    private final HttpUriRequest expected;

    public static HttpUriRequestMatcher matches(HttpUriRequest expected) {
        return new HttpUriRequestMatcher(expected);
    }

    private HttpUriRequestMatcher(HttpUriRequest expected) {
        this.expected = expected;
    }

    public static HttpUriRequest argThat(HttpUriRequest expected) {
        HttpUriRequestMatcher matcher = matches(expected);
        return ArgumentMatchers.argThat(matcher.createArgumentMatcher());
    }

    @Override
    protected boolean matchesSafely(HttpUriRequest actualHttpRequest) {
        return expected.getURI().equals(actualHttpRequest.getURI()) &&
                expected.getMethod().equals(actualHttpRequest.getMethod()) &&
                expected.getProtocolVersion().equals(actualHttpRequest.getProtocolVersion());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Expected http request equal to:")
                .appendText("URI: ").appendValue(expected.getURI())
                .appendText("Method: ").appendValue(expected.getMethod())
                .appendText("ProtocolVersion: ").appendValue(expected.getProtocolVersion());
    }

    protected ArgumentMatcher<HttpUriRequest> createArgumentMatcher() {
        return new ArgumentMatcher<>() {

            @Override
            public boolean matches(HttpUriRequest arg0) {
                return matchesSafely(arg0);
            }

            @Override
            public String toString() {
                return "Matches: " + expected;
            }
        };
    }
}
