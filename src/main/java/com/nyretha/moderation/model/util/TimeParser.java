package com.nyretha.moderation.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {
    
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([sSmMhHdDwW])");

    public static long parseToMillis(String shorthand) {
        if (shorthand == null || shorthand.equalsIgnoreCase("permanent") || shorthand.equalsIgnoreCase("perm")) {
            return -1; 
        }

        Matcher matcher = TIME_PATTERN.matcher(shorthand);
        long totalMillis = 0;
        boolean found = false;

        while (matcher.find()) {
            found = true;
            long value = Long.parseLong(matcher.group(1));
            char unit = matcher.group(2).toLowerCase().charAt(0);

            switch (unit) {
                case 's': totalMillis += value * 1000L; break;
                case 'm': totalMillis += value * 60L * 1000L; break;
                case 'h': totalMillis += value * 60L * 60L * 1000L; break;
                case 'd': totalMillis += value * 24L * 60L * 60L * 1000L; break;
                case 'w': totalMillis += value * 7L * 24L * 60L * 60L * 1000L; break;
            }
        }

        return found ? totalMillis : 0;
    }
}
