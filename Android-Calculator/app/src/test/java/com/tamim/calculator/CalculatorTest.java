package com.tamim.calculator;

import com.tamim.calculator.converter.UnitCategory;
import com.tamim.calculator.converter.UnitConverter;
import com.tamim.calculator.engine.CalculatorEngine;
import com.tamim.calculator.util.NumberFormatter;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class CalculatorTest {

    private Calculator calc;
    private CalculatorEngine engine;

    @Before
    public void setUp() {
        calc = new Calculator();
        engine = new CalculatorEngine();
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

    // --- High-Precision CalculatorEngine Tests ---

    @Test
    public void engineExactPrecision_noFloatingPointDrift() {
        engine.evaluate(new BigDecimal("0.1"));
        engine.setOperator("+");
        BigDecimal result = engine.evaluate(new BigDecimal("0.2"));
        assertEquals(new BigDecimal("0.3"), result);
    }

    @Test
    public void enginePercentage_standalone() {
        BigDecimal pct = engine.calculatePercentage(new BigDecimal("50"));
        assertEquals(new BigDecimal("0.5"), pct);
    }

    @Test
    public void enginePercentage_withAdditionContext() {
        engine.evaluate(new BigDecimal("100"));
        engine.setOperator("+");
        BigDecimal pct = engine.calculatePercentage(new BigDecimal("10"));
        assertEquals(new BigDecimal("10"), pct);

        BigDecimal total = engine.evaluate(pct);
        assertEquals(new BigDecimal("110"), total);
    }

    @Test
    public void enginePercentage_withSubtractionContext() {
        engine.evaluate(new BigDecimal("50"));
        engine.setOperator("−");
        BigDecimal pct = engine.calculatePercentage(new BigDecimal("20"));
        assertEquals(new BigDecimal("10"), pct);

        BigDecimal total = engine.evaluate(pct);
        assertEquals(new BigDecimal("40"), total);
    }

    @Test
    public void engineSquareRoot_exact() {
        BigDecimal root = engine.squareRoot(new BigDecimal("25"));
        assertEquals(new BigDecimal("5"), root);
    }

    @Test(expected = ArithmeticException.class)
    public void engineSquareRoot_negativeThrows() {
        engine.squareRoot(new BigDecimal("-4"));
    }

    @Test
    public void engineSquare() {
        BigDecimal squared = engine.square(new BigDecimal("9"));
        assertEquals(new BigDecimal("81"), squared);
    }

    @Test
    public void engineReciprocal() {
        BigDecimal recip = engine.reciprocal(new BigDecimal("4"));
        assertEquals(new BigDecimal("0.25"), recip);
    }

    @Test(expected = ArithmeticException.class)
    public void engineReciprocal_zeroThrows() {
        engine.reciprocal(BigDecimal.ZERO);
    }

    @Test
    public void engineNegate() {
        BigDecimal neg = engine.negate(new BigDecimal("15"));
        assertEquals(new BigDecimal("-15"), neg);
        assertEquals(new BigDecimal("15"), engine.negate(neg));
    }

    @Test
    public void engineRepeatedEquals_constantCalculation() {
        engine.evaluate(new BigDecimal("5"));
        engine.setOperator("+");
        BigDecimal r1 = engine.evaluate(new BigDecimal("2"));
        assertEquals(new BigDecimal("7"), r1);

        BigDecimal r2 = engine.evaluateRepeatedEquals(r1);
        assertEquals(new BigDecimal("9"), r2);

        BigDecimal r3 = engine.evaluateRepeatedEquals(r2);
        assertEquals(new BigDecimal("11"), r3);
    }

    // --- Trigonometry & Advanced Scientific Tests ---

    @Test
    public void engineSin_degrees() {
        engine.setRadMode(false);
        assertEquals(new BigDecimal("0"), engine.sin(new BigDecimal("0")));
        assertEquals(new BigDecimal("1"), engine.sin(new BigDecimal("90")));
        assertEquals(new BigDecimal("0"), engine.sin(new BigDecimal("180")));
    }

    @Test
    public void engineCos_degrees() {
        engine.setRadMode(false);
        assertEquals(new BigDecimal("1"), engine.cos(new BigDecimal("0")));
        assertEquals(new BigDecimal("0"), engine.cos(new BigDecimal("90")));
        assertEquals(new BigDecimal("-1"), engine.cos(new BigDecimal("180")));
    }

    @Test
    public void engineTan_degrees() {
        engine.setRadMode(false);
        assertEquals(0.0, engine.tan(new BigDecimal("0")).doubleValue(), 1e-10);
        assertEquals(1.0, engine.tan(new BigDecimal("45")).doubleValue(), 1e-10);
    }

    @Test(expected = ArithmeticException.class)
    public void engineTan90_degreesThrows() {
        engine.setRadMode(false);
        engine.tan(new BigDecimal("90"));
    }

    @Test
    public void engineLogarithms() {
        assertEquals(2.0, engine.log10(new BigDecimal("100")).doubleValue(), 1e-10);
        assertEquals(1.0, engine.naturalLog(new BigDecimal(String.valueOf(Math.E))).doubleValue(), 1e-10);
    }

    @Test
    public void engineFactorial() {
        assertEquals(new BigDecimal("1"), engine.factorial(new BigDecimal("0")));
        assertEquals(new BigDecimal("120"), engine.factorial(new BigDecimal("5")));
        assertEquals(new BigDecimal("720"), engine.factorial(new BigDecimal("6")));
    }

    @Test
    public void enginePower() {
        engine.evaluate(new BigDecimal("2"));
        engine.setOperator("^");
        BigDecimal result = engine.evaluate(new BigDecimal("3"));
        assertEquals(8.0, result.doubleValue(), 1e-10);
    }

    // --- Memory Register Tests ---

    @Test
    public void engineMemoryRegisters() {
        assertFalse(engine.hasMemory());

        engine.memoryStore(new BigDecimal("50"));
        assertTrue(engine.hasMemory());
        assertEquals(new BigDecimal("50"), engine.memoryRecall());

        engine.memoryAdd(new BigDecimal("20"));
        assertEquals(new BigDecimal("70"), engine.memoryRecall());

        engine.memorySubtract(new BigDecimal("30"));
        assertEquals(new BigDecimal("40"), engine.memoryRecall());

        engine.memoryClear();
        assertFalse(engine.hasMemory());
        assertEquals(BigDecimal.ZERO, engine.memoryRecall());
    }

    // --- Unit Converter Tests ---

    @Test
    public void unitConverter_length() {
        // 1 km = 1000 m (Meter idx 0, Kilometer idx 1)
        double meters = UnitConverter.convert(UnitCategory.LENGTH, 1, 0, 1.0);
        assertEquals(1000.0, meters, 1e-6);
    }

    @Test
    public void unitConverter_weight() {
        // 1 kg = 1000 g (Kilogram idx 0, Gram idx 1)
        double grams = UnitConverter.convert(UnitCategory.WEIGHT, 0, 1, 1.0);
        assertEquals(1000.0, grams, 1e-6);
    }

    @Test
    public void unitConverter_temperature() {
        // 100 Celsius = 212 Fahrenheit (Celsius idx 0, Fahrenheit idx 1)
        double f = UnitConverter.convert(UnitCategory.TEMPERATURE, 0, 1, 100.0);
        assertEquals(212.0, f, 1e-6);

        // 32 Fahrenheit = 0 Celsius
        double c = UnitConverter.convert(UnitCategory.TEMPERATURE, 1, 0, 32.0);
        assertEquals(0.0, c, 1e-6);
    }

    @Test
    public void unitConverter_data() {
        // 1 GB = 1024 MB (MB idx 2, GB idx 3)
        double mb = UnitConverter.convert(UnitCategory.DATA, 3, 2, 1.0);
        assertEquals(1024.0, mb, 1e-6);
    }

    @Test
    public void unitConverter_speed() {
        // 36 km/h = 10 m/s (m/s idx 0, km/h idx 1)
        double ms = UnitConverter.convert(UnitCategory.SPEED, 1, 0, 36.0);
        assertEquals(10.0, ms, 1e-4);
    }

    // --- Number Formatting Tests ---

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

    @Test
    public void formatNumber_thousandsSeparator() {
        assertEquals("1,000,000", NumberFormatter.formatBigDecimal(new BigDecimal("1000000")));
    }

    @Test
    public void formatNumber_scientificNotationForExtremeNumbers() {
        String formatted = NumberFormatter.formatBigDecimal(new BigDecimal("10000000000000"));
        assertTrue(formatted.contains("E") || formatted.contains("e"));
    }

    @Test
    public void formatTypingInput_preservesTypingPrecision() {
        assertEquals("1,234.", NumberFormatter.formatTypingInput("1234."));
        assertEquals("1,234.00", NumberFormatter.formatTypingInput("1234.00"));
        assertEquals("1,234.506", NumberFormatter.formatTypingInput("1234.506"));
        assertEquals("-1,234.5", NumberFormatter.formatTypingInput("-1234.5"));
    }
}
