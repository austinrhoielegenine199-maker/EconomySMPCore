package com.nyretha.economy.util;

import java.text.DecimalFormat;

public class NumberFormatter {
    private static final char[] SUFFIXES = {'K', 'M', 'B', 'T'};
    private static final DecimalFormat FORMAT = new DecimalFormat("#,##0.#");

    public static String format(double value, String symbol, boolean formattingEnabled) {
        if (!formattingEnabled) {
            return symbol + String.format("%,.2f", value);
        }

        if (value < 1000) {
            return symbol + FORMAT.format(value);
        }

        int valueIndex = (int) (Math.floor(Math.log10(value)) / 3);
        double scaledValue = value / Math.pow(10, valueIndex * 3);
        
        return symbol + FORMAT.format(scaledValue) + SUFFIXES[valueIndex - 1];
    }
}
