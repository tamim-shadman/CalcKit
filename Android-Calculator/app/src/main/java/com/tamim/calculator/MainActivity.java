package com.tamim.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tamim.calculator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private final StringBuilder currentInput = new StringBuilder("0");
    private boolean isNewEntry = true;
    private boolean lastWasEquals = false;

    private final Calculator calculator = new Calculator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wireDigitButtons();
        wireOperatorButtons();
        wireFunctionButtons();
        setupDisplayShortcuts();
    }

    private void performFeedback(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
    }

    private void wireDigitButtons() {
        Button[] digitButtons = {
            binding.num0, binding.num1, binding.num2, binding.num3, binding.num4,
            binding.num5, binding.num6, binding.num7, binding.num8, binding.num9
        };

        for (Button btn : digitButtons) {
            btn.setOnClickListener(v -> {
                performFeedback(v);
                appendDigit(((Button) v).getText().toString());
            });
        }

        binding.point.setOnClickListener(v -> {
            performFeedback(v);
            appendDecimal();
        });
    }

    private void wireOperatorButtons() {
        Button[] operatorButtons = {
            binding.plus, binding.min, binding.times, binding.div
        };

        for (Button btn : operatorButtons) {
            btn.setOnClickListener(v -> {
                performFeedback(v);
                handleOperator(((Button) v).getText().toString());
            });
        }

        binding.equal.setOnClickListener(v -> {
            performFeedback(v);
            handleEquals();
        });
    }

    private void wireFunctionButtons() {
        binding.ac.setOnClickListener(v -> {
            performFeedback(v);
            handleClear();
        });

        binding.del.setOnClickListener(v -> {
            performFeedback(v);
            handleDelete();
        });

        binding.del.setOnLongClickListener(v -> {
            performFeedback(v);
            handleClear();
            return true;
        });

        binding.percent.setOnClickListener(v -> {
            performFeedback(v);
            handlePercent();
        });
    }

    private void setupDisplayShortcuts() {
        View.OnLongClickListener copyListener = v -> {
            performFeedback(v);
            copyToClipboard(binding.screen.getText().toString());
            return true;
        };

        binding.screen.setOnLongClickListener(copyListener);
        binding.expression.setOnLongClickListener(copyListener);
    }

    private void copyToClipboard(String text) {
        if (text == null || text.isEmpty() || text.equals(getString(R.string.error_label))) {
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("Calculator Result", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    private void appendDigit(String digit) {
        if (isNewEntry) {
            currentInput.setLength(0);
            currentInput.append(digit);
            isNewEntry = false;
            lastWasEquals = false;
        } else {
            if ("0".equals(currentInput.toString())) {
                currentInput.setLength(0);
            }
            currentInput.append(digit);
        }
        binding.screen.setText(currentInput);
    }

    private void appendDecimal() {
        if (isNewEntry) {
            currentInput.setLength(0);
            currentInput.append("0.");
            isNewEntry = false;
            lastWasEquals = false;
        } else if (!currentInput.toString().contains(".")) {
            currentInput.append(".");
        }
        binding.screen.setText(currentInput);
    }

    private void handleOperator(String operator) {
        if (isNewEntry && calculator.hasPendingOperator()) {
            calculator.setOperator(operator);
            swapLastOperatorInExpression(operator);
            return;
        }

        double input = parseCurrentInput();

        if (lastWasEquals) {
            calculator.storeValue(input);
            binding.expression.setText(formatNumber(input) + " " + operator);
        } else if (calculator.hasPendingOperator()) {
            try {
                double result = calculator.evaluate(input);
                binding.expression.setText(
                    binding.expression.getText() + " " + formatNumber(input) + " " + operator
                );
                updateDisplay(result);
            } catch (ArithmeticException e) {
                showError();
                return;
            }
        } else {
            calculator.evaluate(input);
            binding.expression.setText(formatNumber(input) + " " + operator);
        }

        calculator.setOperator(operator);
        isNewEntry = true;
        lastWasEquals = false;
    }

    private void handleEquals() {
        if (lastWasEquals) return;

        double input = parseCurrentInput();
        String pendingOp = calculator.getPendingOperator();

        String newExpr = (pendingOp != null)
            ? binding.expression.getText() + " " + formatNumber(input) + " ="
            : formatNumber(input) + " =";

        try {
            double result = calculator.evaluate(input);
            binding.expression.setText(newExpr);
            updateDisplay(result);
            calculator.storeValue(result);
        } catch (ArithmeticException e) {
            binding.expression.setText(newExpr);
            showError();
            return;
        }

        isNewEntry = true;
        lastWasEquals = true;
    }

    private void handleClear() {
        calculator.reset();
        currentInput.setLength(0);
        currentInput.append("0");
        binding.screen.setText("0");
        binding.expression.setText("");
        isNewEntry = true;
        lastWasEquals = false;
    }

    private void handleDelete() {
        if (isNewEntry) return;

        if (currentInput.length() > 1) {
            currentInput.deleteCharAt(currentInput.length() - 1);
            if (currentInput.charAt(currentInput.length() - 1) == '.') {
                currentInput.deleteCharAt(currentInput.length() - 1);
            }
        } else {
            currentInput.setLength(0);
            currentInput.append("0");
        }
        binding.screen.setText(currentInput);
    }

    private void handlePercent() {
        double value = parseCurrentInput() / 100.0;
        String formatted = formatNumber(value);
        currentInput.setLength(0);
        currentInput.append(formatted);
        binding.screen.setText(formatted);
    }

    private double parseCurrentInput() {
        try {
            return Double.parseDouble(currentInput.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateDisplay(double value) {
        String formatted = formatNumber(value);
        currentInput.setLength(0);
        currentInput.append(formatted);
        binding.screen.setText(formatted);
    }

    private void swapLastOperatorInExpression(String operator) {
        String expr = binding.expression.getText().toString().trim();
        int lastSpace = expr.lastIndexOf(' ');
        if (lastSpace >= 0) {
            binding.expression.setText(expr.substring(0, lastSpace + 1) + operator);
        }
    }

    private void showError() {
        binding.screen.setText(R.string.error_label);
        binding.expression.setText("");
        currentInput.setLength(0);
        currentInput.append("0");
        calculator.reset();
        isNewEntry = true;
        lastWasEquals = false;
    }

    static String formatNumber(double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) return "Error";
        if (value == 0) return "0";

        if (value == Math.floor(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }

        return String.format("%.8f", value)
                      .replaceAll("0+$", "")
                      .replaceAll("\\.$", "");
    }
}
