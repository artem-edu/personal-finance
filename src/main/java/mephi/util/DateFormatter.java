package mephi.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    public static String format(long ms) {
        return format(ms, "dd.MM.yyyy HH:mm");
    }
    public static String format(long ms, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()));
    }
}
