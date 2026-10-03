package com.tamim.calculator.engine;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * High-precision calculation engine backed by BigDecimal.
 * Handles arithmetic (+, −, ×, ÷, ^), trigonometry (sin, cos, tan),
 * logarithms (log, ln), factorial (x!), percentages, roots, powers,
 * memory registers (MC, MR, M+, M-, MS), and angle modes (DEG/RAD).
 */
public class CalculatorEngine {

    public static final MathContext MATH_CONTEXT = new MathContext(16, RoundingMode.HALF_UP);
    private static final int DIVISION_SCALE = 16;
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private BigDecimal storedValue = BigDecimal.ZERO;
    private String pendingOperator = null;

    // For repeated equals (constant calculation)
    private String lastOperator = null;
    private BigDecimal lastOperand = null;

    // Memory register
    private BigDecimal memoryValue = BigDecimal.ZERO;

    // Angle mode: false = DEG, true = RAD
    private boolean isRadMode = false;

    public CalculatorEngine() {
        reset();
    }

    public BigDecimal evaluate(BigDecimal input) {
        if (input == null) {
            input = BigDecimal.ZERO;
        }

        if (pendingOperator == null) {
            storedValue = input;
            return storedValue;
        }

        BigDecimal result = performOperation(storedValue, input, pendingOperator);

        // Store for repeated equals
        lastOperator = pendingOperator;
        lastOperand = input;

        storedValue = result;
        pendingOperator = null;
        return storedValue;
    }

    public BigDecimal evaluateRepeatedEquals(BigDecimal currentInput) {
        if (lastOperator == null || lastOperand == null) {
            return currentInput != null ? currentInput : storedValue;
        }

        BigDecimal base = currentInput != null ? currentInput : storedValue;
        BigDecimal result = performOperation(base, lastOperand, lastOperator);
        storedValue = result;
        return storedValue;
    }

