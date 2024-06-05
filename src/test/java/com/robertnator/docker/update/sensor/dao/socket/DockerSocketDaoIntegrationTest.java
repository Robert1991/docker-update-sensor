package com.robertnator.docker.update.sensor.dao.socket;

import com.robertnator.docker.update.sensor.model.socket.DockerLocalImageInfo;
import com.robertnator.docker.update.sensor.service.json.JsonObjectMappingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.robertnator.docker.update.sensor.dao.socket.DockerSocketDao.DOCKER_UNIX_SOCKET;
import static com.robertnator.docker.update.sensor.utils.DateUtils.toDate;
import static com.robertnator.docker.update.sensor.utils.TestResourceUtils.getTestResourceContent;
import static com.robertnator.docker.update.sensor.utils.TestResourceUtils.getTestResourcePath;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DockerSocketDaoIntegrationTest {

    @MockBean
    private UnixSocketDao unixSocketDao;

    @Autowired
    private DockerSocketDao daoUnderTest;

    @Test
    void testGetImageInfo(TestInfo testInfo) throws IOException, UnixSocketException, JsonObjectMappingException {
        String sampleImageInfoJsonString = Files.readString(getTestResourcePath(testInfo, ".json"));

        when(unixSocketDao.get(new File(DOCKER_UNIX_SOCKET), "/images/imageName/json"))
            .thenReturn(sampleImageInfoJsonString);

        DockerLocalImageInfo actualLocalImageInfo = daoUnderTest.getImageInfo("imageName");

        assertThat(actualLocalImageInfo, equalTo(
            new DockerLocalImageInfo("sha256:b23433ac1b2accddcf0e5089b1b7cd4a1e5bb8d2d1ce3c9f6ecfcd15b9fd99d2",
                singletonList("koenkk/zigbee2mqtt:latest"),
                singletonList(
                    "koenkk/zigbee2mqtt@sha256:32901c8b100ee4d04123eb714523a023ca4aba7946e504209c7d773d07f697b5"),
                toDate("2024-04-01T18:02:30.665927498Z"))));
    }

    @Test
    void testGetImageInfoWithMissingTag(TestInfo testInfo) throws Exception {
        doTestGetImageInfoErrorCase(testInfo);
    }

    @Test
    void testGetImageInfoWithUnknownTag(TestInfo testInfo) throws Exception {
        doTestGetImageInfoErrorCase(testInfo);
    }

    @Test
    void testGetImageInfoWithEmptyRequiredTag(TestInfo testInfo) throws Exception {
        doTestGetImageInfoErrorCase(testInfo);
    }

    @Test
    void testGetImageInfoWhenRequiredTagHasMoreThanOneElement(TestInfo testInfo) throws Exception {
        doTestGetImageInfoErrorCase(testInfo);
    }

    private void doTestGetImageInfoErrorCase(TestInfo testInfo) throws Exception {
        String sampleImageInfoJsonString = getTestResourceContent(testInfo, ".json");

        when(unixSocketDao.get(new File(DOCKER_UNIX_SOCKET), "/images/imageName/json"))
            .thenReturn(sampleImageInfoJsonString);

        assertThrows(JsonObjectMappingException.class, () -> daoUnderTest.getImageInfo("imageName"));
    }
}
