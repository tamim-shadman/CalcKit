package com.tamim.calculator.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tamim.calculator.databinding.BottomSheetHistoryBinding;
import com.tamim.calculator.model.HistoryItem;
import com.tamim.calculator.viewmodel.CalculatorViewModel;

import java.util.List;

public class HistoryBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public interface OnHistoryItemSelectedCallback {
        void onHistoryItemSelected(HistoryItem item);
    }

    private BottomSheetHistoryBinding binding;
    private CalculatorViewModel viewModel;
    private OnHistoryItemSelectedCallback callback;
    private HistoryAdapter adapter;

    public static HistoryBottomSheetDialogFragment newInstance() {
        return new HistoryBottomSheetDialogFragment();
    }

    public void setViewModel(CalculatorViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setCallback(OnHistoryItemSelectedCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new HistoryAdapter(item -> {
            if (callback != null) {
                callback.onHistoryItemSelected(item);
            }
            dismiss();
        });

        binding.recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHistory.setAdapter(adapter);

        binding.btnClearHistory.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Clear History")
                    .setMessage("Are you sure you want to delete all calculation history?")
                    .setPositiveButton("Clear", (dialog, which) -> {
                        if (viewModel != null) {
                            viewModel.clearHistory();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        if (viewModel != null) {
            viewModel.getHistoryList().observe(getViewLifecycleOwner(), this::updateList);
        }
    }

    private void updateList(List<HistoryItem> items) {
        if (items == null || items.isEmpty()) {
            binding.layoutEmptyHistory.setVisibility(View.VISIBLE);
            binding.recyclerHistory.setVisibility(View.GONE);
            binding.btnClearHistory.setEnabled(false);
        } else {
            binding.layoutEmptyHistory.setVisibility(View.GONE);
            binding.recyclerHistory.setVisibility(View.VISIBLE);
            binding.btnClearHistory.setEnabled(true);
            adapter.setItems(items);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
