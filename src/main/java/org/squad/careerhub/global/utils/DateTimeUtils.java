package org.squad.careerhub.global.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class DateTimeUtils {
    private DateTimeUtils() {
        throw new AssertionError("Utility class");
    }

    public static LocalDateTime now() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }
}