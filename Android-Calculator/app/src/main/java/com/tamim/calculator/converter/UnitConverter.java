package com.tamim.calculator.converter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

/**
 * High-accuracy conversion engine supporting Length, Weight, Temperature,
 * Data, Speed, and Area.
 */
public final class UnitConverter {

    private UnitConverter() {
        // Utility
    }

    public static double convert(UnitCategory category, int fromIndex, int toIndex, double value) {
        if (category == UnitCategory.TEMPERATURE) {
            return convertTemperature(fromIndex, toIndex, value);
        }

        List<UnitCategory.Unit> units = category.getUnits();
        if (fromIndex < 0 || fromIndex >= units.size() || toIndex < 0 || toIndex >= units.size()) {
            return value;
        }

        UnitCategory.Unit from = units.get(fromIndex);
        UnitCategory.Unit to = units.get(toIndex);

        // Convert from source to base, then base to target
        double baseValue = value * from.getFactorToBase();
        return baseValue / to.getFactorToBase();
    }

    private static double convertTemperature(int fromIndex, int toIndex, double value) {
        if (fromIndex == toIndex) return value;

        // 0: Celsius, 1: Fahrenheit, 2: Kelvin
        // Step 1: convert to Celsius
        double celsius;
        switch (fromIndex) {
            case 0:
                celsius = value;
                break;
            case 1:
                celsius = (value - 32.0) * 5.0 / 9.0;
                break;
            case 2:
                celsius = value - 273.15;
                break;
            default:
                celsius = value;
        }

        // Step 2: convert from Celsius to target
        switch (toIndex) {
            case 0:
                return celsius;
            case 1:
                return (celsius * 9.0 / 5.0) + 32.0;
            case 2:
                return celsius + 273.15;
            default:
                return celsius;
        }
    }

    public static String formatResult(double result) {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            return "Error";
        }
        if (result == 0.0) {
            return "0";
        }

        double abs = Math.abs(result);
        DecimalFormat format;
        if (abs >= 1e9 || (abs > 0 && abs < 1e-4)) {
            format = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.US));
        } else if (result == Math.floor(result) && abs < 1e12) {
            format = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));
        } else {
            format = new DecimalFormat("#,##0.######", DecimalFormatSymbols.getInstance(Locale.US));
        }
        return format.format(result);
    }
}
