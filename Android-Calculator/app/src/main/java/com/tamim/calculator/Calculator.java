package com.tamim.calculator;

import com.tamim.calculator.engine.CalculatorEngine;

import java.math.BigDecimal;

/**
 * High-precision calculator wrapper delegating to {@link CalculatorEngine}.
 * Preserves backwards-compatibility with existing double-based APIs while providing
 * exact BigDecimal calculations.
 */
public class Calculator {

    private final CalculatorEngine engine = new CalculatorEngine();

    public double evaluate(double input) {
        BigDecimal result = engine.evaluate(BigDecimal.valueOf(input));
        return result.doubleValue();
    }

    public BigDecimal evaluate(BigDecimal input) {
        return engine.evaluate(input);
    }

    public void storeValue(double value) {
        engine.storeValue(BigDecimal.valueOf(value));
    }

    public void storeValue(BigDecimal value) {
        engine.storeValue(value);
    }

    public void setOperator(String operator) {
        engine.setOperator(operator);
    }

    public String getPendingOperator() {
        return engine.getPendingOperator();
    }

    public double getStoredValue() {
        return engine.getStoredValue().doubleValue();
    }

    public BigDecimal getStoredBigDecimal() {
        return engine.getStoredValue();
    }

    public boolean hasPendingOperator() {
        return engine.hasPendingOperator();
    }

    public void reset() {
        engine.reset();
    }

    public CalculatorEngine getEngine() {
        return engine;
    }
}
