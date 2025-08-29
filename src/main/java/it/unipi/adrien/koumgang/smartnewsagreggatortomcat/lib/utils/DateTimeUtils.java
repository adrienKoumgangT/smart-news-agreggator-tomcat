package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    /**
     * see @Instant
     * <p>
     * Return Instant (always UTC, ISO-8601). ex: 2025-08-28T14:25:10.123Z
     * */
    public static String getCurrentInstant() {
        return Instant.now().toString();
    }

    /**
     * see @ZonedDateTime @DateTimeFormatter
     * <p>
     * Return Custom format yyyy-MM-dd HH:mm:ss 'UTC'. ex: 2025-08-28 14:25:10 UTC
     * */
    public static String getCurrentDateTime() {
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'");
        return utcNow.format(fmt);
    }

    /**
     * see @ZonedDateTime
     * <p>
     * Return ZonedDateTime in UTC. ex: 2025-08-28T14:25:10Z
     * */
    public static String getCurrentZonedDateTime() {
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneOffset.UTC);
        return utcNow.format(DateTimeFormatter.ISO_INSTANT);
    }

}
