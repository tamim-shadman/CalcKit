package com.tamim.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.tamim.calculator.data.PreferencesManager;
import com.tamim.calculator.databinding.ActivityMainBinding;
import com.tamim.calculator.ui.ConverterBottomSheetDialogFragment;
import com.tamim.calculator.ui.HistoryBottomSheetDialogFragment;
import com.tamim.calculator.ui.SettingsDialogFragment;
import com.tamim.calculator.util.NumberFormatter;
import com.tamim.calculator.viewmodel.CalculatorViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CalculatorViewModel viewModel;
    private PreferencesManager prefs;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = new PreferencesManager(this);
        prefs.applySavedTheme();

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        viewModel = new ViewModelProvider(this).get(CalculatorViewModel.class);

        setupObservers();
        wireDigitButtons();
        wireOperatorButtons();
        wireFunctionButtons();
        wireMemoryButtons();
        wireScientificButtons();
        setupDisplayShortcuts();
        setupHeaderActions();
    }

    private void performFeedback(View view) {
        if (prefs.isHapticEnabled()) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        }
        if (prefs.isSoundEnabled() && audioManager != null) {
            audioManager.playSoundEffect(SoundEffectConstants.CLICK);
        }
    }

    private void setupObservers() {
        viewModel.getDisplay().observe(this, text -> {
            binding.screen.setText(text);
        });

        viewModel.getExpression().observe(this, expr -> {
            binding.expression.setText(expr);
        });

        viewModel.getActiveOperator().observe(this, this::updateOperatorHighlight);

        viewModel.getIsClearAllState().observe(this, isClearAll -> {
            binding.ac.setText(isClearAll ? "AC" : "C");
        });

        viewModel.getIsRadMode().observe(this, isRad -> {
            if (binding.badgeAngleMode != null) {
                binding.badgeAngleMode.setText(isRad ? "RAD" : "DEG");
            }
        });

        viewModel.getHasMemory().observe(this, hasMem -> {
            if (binding.badgeMemory != null) {
                binding.badgeMemory.setVisibility(hasMem ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void updateOperatorHighlight(String activeOp) {
        Button[] ops = { binding.plus, binding.min, binding.times, binding.div };
        String[] opSymbols = { "+", "−", "×", "÷" };

        int defaultBg = R.drawable.btn_operator;
        int activeBg = R.drawable.btn_operator_active;
        int defaultColor = ContextCompat.getColor(this, R.color.colorTextOperator);
        int activeColor = ContextCompat.getColor(this, R.color.colorOperatorActiveText);

        for (int i = 0; i < ops.length; i++) {
            Button btn = ops[i];
            if (btn == null) continue;

            if (opSymbols[i].equals(activeOp)) {
                btn.setBackgroundResource(activeBg);
                btn.setTextColor(activeColor);
            } else {
                btn.setBackgroundResource(defaultBg);
                btn.setTextColor(defaultColor);
            }
        }
    }

    private void wireDigitButtons() {
        Button[] digitButtons = {
            binding.num0, binding.num1, binding.num2, binding.num3, binding.num4,
            binding.num5, binding.num6, binding.num7, binding.num8, binding.num9
        };

        for (Button btn : digitButtons) {
            if (btn != null) {
                btn.setOnClickListener(v -> {
                    performFeedback(v);
                    viewModel.appendDigit(((Button) v).getText().toString());
                });
            }
        }

        binding.point.setOnClickListener(v -> {
            performFeedback(v);
            viewModel.appendDecimal();
        });
    }

    private void wireOperatorButtons() {
        Button[] operatorButtons = {
            binding.plus, binding.min, binding.times, binding.div
        };

        for (Button btn : operatorButtons) {
            if (btn != null) {
                btn.setOnClickListener(v -> {
                    performFeedback(v);
                    viewModel.handleOperator(((Button) v).getText().toString());
                });
            }
        }

        binding.equal.setOnClickListener(v -> {
            performFeedback(v);
            viewModel.handleEquals();
        });
    }

    private void wireFunctionButtons() {
        binding.ac.setOnClickListener(v -> {
            performFeedback(v);
            viewModel.handleClear();
        });

        binding.del.setOnClickListener(v -> {
            performFeedback(v);
            viewModel.handleDelete();
        });

        binding.del.setOnLongClickListener(v -> {
            performFeedback(v);
            viewModel.handleClear();
            return true;
        });

        binding.percent.setOnClickListener(v -> {
            performFeedback(v);
            viewModel.handlePercent();
        });

        if (binding.negate != null) {
            binding.negate.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleNegate();
            });
        }
    }

    private void wireMemoryButtons() {
        if (binding.btnMC != null) {
            binding.btnMC.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleMemoryClear();
            });
        }
        if (binding.btnMR != null) {
            binding.btnMR.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleMemoryRecall();
            });
        }
        if (binding.btnMPlus != null) {
            binding.btnMPlus.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleMemoryAdd();
            });
        }
        if (binding.btnMMinus != null) {
            binding.btnMMinus.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleMemorySubtract();
            });
        }
        if (binding.btnMS != null) {
            binding.btnMS.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleMemoryStore();
            });
        }
    }

    private void wireScientificButtons() {
        if (binding.btnToggleScience != null) {
            binding.btnToggleScience.setOnClickListener(v -> {
                performFeedback(v);
                if (binding.panelScience != null) {
                    boolean isCurrentlyVisible = binding.panelScience.getVisibility() == View.VISIBLE;
                    binding.panelScience.setVisibility(isCurrentlyVisible ? View.GONE : View.VISIBLE);
                }
            });
        }

        if (binding.badgeAngleMode != null) {
            binding.badgeAngleMode.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.toggleAngleMode();
            });
        }

        if (binding.btnSin != null) {
            binding.btnSin.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleSin();
            });
        }

        if (binding.btnCos != null) {
            binding.btnCos.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleCos();
            });
        }

        if (binding.btnTan != null) {
            binding.btnTan.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleTan();
            });
        }

        if (binding.btnLog != null) {
            binding.btnLog.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleLog10();
            });
        }

        if (binding.btnLn != null) {
            binding.btnLn.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleNaturalLog();
            });
        }

        if (binding.btnPower != null) {
            binding.btnPower.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleOperator("^");
            });
        }

        if (binding.btnFact != null) {
            binding.btnFact.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleFactorial();
            });
        }

        if (binding.btnSqrt != null) {
            binding.btnSqrt.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleSquareRoot();
            });
        }

        if (binding.btnSquare != null) {
            binding.btnSquare.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleSquare();
            });
        }

        if (binding.btnReciprocal != null) {
            binding.btnReciprocal.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.handleReciprocal();
            });
        }

        if (binding.btnPi != null) {
            binding.btnPi.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.appendDigit(String.valueOf(Math.PI));
            });
        }

        if (binding.btnE != null) {
            binding.btnE.setOnClickListener(v -> {
                performFeedback(v);
                viewModel.appendDigit(String.valueOf(Math.E));
            });
        }
    }

    private void setupHeaderActions() {
        if (binding.btnHistory != null) {
            binding.btnHistory.setOnClickListener(v -> {
                performFeedback(v);
                showHistoryBottomSheet();
            });
        }

        if (binding.btnConverter != null) {
            binding.btnConverter.setOnClickListener(v -> {
                performFeedback(v);
                ConverterBottomSheetDialogFragment.newInstance()
                        .show(getSupportFragmentManager(), "ConverterBottomSheet");
            });
        }

        if (binding.btnSettings != null) {
            binding.btnSettings.setOnClickListener(v -> {
                performFeedback(v);
                SettingsDialogFragment.newInstance()
                        .show(getSupportFragmentManager(), "SettingsDialog");
            });
        }
    }

    private void showHistoryBottomSheet() {
        HistoryBottomSheetDialogFragment fragment = HistoryBottomSheetDialogFragment.newInstance();
        fragment.setViewModel(viewModel);
        fragment.setCallback(item -> {
            viewModel.onHistoryItemSelected(item);
        });
        fragment.show(getSupportFragmentManager(), "HistoryBottomSheet");
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
            Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
        }
    }

    public static String formatNumber(double value) {
        return NumberFormatter.formatNumber(value);
    }
}
