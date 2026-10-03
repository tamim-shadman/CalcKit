package com.tamim.calculator.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tamim.calculator.R;
import com.tamim.calculator.converter.UnitCategory;
import com.tamim.calculator.converter.UnitConverter;
import com.tamim.calculator.databinding.BottomSheetConverterBinding;

import java.util.List;

public class ConverterBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private BottomSheetConverterBinding binding;
    private UnitCategory currentCategory = UnitCategory.LENGTH;
    private final StringBuilder inputBuilder = new StringBuilder("1");

    public static ConverterBottomSheetDialogFragment newInstance() {
        return new ConverterBottomSheetDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetConverterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCategoryChips();
        updateSpinners();
        wireKeypad();

        binding.btnSwapUnits.setOnClickListener(v -> {
            int fromPos = binding.spinnerFrom.getSelectedItemPosition();
            int toPos = binding.spinnerTo.getSelectedItemPosition();
            binding.spinnerFrom.setSelection(toPos);
            binding.spinnerTo.setSelection(fromPos);
            recalculate();
        });

        binding.btnCopyConverted.setOnClickListener(v -> {
            String resultText = binding.textToValue.getText().toString();
            Context context = requireContext();
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText("Converted Result", resultText);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategoryChips() {
        binding.chipGroupCategory.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipLength) currentCategory = UnitCategory.LENGTH;
            else if (checkedId == R.id.chipWeight) currentCategory = UnitCategory.WEIGHT;
            else if (checkedId == R.id.chipTemp) currentCategory = UnitCategory.TEMPERATURE;
            else if (checkedId == R.id.chipData) currentCategory = UnitCategory.DATA;
            else if (checkedId == R.id.chipSpeed) currentCategory = UnitCategory.SPEED;
            else if (checkedId == R.id.chipArea) currentCategory = UnitCategory.AREA;

            updateSpinners();
        });
    }

    private void updateSpinners() {
        List<UnitCategory.Unit> units = currentCategory.getUnits();
        ArrayAdapter<UnitCategory.Unit> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                units
        );

        binding.spinnerFrom.setAdapter(adapter);
        binding.spinnerTo.setAdapter(adapter);

        // Default: from index 0, to index 1 (if available)
        binding.spinnerFrom.setSelection(0);
        binding.spinnerTo.setSelection(units.size() > 1 ? 1 : 0);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recalculate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        binding.spinnerFrom.setOnItemSelectedListener(listener);
        binding.spinnerTo.setOnItemSelectedListener(listener);

        recalculate();
    }

    private void wireKeypad() {
        Button[] digitButtons = {
                binding.btnC0, binding.btnC1, binding.btnC2, binding.btnC3,
                binding.btnC4, binding.btnC5, binding.btnC6, binding.btnC7,
                binding.btnC8, binding.btnC9
        };

        for (Button btn : digitButtons) {
            btn.setOnClickListener(v -> {
                String digit = ((Button) v).getText().toString();
                if ("0".equals(inputBuilder.toString())) {
                    inputBuilder.setLength(0);
                }
                inputBuilder.append(digit);
                recalculate();
            });
        }

        binding.btnCDot.setOnClickListener(v -> {
            if (!inputBuilder.toString().contains(".")) {
                if (inputBuilder.length() == 0) {
                    inputBuilder.append("0");
                }
                inputBuilder.append(".");
                recalculate();
            }
        });

        binding.btnCDel.setOnClickListener(v -> {
            if (inputBuilder.length() > 1) {
                inputBuilder.deleteCharAt(inputBuilder.length() - 1);
            } else {
                inputBuilder.setLength(0);
                inputBuilder.append("0");
            }
            recalculate();
        });

        binding.btnCClear.setOnClickListener(v -> {
            inputBuilder.setLength(0);
            inputBuilder.append("0");
            recalculate();
        });

        binding.btnCNegate.setOnClickListener(v -> {
            if ("0".equals(inputBuilder.toString())) return;
            if (inputBuilder.charAt(0) == '-') {
                inputBuilder.deleteCharAt(0);
            } else {
                inputBuilder.insert(0, "-");
            }
            recalculate();
        });

        binding.btnCDone.setOnClickListener(v -> dismiss());
    }

    private void recalculate() {
        binding.textFromValue.setText(inputBuilder.toString());

        double inputVal;
        try {
            inputVal = Double.parseDouble(inputBuilder.toString());
        } catch (NumberFormatException e) {
            inputVal = 0.0;
        }

        int fromIdx = binding.spinnerFrom.getSelectedItemPosition();
        int toIdx = binding.spinnerTo.getSelectedItemPosition();

        double converted = UnitConverter.convert(currentCategory, fromIdx, toIdx, inputVal);
        binding.textToValue.setText(UnitConverter.formatResult(converted));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
