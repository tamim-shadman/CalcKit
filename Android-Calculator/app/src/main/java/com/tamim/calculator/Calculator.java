package com.tamim.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Calculator {

    private static final MathContext MATH_CONTEXT = new MathContext(12, RoundingMode.HALF_UP);
    private BigDecimal storedValue = BigDecimal.ZERO;
    private String pendingOperator = null;

    public double evaluate(double input) {
        BigDecimal current = BigDecimal.valueOf(input);
        if (pendingOperator == null) {
            storedValue = current;
            return input;
        }

        BigDecimal result;
        switch (pendingOperator) {
            case "+":
                result = storedValue.add(current, MATH_CONTEXT);
                break;
            case "−":
                result = storedValue.subtract(current, MATH_CONTEXT);
                break;
            case "×":
                result = storedValue.multiply(current, MATH_CONTEXT);
                break;
            case "÷":
                if (current.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                result = storedValue.divide(current, 10, RoundingMode.HALF_UP).stripTrailingZeros();
                break;
            default:
                result = current;
        }

        storedValue = result;
        return result.doubleValue();
    }

    public void storeValue(double value) {
        storedValue = BigDecimal.valueOf(value);
        pendingOperator = null;
    }

    public void setOperator(String operator) {
        pendingOperator = operator;
    }

    public String getPendingOperator() {
        return pendingOperator;
    }

    public double getStoredValue() {
        return storedValue.doubleValue();
    }

    public boolean hasPendingOperator() {
        return pendingOperator != null;
    }

    public void reset() {
        storedValue = BigDecimal.ZERO;
        pendingOperator = null;
    }
}
