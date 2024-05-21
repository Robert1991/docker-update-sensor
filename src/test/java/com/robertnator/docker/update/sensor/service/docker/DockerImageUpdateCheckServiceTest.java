package com.robertnator.docker.update.sensor.service.docker;

import com.robertnator.docker.update.sensor.dao.dockerhub.DockerHubDao;
import com.robertnator.docker.update.sensor.dao.socket.DockerSocketDao;
import com.robertnator.docker.update.sensor.dao.socket.UnixSocketException;
import com.robertnator.docker.update.sensor.model.DockerUpdateInfo;
import com.robertnator.docker.update.sensor.model.dockerhub.DockerHubImageInfo;
import com.robertnator.docker.update.sensor.model.socket.DockerLocalImageInfo;
import com.robertnator.docker.update.sensor.service.DockerImageUpdateCheckException;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DockerImageUpdateCheckServiceTest {

    @Mock
    private DockerHubDao dockerHubDao;

    @Mock
    private DockerSocketDao dockerSocketDao;

    @InjectMocks
    private DockerImageUpdateCheckService serviceUnderTest;

    @Nested
    class CheckForUpdateNonErrorCases {

        @BeforeEach
        void setUp() throws JsonObjectMappingException {
            when(dockerHubDao.getLatestTags("image", "namespace", 50))
                .thenReturn(asList(
                    new DockerHubImageInfo("1", new Date(1), "latest", "latestDigest"),
                    new DockerHubImageInfo("2", new Date(2), "1.1", "latestDigest"),
                    new DockerHubImageInfo("3", new Date(3), "1.0", "versionDigest"),
                    new DockerHubImageInfo("4", new Date(4), "0.9", null)));
        }

        @Test
        void test() throws JsonObjectMappingException, UnixSocketException, DockerImageUpdateCheckException {
            when(dockerSocketDao.getImageInfo("image"))
                .thenReturn(
                    new DockerLocalImageInfo("a", singletonList("1.1"), singletonList("image@versionDigest"),
                        new Date(5)));

            assertThat(serviceUnderTest.checkForUpdate("image", "namespace"), equalTo(
                new DockerUpdateInfo(true, "image", "1.1", "latestDigest",
                    new Date(2))));
        }

        @Test
        void testImageIsUpToDate()
            throws JsonObjectMappingException, UnixSocketException, DockerImageUpdateCheckException {
            when(dockerSocketDao.getImageInfo("image"))
                .thenReturn(
                    new DockerLocalImageInfo("a", singletonList("1.1"), singletonList("latestDigest"), new Date(4)));

            assertThat(serviceUnderTest.checkForUpdate("image", "namespace"), equalTo(
                new DockerUpdateInfo(false, "image", "1.1", "latestDigest",
                    new Date(2))));
        }
    }


    @Test
    void testCheckForUpdateWhenLatestVersionIsNotFound() {
        var thrown = assertThrows(DockerImageUpdateCheckException.class,
            () -> serviceUnderTest.checkForUpdate("imageName", "namespace"));
        assertThat(thrown.getMessage(), equalTo("No info about latest version could be fetched from docker hub for " +
            "image 'imageName'"));
    }

    @Test
    void testCheckForUpdateWhenThereIsNoTagForLatestVersion()
        throws JsonObjectMappingException, DockerImageUpdateCheckException, UnixSocketException {
        when(dockerHubDao.getLatestTags("image", "namespace", 50))
            .thenReturn(asList(
                new DockerHubImageInfo("1", new Date(1), "latest", "latestDigest"),
                new DockerHubImageInfo("3", new Date(3), "1.0", "versionDigest")));

        when(dockerSocketDao.getImageInfo("image"))
            .thenReturn(
                new DockerLocalImageInfo("a", singletonList("1.1"), singletonList("image@versionDigest"), new Date(4)));

        assertThat(serviceUnderTest.checkForUpdate("image", "namespace"), equalTo(
            new DockerUpdateInfo(true, "image", "latest", "latestDigest",
                new Date(1))));
    }
}
