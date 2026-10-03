package com.tamim.calculator.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tamim.calculator.R;
import com.tamim.calculator.data.PreferencesManager;
import com.tamim.calculator.databinding.DialogSettingsBinding;

public class SettingsDialogFragment extends DialogFragment {

    private DialogSettingsBinding binding;
    private PreferencesManager prefs;

    public static SettingsDialogFragment newInstance() {
        return new SettingsDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        prefs = new PreferencesManager(requireContext());
        binding = DialogSettingsBinding.inflate(LayoutInflater.from(getContext()));

        initViews();

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(binding.getRoot())
                .setPositiveButton("Done", null)
                .create();
    }

    private void initViews() {
        int themeMode = prefs.getThemeMode();
        if (themeMode == PreferencesManager.THEME_LIGHT) {
            binding.radioThemeLight.setChecked(true);
        } else if (themeMode == PreferencesManager.THEME_DARK) {
            binding.radioThemeDark.setChecked(true);
        } else {
            binding.radioThemeSystem.setChecked(true);
        }

        binding.radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioThemeLight) {
                prefs.setThemeMode(PreferencesManager.THEME_LIGHT);
            } else if (checkedId == R.id.radioThemeDark) {
                prefs.setThemeMode(PreferencesManager.THEME_DARK);
            } else {
                prefs.setThemeMode(PreferencesManager.THEME_SYSTEM);
            }
        });

        binding.switchHaptic.setChecked(prefs.isHapticEnabled());
        binding.switchHaptic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setHapticEnabled(isChecked);
        });

        binding.switchSound.setChecked(prefs.isSoundEnabled());
        binding.switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setSoundEnabled(isChecked);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
