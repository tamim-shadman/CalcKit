package com.tamim.calculator.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tamim.calculator.data.HistoryRepository;
import com.tamim.calculator.engine.CalculatorEngine;
import com.tamim.calculator.model.HistoryItem;
import com.tamim.calculator.util.NumberFormatter;

import java.math.BigDecimal;
import java.util.List;

/**
 * ViewModel managing calculator state, surviving configuration changes,
 * and handling advanced scientific, memory registers, and conversion events.
 */
public class CalculatorViewModel extends AndroidViewModel {

    private final CalculatorEngine engine = new CalculatorEngine();
    private final HistoryRepository historyRepository;

    private final MutableLiveData<String> display = new MutableLiveData<>("0");
    private final MutableLiveData<String> expression = new MutableLiveData<>("");
    private final MutableLiveData<String> activeOperator = new MutableLiveData<>(null);
    private final MutableLiveData<List<HistoryItem>> historyList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isClearAllState = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> hasMemory = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRadMode = new MutableLiveData<>(false);

    private final StringBuilder rawInput = new StringBuilder("0");
    private boolean isNewEntry = true;
    private boolean lastWasEquals = false;
    private boolean isError = false;

    public CalculatorViewModel(@NonNull Application application) {
        super(application);
        this.historyRepository = new HistoryRepository(application);
        loadHistory();
    }

    public LiveData<String> getDisplay() {
        return display;
    }

    public LiveData<String> getExpression() {
        return expression;
    }

    public LiveData<String> getActiveOperator() {
        return activeOperator;
    }

    public LiveData<List<HistoryItem>> getHistoryList() {
        return historyList;
    }

    public LiveData<Boolean> getIsClearAllState() {
        return isClearAllState;
    }

    public LiveData<Boolean> getHasMemory() {
        return hasMemory;
    }

    public LiveData<Boolean> getIsRadMode() {
        return isRadMode;
    }

    public void loadHistory() {
        historyList.setValue(historyRepository.loadHistory());
    }

    public void appendDigit(String digit) {
        if (isError) {
            handleClear();
        }

        if (lastWasEquals) {
            expression.setValue("");
            lastWasEquals = false;
        }

        if (isNewEntry) {
            rawInput.setLength(0);
            rawInput.append(digit);
            isNewEntry = false;
        } else {
            if ("0".equals(rawInput.toString())) {
                rawInput.setLength(0);
            }
            rawInput.append(digit);
        }

        activeOperator.setValue(null);
        isClearAllState.setValue(false);
        display.setValue(NumberFormatter.formatTypingInput(rawInput.toString()));
    }

    public void appendDecimal() {
        if (isError) {
            handleClear();
        }

        if (lastWasEquals) {
            expression.setValue("");
            lastWasEquals = false;
        }

        if (isNewEntry) {
            rawInput.setLength(0);
            rawInput.append("0.");
            isNewEntry = false;
        } else if (!rawInput.toString().contains(".")) {
            rawInput.append(".");
        }

        activeOperator.setValue(null);
        isClearAllState.setValue(false);
        display.setValue(NumberFormatter.formatTypingInput(rawInput.toString()));
    }

    public void handleOperator(String op) {
        if (isError) {
            handleClear();
        }

        if (isNewEntry && engine.hasPendingOperator()) {
            engine.setOperator(op);
            activeOperator.setValue(op);
            swapLastOperatorInExpression(op);
            return;
        }

        BigDecimal input = parseRawInput();

        if (lastWasEquals) {
            engine.storeValue(input);
            expression.setValue(NumberFormatter.formatBigDecimal(input) + " " + op);
        } else if (engine.hasPendingOperator()) {
            try {
                BigDecimal result = engine.evaluate(input);
                String currentExpr = expression.getValue();
                expression.setValue((currentExpr != null ? currentExpr : "") + " "
                        + NumberFormatter.formatBigDecimal(input) + " " + op);
                display.setValue(NumberFormatter.formatBigDecimal(result));
                rawInput.setLength(0);
                rawInput.append(result.toPlainString());
            } catch (ArithmeticException e) {
                showError(e.getMessage());
                return;
            }
        } else {
            engine.evaluate(input);
            expression.setValue(NumberFormatter.formatBigDecimal(input) + " " + op);
        }

        engine.setOperator(op);
        activeOperator.setValue(op);
        isNewEntry = true;
        lastWasEquals = false;
        isClearAllState.setValue(false);
    }

