package com.robertnator.docker.update.sensor.service.docker;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(MockitoExtension.class)
public class VersionTagComparisonServiceTest {

    @InjectMocks
    private VersionTagComparisonService serviceUnderTest;


    @ParameterizedTest
    @MethodSource
    void testGetBestSchematicVersioningTag(List<String> actualTags, String expectedBestTag) {
        assertThat(serviceUnderTest.getBestSchematicVersioningTag(actualTags), equalTo(expectedBestTag));
    }

    static Stream<Arguments> testGetBestSchematicVersioningTag() {
        return Stream.of(
            arguments(new ArrayList<>(), null),
            arguments(asList("stable", "beta"), "stable"),
            arguments(asList("beta", "stable"), "beta"),
            arguments(asList("v2024.5.2", "2024.5.2"), "v2024.5.2"),
            arguments(asList("2024.5.2", "v2024.5.2"), "2024.5.2"),
            arguments(asList("stable", "2024.5.2", "2024.5", "beta"), "2024.5.2"),
            arguments(asList("stable", "2024.5", "2024"), "2024.5"),
            arguments(asList("stable", "beta", "2024"), "2024"),
            arguments(asList("stable", "v2024.5.2", "v2024.5", "beta"), "v2024.5.2"),
            arguments(asList("stable", "v2024.5", "v2024"), "v2024.5"),
            arguments(asList("stable", "beta", "v2024"), "v2024"),
            arguments(asList("jammy", "11.3.2-jammy", "11.3.2", "11.3-jammy", "11.3", "11-jammy", "11"), "11.3.2"),
            arguments(asList("jammy", "11.3-jammy", "11.3", "11-jammy", "11"), "11.3"),
            arguments(asList("jammy", "11-jammy", "11"), "11"),
            arguments(asList("jammy", "11-jammy"), "11"),
            arguments(asList("jammy", "11.3-jammy"), "11.3"),
            arguments(asList("jammy", "11.3.2-jammy-jam"), "11.3.2")
        );
    }
}
