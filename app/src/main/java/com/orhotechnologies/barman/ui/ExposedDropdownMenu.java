package com.orhotechnologies.barman.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class ExposedDropdownMenu extends MaterialAutoCompleteTextView {

    public ExposedDropdownMenu(@NonNull Context context) {
        super(context);
    }

    public ExposedDropdownMenu(@NonNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ExposedDropdownMenu(@NonNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    @Override
    public boolean getFreezesText() {
        return false;
    }
}