    public void handleEquals() {
        if (isError) return;

        if (lastWasEquals && engine.hasRepeatedEqualsReady()) {
            BigDecimal current = parseRawInput();
            try {
                BigDecimal result = engine.evaluateRepeatedEquals(current);
                String exprStr = NumberFormatter.formatBigDecimal(current) + " "
                        + engine.getLastOperator() + " "
                        + NumberFormatter.formatBigDecimal(engine.getLastOperand()) + " =";
                expression.setValue(exprStr);
                String formattedResult = NumberFormatter.formatBigDecimal(result);
                display.setValue(formattedResult);
                rawInput.setLength(0);
                rawInput.append(result.toPlainString());
                saveHistoryItem(exprStr, formattedResult);
            } catch (ArithmeticException e) {
                showError(e.getMessage());
            }
            return;
        }

        if (lastWasEquals) return;

        BigDecimal input = parseRawInput();
        String currentExpr = expression.getValue();

        if (engine.hasPendingOperator()) {
            String fullExpr = (currentExpr != null ? currentExpr : "") + " "
                    + NumberFormatter.formatBigDecimal(input) + " =";
            try {
                BigDecimal result = engine.evaluate(input);
                String formattedResult = NumberFormatter.formatBigDecimal(result);
                expression.setValue(fullExpr);
                display.setValue(formattedResult);
                rawInput.setLength(0);
                rawInput.append(result.toPlainString());
                saveHistoryItem(fullExpr, formattedResult);
            } catch (ArithmeticException e) {
                expression.setValue(fullExpr);
                showError(e.getMessage());
                return;
            }
        } else {
            String fullExpr = NumberFormatter.formatBigDecimal(input) + " =";
            expression.setValue(fullExpr);
        }

        activeOperator.setValue(null);
        isNewEntry = true;
        lastWasEquals = true;
        isClearAllState.setValue(true);
    }

    public void handleClear() {
        engine.reset();
        rawInput.setLength(0);
        rawInput.append("0");
        display.setValue("0");
        expression.setValue("");
        activeOperator.setValue(null);
        isNewEntry = true;
        lastWasEquals = false;
        isError = false;
        isClearAllState.setValue(true);
    }

    public void handleDelete() {
        if (isError) {
            handleClear();
            return;
        }

        if (isNewEntry) {
            return;
        }

        if (rawInput.length() > 1) {
            rawInput.deleteCharAt(rawInput.length() - 1);
            if (rawInput.charAt(rawInput.length() - 1) == '.') {
                rawInput.deleteCharAt(rawInput.length() - 1);
            }
            if ("-".equals(rawInput.toString())) {
                rawInput.setLength(0);
                rawInput.append("0");
                isNewEntry = true;
            }
        } else {
            rawInput.setLength(0);
            rawInput.append("0");
            isNewEntry = true;
        }

        display.setValue(NumberFormatter.formatTypingInput(rawInput.toString()));
    }

    public void handleNegate() {
        if (isError) return;

        String current = rawInput.toString();
        if ("0".equals(current) || "0.0".equals(current)) {
            return;
        }

        if (current.startsWith("-")) {
            rawInput.deleteCharAt(0);
        } else {
            rawInput.insert(0, "-");
        }

        display.setValue(NumberFormatter.formatTypingInput(rawInput.toString()));
    }

    public void handlePercent() {
        if (isError) return;

        BigDecimal input = parseRawInput();
        BigDecimal percentVal = engine.calculatePercentage(input);
        rawInput.setLength(0);
        rawInput.append(percentVal.toPlainString());
        display.setValue(NumberFormatter.formatBigDecimal(percentVal));
        isNewEntry = true;
    }

    public void handleSquareRoot() {
        executeUnaryFunction("√", engine::squareRoot);
    }

    public void handleSquare() {
        executeUnaryFunction("sqr", engine::square);
    }

    public void handleReciprocal() {
        executeUnaryFunction("1/", engine::reciprocal);
    }

