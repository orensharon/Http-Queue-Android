package com.orensharon.httpqueue.presentation.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.util.Locale;

public class RatioTextView extends androidx.appcompat.widget.AppCompatTextView {
    public RatioTextView(Context context) {
        this(context, null);
    }

    public RatioTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init(){

    }

    public void setRatio(int ratio) {
        if (ratio < 0 || ratio > 100) {
            return;
        }
        this.setTextColor(this.generateColorByRatio(ratio));
        this.setText(String.format(Locale.US, "%d%%", ratio));
    }

    private int generateColorByRatio(int ratio) {
        int r = (255 * (100 - ratio)) / 100;
        int g = (255 * ratio) / 100;
        int b = 0;
        return Color.argb(255, r, g, b);
    }

}
