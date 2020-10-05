package com.orensharon.brainq.util;

import android.widget.RadioGroup;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.orensharon.BrainQ;
import com.orensharon.brainq.R;

public class BindingAdapters {

    @BindingAdapter("timeScale")
    public static void setTimeScale(RadioGroup radioGroup, Integer timeScale){
        int id;
        switch (timeScale) {
            case BrainQ.TimeScale.HOURLY:
                id = R.id.hour;
                break;
            case BrainQ.TimeScale.DAILY:
                id = R.id.day;
                break;
            case BrainQ.TimeScale.WEEKLY:
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
            case R.id.hour:
                return BrainQ.TimeScale.HOURLY;
            case R.id.day:
                return BrainQ.TimeScale.DAILY;
            case R.id.week:
                return BrainQ.TimeScale.WEEKLY;
        }
        return BrainQ.TimeScale.HOURLY;
    }

    @BindingAdapter("timeScaleAttrChanged")
    public static void setListener(RadioGroup radioGroup, final InverseBindingListener listener) {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> listener.onChange());
    }
}