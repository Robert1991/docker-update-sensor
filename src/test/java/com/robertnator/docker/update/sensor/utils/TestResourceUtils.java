package com.robertnator.docker.update.sensor.utils;

import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.readString;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.fail;

public class TestResourceUtils {

    public static String getTestResourceContent(TestInfo testInfo, String fileExtension) {
        return getTestResourceContent(testInfo, EMPTY, fileExtension);
    }

    public static String getTestResourceContent(TestInfo testInfo, String fileAppendix, String fileExtension) {
        Path testResourcePath = getTestResourcePath(testInfo, fileAppendix, fileExtension);
        try {
            return readString(testResourcePath);
        } catch (IOException e) {
            fail("failed to read " + testResourcePath);
            return null;
        }
    }

    public static Path getTestResourcePath(TestInfo testInfo, String fileExtension) {
        return getTestResourcePath(testInfo, EMPTY, fileExtension);
    }

    public static Path getTestResourcePath(TestInfo testInfo, String fileAppendix, String fileExtension) {
        return Path.of("src/test/resources", testInfo.getTestClass().get().getSimpleName(),
            testInfo.getTestMethod().get().getName() + fileAppendix + fileExtension);
    }
}