    public void handleSin() {
        executeUnaryFunction("sin", engine::sin);
    }

    public void handleCos() {
        executeUnaryFunction("cos", engine::cos);
    }

    public void handleTan() {
        executeUnaryFunction("tan", engine::tan);
    }

    public void handleLog10() {
        executeUnaryFunction("log", engine::log10);
    }

    public void handleNaturalLog() {
        executeUnaryFunction("ln", engine::naturalLog);
    }

    public void handleFactorial() {
        executeUnaryFunction("fact", engine::factorial);
    }

    public void toggleAngleMode() {
        engine.toggleAngleMode();
        isRadMode.setValue(engine.isRadMode());
    }

    // --- Memory Operations ---

    public void handleMemoryClear() {
        engine.memoryClear();
        hasMemory.setValue(false);
    }

    public void handleMemoryRecall() {
        BigDecimal val = engine.memoryRecall();
        rawInput.setLength(0);
        rawInput.append(val.toPlainString());
        display.setValue(NumberFormatter.formatBigDecimal(val));
        isNewEntry = true;
        lastWasEquals = false;
    }

    public void handleMemoryAdd() {
        if (isError) return;
        BigDecimal input = parseRawInput();
        engine.memoryAdd(input);
        hasMemory.setValue(engine.hasMemory());
        isNewEntry = true;
    }

    public void handleMemorySubtract() {
        if (isError) return;
        BigDecimal input = parseRawInput();
        engine.memorySubtract(input);
        hasMemory.setValue(engine.hasMemory());
        isNewEntry = true;
    }

    public void handleMemoryStore() {
        if (isError) return;
        BigDecimal input = parseRawInput();
        engine.memoryStore(input);
        hasMemory.setValue(engine.hasMemory());
        isNewEntry = true;
    }

    private interface UnaryMathFunction {
        BigDecimal apply(BigDecimal input) throws ArithmeticException;
    }

    private void executeUnaryFunction(String name, UnaryMathFunction func) {
        if (isError) return;

        BigDecimal input = parseRawInput();
        try {
            BigDecimal result = func.apply(input);
            String exprStr = name + "(" + NumberFormatter.formatBigDecimal(input) + ") =";
            expression.setValue(exprStr);
            String formattedResult = NumberFormatter.formatBigDecimal(result);
            display.setValue(formattedResult);
            rawInput.setLength(0);
            rawInput.append(result.toPlainString());
            saveHistoryItem(exprStr, formattedResult);
            isNewEntry = true;
            lastWasEquals = true;
        } catch (ArithmeticException e) {
            showError(e.getMessage() != null ? e.getMessage() : "Invalid input");
        }
    }

    public void onHistoryItemSelected(HistoryItem item) {
        if (item == null) return;
        String rawResult = item.getResult().replace(",", "");
        rawInput.setLength(0);
        rawInput.append(rawResult);
        display.setValue(NumberFormatter.formatBigDecimal(new BigDecimal(rawResult)));
        isNewEntry = true;
        lastWasEquals = false;
        activeOperator.setValue(null);
    }

    public void clearHistory() {
        historyRepository.clearHistory();
        loadHistory();
    }

    private void saveHistoryItem(String expr, String res) {
        HistoryItem item = new HistoryItem(expr, res);
        historyRepository.addHistory(item);
        loadHistory();
    }

    private BigDecimal parseRawInput() {
        try {
            return new BigDecimal(rawInput.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private void swapLastOperatorInExpression(String operator) {
        String expr = expression.getValue();
        if (expr != null) {
            int lastSpace = expr.trim().lastIndexOf(' ');
            if (lastSpace >= 0) {
                expression.setValue(expr.trim().substring(0, lastSpace + 1) + operator);
            } else {
                expression.setValue(operator);
            }
        }
    }

    private void showError(String msg) {
        display.setValue(msg != null && !msg.isEmpty() ? msg : "Error");
        rawInput.setLength(0);
        rawInput.append("0");
        engine.reset();
        activeOperator.setValue(null);
        isError = true;
        isNewEntry = true;
        lastWasEquals = false;
        isClearAllState.setValue(true);
    }
}
