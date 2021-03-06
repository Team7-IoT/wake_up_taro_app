package com.team7.wakeuptaroapp.views.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.team7.wakeuptaroapp.utils.Alarms;

/**
 * アラームの時間を選択するための Preference クラス。
 *
 * @author Naotake.K
 * @see {@link https://gist.github.com/nickaknudson/5024416}
 */
public class AlarmTimePreference extends DialogPreference {
    private int mHour = 0;
    private int mMinute = 0;
    private TimePicker picker = null;

    public AlarmTimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    public void setTime(int hour, int minute, String time) {
        mHour = hour;
        mMinute = minute;
        persistString(time);
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    public void updateSummary() {
        setSummary(Alarms.formatTime(mHour, mMinute));
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(true);
        return picker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setCurrentHour(mHour);
        picker.setCurrentMinute(mMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            int currHour = picker.getCurrentHour();
            int currMinute = picker.getCurrentMinute();

            String time = Alarms.formatTime(currHour, currMinute);
            if (!callChangeListener(time)) {
                return;
            }

            // persist
            setTime(currHour, currMinute, time);
            updateSummary();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String time;
        if (restorePersistedValue) {
            time = getPersistedString(Alarms.DEFAULT_VALUE);
        } else {
            time = defaultValue.toString();
        }

        int currHour = Alarms.selectHour(time);
        int currMinute = Alarms.selectMinute(time);
        // need to persist here for default value to work
        setTime(currHour, currMinute, time);
        updateSummary();
    }
}