package com.orensharon.httpqueue.presentation.util;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.orensharon.httpqueue.R;
import com.orensharon.httpqueue.presentation.TimeScale;
import com.orensharon.httpqueue.presentation.view.RatioTextView;

public class BindingAdapters {

    @BindingAdapter("ratio")
    public static void setTimeScale(RatioTextView textView, Integer ratio){
        textView.setRatio(ratio);
    }

    @BindingAdapter("timeScale")
    public static void setTimeScale(RadioGroup radioGroup, Integer timeScale){
        int id;
        switch (timeScale) {
            case TimeScale.MINUTELY:
                id = R.id.minute;
                break;
            case TimeScale.HOURLY:
                id = R.id.hour;
                break;
            case TimeScale.DAILY:
                id = R.id.day;
                break;
            case TimeScale.WEEKLY:
                id = R.id.week;
                break;
            default:
                return;
        }

        radioGroup.check(id);
    }

    @InverseBindingAdapter(attribute = "timeScale", event = "timeScaleAttrChanged")
    public static int getTimeScale(RadioGroup radioGroup) {
        int id = radioGroup.getCheckedRadioButtonId();
        switch (id) {
            case R.id.minute:
                return TimeScale.MINUTELY;
            case R.id.hour:
                return TimeScale.HOURLY;
            case R.id.day:
                return TimeScale.DAILY;
            case R.id.week:
                return TimeScale.WEEKLY;
        }
        return TimeScale.MINUTELY;
    }

    @BindingAdapter("timeScaleAttrChanged")
    public static void setListener(RadioGroup radioGroup, final InverseBindingListener listener) {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> listener.onChange());
    }

    @BindingAdapter("onChecked")
    public static void setListener(RadioButton radioButton, final InverseBindingListener listener) {
        radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                return;
            }
            listener.onChange();
        });
    }
}
