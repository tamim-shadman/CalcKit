package com.tamim.calculator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utility for formatting calculator numbers for display.
 * Supports locale-aware digit grouping, scientific notation for extreme values,
 * and preserves in-progress decimal entry without stripping trailing zeros prematurely.
 */
public final class NumberFormatter {

    private static final double MAX_STANDARD_DISPLAY = 1e12;
    private static final double MIN_STANDARD_DISPLAY = 1e-7;
    private static final int MAX_FRACTION_DIGITS = 10;

    private NumberFormatter() {
        // Utility class
    }

    /**
     * Formats a double value (used by legacy APIs and tests).
     */
    public static String formatNumber(double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            return "Error";
        }
        if (value == 0.0) {
            return "0";
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        return formatBigDecimal(bd);
    }

    /**
     * Formats a BigDecimal value for final display after evaluation.
     */
    public static String formatBigDecimal(BigDecimal value) {
        if (value == null) {
            return "0";
        }

        // Check for 0
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }

        BigDecimal absVal = value.abs();
        double dVal = absVal.doubleValue();

        // Scientific notation for very large or very small numbers
        if (dVal >= MAX_STANDARD_DISPLAY || (dVal > 0 && dVal < MIN_STANDARD_DISPLAY)) {
            DecimalFormat sciFormat = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.US));
            return sciFormat.format(value);
        }

        // Standard formatting with thousands grouping
        BigDecimal stripped = value.stripTrailingZeros();
        if (stripped.scale() <= 0) {
            // Integer value
            DecimalFormat intFormat = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));
            return intFormat.format(stripped);
        } else {
            // Decimal value with up to 10 decimal digits
            int scale = Math.min(stripped.scale(), MAX_FRACTION_DIGITS);
            BigDecimal scaled = stripped.setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros();

            if (scaled.scale() <= 0) {
                DecimalFormat intFormat = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));
                return intFormat.format(scaled);
            }

            StringBuilder pattern = new StringBuilder("#,##0.");
            for (int i = 0; i < scaled.scale(); i++) {
                pattern.append("0");
            }
            DecimalFormat decFormat = new DecimalFormat(pattern.toString(), DecimalFormatSymbols.getInstance(Locale.US));
            return decFormat.format(scaled);
        }
    }

    /**
     * Formats user input as they type, preserving decimal points and trailing zeros.
     * E.g. "1234567" -> "1,234,567", "1234567." -> "1,234,567.", "1234.00" -> "1,234.00"
     */
    public static String formatTypingInput(String rawInput) {
        if (rawInput == null || rawInput.isEmpty()) {
            return "0";
        }

        if ("Error".equalsIgnoreCase(rawInput) || "Cannot divide by zero".equalsIgnoreCase(rawInput)) {
            return rawInput;
        }

        // Preserve negative sign
        boolean isNegative = rawInput.startsWith("-");
        String working = isNegative ? rawInput.substring(1) : rawInput;

        int dotIndex = working.indexOf('.');
        if (dotIndex < 0) {
            // Integer only
            try {
                BigDecimal bd = new BigDecimal(working);
                DecimalFormat format = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));
                String formatted = format.format(bd);
                return isNegative ? "-" + formatted : formatted;
            } catch (Exception e) {
                return rawInput;
            }
        }

        String intPart = working.substring(0, dotIndex);
        String fracPart = working.substring(dotIndex + 1);

        String formattedInt;
        try {
            if (intPart.isEmpty()) {
                formattedInt = "0";
            } else {
                BigDecimal bd = new BigDecimal(intPart);
                DecimalFormat format = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));
                formattedInt = format.format(bd);
            }
        } catch (Exception e) {
            formattedInt = intPart;
        }

        String result = formattedInt + "." + fracPart;
        return isNegative ? "-" + result : result;
    }
}
