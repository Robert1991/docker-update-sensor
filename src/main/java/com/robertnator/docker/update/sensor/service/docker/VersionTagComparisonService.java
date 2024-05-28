package com.robertnator.docker.update.sensor.service.docker;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.regex.Pattern.compile;

@Service
public class VersionTagComparisonService {

    private static final Pattern THREE_DIGIT_VERSION_PATTERN = compile(
        "^([vV])?(\\d+\\.)(\\d+\\.)(\\*|\\d+)(-[0-9A-Za-z-]+)?$");
    private static final Pattern TWO_DIGIT_VERSION_PATTERN = compile(
        "^([vV])?(\\d+\\.)(\\*|\\d+)(-[0-9A-Za-z-]+)?$");
    private static final Pattern SINGLE_DIGIT_VERSION_PATTERN = compile(
        "^([vV])?(\\*|\\d+)(-[0-9A-Za-z-]+)?$");

    public String getBestSchematicVersioningTag(List<String> dockerImageTags) {
        if (dockerImageTags.isEmpty()) {
            return null;
        }
        for (Pattern versionPattern : asList(THREE_DIGIT_VERSION_PATTERN, TWO_DIGIT_VERSION_PATTERN,
            SINGLE_DIGIT_VERSION_PATTERN)) {
            String foundVersionTag = tryToGetVersionTag(dockerImageTags, versionPattern);
            if (foundVersionTag != null) {
                return foundVersionTag;
            }
        }
        return dockerImageTags.getFirst();
    }

    private static String tryToGetVersionTag(List<String> dockerImageTags, Pattern versionPattern) {
        for (String tag : dockerImageTags) {
            Matcher matcher = versionPattern.matcher(tag);
            if (matcher.matches()) {
                if (tag.lastIndexOf("-") != -1) {
                    return tag.substring(0, tag.indexOf("-"));
                }
                return tag;
            }
        }
        return null;
    }
}
