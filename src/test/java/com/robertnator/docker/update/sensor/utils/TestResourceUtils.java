package com.robertnator.docker.update.sensor.utils;

import org.junit.jupiter.api.TestInfo;

import java.nio.file.Path;

public class TestResourceUtils {

    public static Path getTestResourcePath(TestInfo testInfo) {
        return Path.of("src/test/resources", testInfo.getTestClass().get().getSimpleName(),
            testInfo.getTestMethod().get().getName() + ".json");
    }
}