    public BigDecimal calculatePercentage(BigDecimal currentInput) {
        if (currentInput == null) {
            currentInput = BigDecimal.ZERO;
        }

        if (pendingOperator == null) {
            return currentInput.divide(HUNDRED, DIVISION_SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
        }

        if ("+".equals(pendingOperator) || "−".equals(pendingOperator) || "-".equals(pendingOperator)) {
            return storedValue.multiply(currentInput, MATH_CONTEXT)
                    .divide(HUNDRED, DIVISION_SCALE, RoundingMode.HALF_UP)
                    .stripTrailingZeros();
        } else {
            return currentInput.divide(HUNDRED, DIVISION_SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
        }
    }

    public BigDecimal squareRoot(BigDecimal input) {
        if (input == null || input.compareTo(BigDecimal.ZERO) < 0) {
            throw new ArithmeticException("Invalid input");
        }
        if (input.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal x0 = new BigDecimal(Math.sqrt(input.doubleValue()));
        if (x0.compareTo(BigDecimal.ZERO) == 0) {
            x0 = BigDecimal.ONE;
        }

        BigDecimal two = new BigDecimal("2");
        for (int i = 0; i < 20; i++) {
            BigDecimal x1 = x0.add(input.divide(x0, DIVISION_SCALE, RoundingMode.HALF_UP), MATH_CONTEXT)
                    .divide(two, DIVISION_SCALE, RoundingMode.HALF_UP);
            if (x0.subtract(x1).abs().compareTo(new BigDecimal("1e-15")) <= 0) {
                x0 = x1;
                break;
            }
            x0 = x1;
        }
        return x0.stripTrailingZeros();
    }

    public BigDecimal square(BigDecimal input) {
        if (input == null) return BigDecimal.ZERO;
        return input.multiply(input, MATH_CONTEXT).stripTrailingZeros();
    }

    public BigDecimal reciprocal(BigDecimal input) {
        if (input == null || input.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return BigDecimal.ONE.divide(input, DIVISION_SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public BigDecimal negate(BigDecimal input) {
        if (input == null) return BigDecimal.ZERO;
        return input.negate();
    }

    // --- Trigonometry & Advanced Scientific ---

    public BigDecimal sin(BigDecimal input) {
        if (input == null) return BigDecimal.ZERO;
        double angle = input.doubleValue();
        if (!isRadMode) {
            // Normalize degrees
            double norm = angle % 360.0;
            if (norm < 0) norm += 360.0;
            if (norm == 0.0 || norm == 180.0 || norm == 360.0) return BigDecimal.ZERO;
            if (norm == 90.0) return BigDecimal.ONE;
            if (norm == 270.0) return new BigDecimal("-1");
            angle = Math.toRadians(angle);
        }
        double val = Math.sin(angle);
        return new BigDecimal(val, MATH_CONTEXT).stripTrailingZeros();
    }

    public BigDecimal cos(BigDecimal input) {
        if (input == null) return BigDecimal.ONE;
        double angle = input.doubleValue();
        if (!isRadMode) {
            double norm = angle % 360.0;
            if (norm < 0) norm += 360.0;
            if (norm == 90.0 || norm == 270.0) return BigDecimal.ZERO;
            if (norm == 0.0 || norm == 360.0) return BigDecimal.ONE;
            if (norm == 180.0) return new BigDecimal("-1");
            angle = Math.toRadians(angle);
        }
        double val = Math.cos(angle);
        return new BigDecimal(val, MATH_CONTEXT).stripTrailingZeros();
    }

    public BigDecimal tan(BigDecimal input) {
        if (input == null) return BigDecimal.ZERO;
        double angle = input.doubleValue();
        if (!isRadMode) {
            double norm = angle % 180.0;
            if (norm < 0) norm += 180.0;
            if (norm == 90.0) {
                throw new ArithmeticException("Invalid input");
            }
            if (norm == 0.0 || norm == 180.0) return BigDecimal.ZERO;
            angle = Math.toRadians(angle);
        } else {
            if (Math.abs(Math.cos(angle)) < 1e-15) {
                throw new ArithmeticException("Invalid input");
            }
        }
        double val = Math.tan(angle);
        return new BigDecimal(val, MATH_CONTEXT).stripTrailingZeros();
    }

    public BigDecimal log10(BigDecimal input) {
        if (input == null || input.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArithmeticException("Invalid input");
        }
        double val = Math.log10(input.doubleValue());
        return new BigDecimal(val, MATH_CONTEXT).stripTrailingZeros();
    }

    public BigDecimal naturalLog(BigDecimal input) {
        if (input == null || input.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArithmeticException("Invalid input");
        }
        double val = Math.log(input.doubleValue());
        return new BigDecimal(val, MATH_CONTEXT).stripTrailingZeros();
    }

    public BigDecimal factorial(BigDecimal input) {
        if (input == null || input.compareTo(BigDecimal.ZERO) < 0) {
            throw new ArithmeticException("Invalid input");
        }

        // Check if integer
        if (input.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
            throw new ArithmeticException("Invalid input");
        }

        int n = input.intValue();
        if (n > 100) {
            throw new ArithmeticException("Overflow");
        }

        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return new BigDecimal(result);
    }

    // --- Memory Registers ---

    public void memoryClear() {
        memoryValue = BigDecimal.ZERO;
    }

    public BigDecimal memoryRecall() {
        return memoryValue;
    }

    public void memoryAdd(BigDecimal val) {
        if (val != null) {
            memoryValue = memoryValue.add(val, MATH_CONTEXT).stripTrailingZeros();
        }
    }

    public void memorySubtract(BigDecimal val) {
        if (val != null) {
            memoryValue = memoryValue.subtract(val, MATH_CONTEXT).stripTrailingZeros();
        }
    }

    public void memoryStore(BigDecimal val) {
        memoryValue = val != null ? val : BigDecimal.ZERO;
    }

    public boolean hasMemory() {
        return memoryValue.compareTo(BigDecimal.ZERO) != 0;
    }

    // --- Angle Mode ---

    public boolean isRadMode() {
        return isRadMode;
    }

    public void setRadMode(boolean radMode) {
        isRadMode = radMode;
    }

    public void toggleAngleMode() {
        isRadMode = !isRadMode;
    }

    private BigDecimal performOperation(BigDecimal a, BigDecimal b, String op) {
        switch (op) {
            case "+":
                return a.add(b, MATH_CONTEXT).stripTrailingZeros();
            case "−":
            case "-":
                return a.subtract(b, MATH_CONTEXT).stripTrailingZeros();
            case "×":
            case "*":
                return a.multiply(b, MATH_CONTEXT).stripTrailingZeros();
            case "÷":
            case "/":
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                return a.divide(b, DIVISION_SCALE, RoundingMode.HALF_UP).stripTrailingZeros();
            case "^":
                double p = Math.pow(a.doubleValue(), b.doubleValue());
                if (Double.isNaN(p) || Double.isInfinite(p)) {
                    throw new ArithmeticException("Invalid power");
                }
                return new BigDecimal(p, MATH_CONTEXT).stripTrailingZeros();
            default:
                return b;
        }
    }

    public void storeValue(BigDecimal value) {
        this.storedValue = value != null ? value : BigDecimal.ZERO;
        this.pendingOperator = null;
    }

    public void setOperator(String operator) {
        this.pendingOperator = operator;
    }

    public String getPendingOperator() {
        return pendingOperator;
    }

    public BigDecimal getStoredValue() {
        return storedValue;
    }

    public boolean hasPendingOperator() {
        return pendingOperator != null;
    }

    public boolean hasRepeatedEqualsReady() {
        return lastOperator != null && lastOperand != null;
    }

    public String getLastOperator() {
        return lastOperator;
    }

    public BigDecimal getLastOperand() {
        return lastOperand;
    }

    public void reset() {
        storedValue = BigDecimal.ZERO;
        pendingOperator = null;
        lastOperator = null;
        lastOperand = null;
    }
}
