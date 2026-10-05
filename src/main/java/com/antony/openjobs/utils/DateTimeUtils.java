package com.antony.openjobs.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public final class DateTimeUtils {
    private DateTimeUtils() {}

    public static OffsetDateTime withServerOffset(LocalDateTime date) {
        return date == null ? null : date.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
