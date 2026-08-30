package com.tamim.calculator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CalculatorTest {

    private Calculator calc;

    @Before
    public void setUp() {
        calc = new Calculator();
    }

    @Test
    public void addition() {
        calc.evaluate(3);
        calc.setOperator("+");
        assertEquals(8.0, calc.evaluate(5), 1e-10);
    }

    @Test
    public void subtraction() {
        calc.evaluate(10);
        calc.setOperator("−");
        assertEquals(4.0, calc.evaluate(6), 1e-10);
    }

    @Test
    public void multiplication() {
        calc.evaluate(7);
        calc.setOperator("×");
        assertEquals(42.0, calc.evaluate(6), 1e-10);
    }

    @Test
    public void division() {
        calc.evaluate(20);
        calc.setOperator("÷");
        assertEquals(4.0, calc.evaluate(5), 1e-10);
    }

    @Test(expected = ArithmeticException.class)
    public void divisionByZero_throws() {
        calc.evaluate(9);
        calc.setOperator("÷");
        calc.evaluate(0);
    }

    @Test
    public void chainedOperations_leftToRight() {
        calc.evaluate(3);
        calc.setOperator("+");
        calc.evaluate(5);
        calc.setOperator("×");
        assertEquals(16.0, calc.evaluate(2), 1e-10);
    }

    @Test
    public void noOperator_evaluateReturnsInput() {
        assertEquals(7.0, calc.evaluate(7), 1e-10);
    }

    @Test
    public void reset_clearsAllState() {
        calc.evaluate(100);
        calc.setOperator("+");
        calc.reset();

        assertNull(calc.getPendingOperator());
        assertEquals(0.0, calc.getStoredValue(), 0.0);
        assertFalse(calc.hasPendingOperator());
    }

    @Test
    public void storeValue_overwritesAndClearsOperator() {
        calc.evaluate(5);
        calc.setOperator("+");
        calc.storeValue(99);

        assertFalse(calc.hasPendingOperator());
        assertEquals(99.0, calc.getStoredValue(), 0.0);
    }

    @Test
    public void hasPendingOperator_falseInitially() {
        assertFalse(calc.hasPendingOperator());
    }

    @Test
    public void hasPendingOperator_trueAfterSet() {
        calc.setOperator("+");
        assertTrue(calc.hasPendingOperator());
    }

    @Test
    public void formatNumber_integer() {
        assertEquals("42", MainActivity.formatNumber(42.0));
    }

    @Test
    public void formatNumber_negativeInteger() {
        assertEquals("-7", MainActivity.formatNumber(-7.0));
    }

    @Test
    public void formatNumber_suppressesFloatingPointNoise() {
        assertEquals("0.3", MainActivity.formatNumber(0.1 + 0.2));
    }

    @Test
    public void formatNumber_zero() {
        assertEquals("0", MainActivity.formatNumber(0.0));
    }

    @Test
    public void formatNumber_negativeZero() {
        assertEquals("0", MainActivity.formatNumber(-0.0));
    }

    @Test
    public void formatNumber_positiveInfinity() {
        assertEquals("Error", MainActivity.formatNumber(Double.POSITIVE_INFINITY));
    }

    @Test
    public void formatNumber_negativeInfinity() {
        assertEquals("Error", MainActivity.formatNumber(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void formatNumber_nan() {
        assertEquals("Error", MainActivity.formatNumber(Double.NaN));
    }

    @Test
    public void formatNumber_trailsTrailingZeros() {
        assertEquals("1.5", MainActivity.formatNumber(1.5));
    }
}
