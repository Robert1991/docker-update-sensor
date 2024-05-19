package com.robertnator.docker.update.sensor.socket;

import com.robertnator.docker.update.sensor.dao.json.JsonObjectMappingException;
import com.robertnator.docker.update.sensor.dao.json.JsonObjectMappingService;
import com.robertnator.docker.update.sensor.dao.socket.DockerLocalImageInfo;
import com.robertnator.docker.update.sensor.dao.socket.DockerSocketDao;
import com.robertnator.docker.update.sensor.dao.socket.UnixSocketDao;
import com.robertnator.docker.update.sensor.dao.socket.UnixSocketException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Date;

import static com.robertnator.docker.update.sensor.dao.socket.DockerSocketDao.DOCKER_UNIX_SOCKET;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DockerSocketDaoTest {

    @Mock
    private UnixSocketDao unixSocketDao;

    @Mock
    private JsonObjectMappingService jsonObjectMappingService;

    @InjectMocks
    private DockerSocketDao daoUnderTest;

    @Test
    void testGetImageInfo() throws UnixSocketException, JsonObjectMappingException {
        when(unixSocketDao.get(new File(DOCKER_UNIX_SOCKET), "/images/docker/image/name/json"))
            .thenReturn("{ jsonResponse }");
        DockerLocalImageInfo expectedImageInfo = new DockerLocalImageInfo("id", singletonList("RepoTag"),
            singletonList("RepoDigest"), new Date());
        when(jsonObjectMappingService.mapToClass("{ jsonResponse }", DockerLocalImageInfo.class))
            .thenReturn(expectedImageInfo);

        DockerLocalImageInfo actualImageInfo = daoUnderTest.getImageInfo("docker/image/name");

        MatcherAssert.assertThat(actualImageInfo, equalTo(expectedImageInfo));
    }
}
