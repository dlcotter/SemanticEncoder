package common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {
    private static final String ALPHA_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMERIC_STRING = "0123456789";
    public static final String XSD_DATETIME_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static String now() {
        return now(null);
    }

    public static String now(String pattern) {
        return nSecondsAgo(0, pattern);
    }

    public static String nSecondsAgo(int n, String pattern) {
        return DateTimeFormatter
                .ofPattern(pattern == null ? XSD_DATETIME_FMT : pattern, Locale.ENGLISH)
                .format(LocalDateTime.now().minusNanos(n * 10^9));
    }

    public static String randomAlphaIdentifier(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_STRING.length());
            builder.append(ALPHA_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static String randomNumericIdentifier(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * NUMERIC_STRING.length());
            builder.append(NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}
