package com.robertnator.docker.update.sensor.utils;

import java.time.Instant;
import java.util.Date;

public class DateUtils {

    public static Date toDate(String dateString) {
        return new Date(Instant.parse(dateString).toEpochMilli());
    }
}
