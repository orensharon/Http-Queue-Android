package com.orensharon.httpqueue.presentation.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.util.Locale;

public class PercentageTextView extends androidx.appcompat.widget.AppCompatTextView {
    public PercentageTextView(Context context) {
        this(context, null);
    }

    public PercentageTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init(){

    }

    public void setPercentage(int percentage) {
        if (percentage < 0 || percentage > 100) {
            return;
        }
        this.setTextColor(this.generateColorByPercentage(percentage));
        this.setText(String.format(Locale.US, "%d%%", percentage));
    }

    private int generateColorByPercentage(int percentage) {
        int r = (255 * (100 - percentage)) / 100;
        int g = (255 * percentage) / 100;
        int b = 0;
        return Color.argb(255, r, g, b);
    }

}
