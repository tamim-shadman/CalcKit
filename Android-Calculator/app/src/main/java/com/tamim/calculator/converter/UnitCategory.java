package com.tamim.calculator.converter;

import java.util.ArrayList;
import java.util.List;

public enum UnitCategory {
    LENGTH("Length"),
    WEIGHT("Weight"),
    TEMPERATURE("Temperature"),
    DATA("Data"),
    SPEED("Speed"),
    AREA("Area");

    private final String displayName;

    UnitCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static class Unit {
        private final String name;
        private final String symbol;
        private final double factorToBase; // Factor relative to base unit

        public Unit(String name, String symbol, double factorToBase) {
            this.name = name;
            this.symbol = symbol;
            this.factorToBase = factorToBase;
        }

        public String getName() {
            return name;
        }

        public String getSymbol() {
            return symbol;
        }

        public double getFactorToBase() {
            return factorToBase;
        }

        @Override
        public String toString() {
            return name + " (" + symbol + ")";
        }
    }

    public List<Unit> getUnits() {
        List<Unit> list = new ArrayList<>();
        switch (this) {
            case LENGTH:
                // Base: Meter (m)
                list.add(new Unit("Meter", "m", 1.0));
                list.add(new Unit("Kilometer", "km", 1000.0));
                list.add(new Unit("Centimeter", "cm", 0.01));
                list.add(new Unit("Millimeter", "mm", 0.001));
                list.add(new Unit("Mile", "mi", 1609.344));
                list.add(new Unit("Yard", "yd", 0.9144));
                list.add(new Unit("Foot", "ft", 0.3048));
                list.add(new Unit("Inch", "in", 0.0254));
                break;
            case WEIGHT:
                // Base: Kilogram (kg)
                list.add(new Unit("Kilogram", "kg", 1.0));
                list.add(new Unit("Gram", "g", 0.001));
                list.add(new Unit("Milligram", "mg", 0.000001));
                list.add(new Unit("Metric Ton", "t", 1000.0));
                list.add(new Unit("Pound", "lb", 0.45359237));
                list.add(new Unit("Ounce", "oz", 0.028349523125));
                break;
            case TEMPERATURE:
                list.add(new Unit("Celsius", "°C", 1.0));
                list.add(new Unit("Fahrenheit", "°F", 1.0));
                list.add(new Unit("Kelvin", "K", 1.0));
                break;
            case DATA:
                // Base: Byte (B)
                list.add(new Unit("Byte", "B", 1.0));
                list.add(new Unit("Kilobyte", "KB", 1024.0));
                list.add(new Unit("Megabyte", "MB", 1048576.0));
                list.add(new Unit("Gigabyte", "GB", 1073741824.0));
                list.add(new Unit("Terabyte", "TB", 1099511627776.0));
                break;
            case SPEED:
                // Base: m/s
                list.add(new Unit("Meters/sec", "m/s", 1.0));
                list.add(new Unit("Kilometers/hour", "km/h", 1.0 / 3.6));
                list.add(new Unit("Miles/hour", "mph", 0.44704));
                list.add(new Unit("Knot", "kn", 0.514444));
                break;
            case AREA:
                // Base: Square Meter (m²)
                list.add(new Unit("Square Meter", "m²", 1.0));
                list.add(new Unit("Square Kilometer", "km²", 1000000.0));
                list.add(new Unit("Square Foot", "ft²", 0.092903));
                list.add(new Unit("Acre", "ac", 4046.8564224));
                list.add(new Unit("Hectare", "ha", 10000.0));
                break;
        }
        return list;
    }
}
