package com.team7.wakeuptaroapp.views.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;

import com.team7.wakeuptaroapp.views.helpers.DayOfWeekHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * アラームの曜日を選択するための Preference クラス。
 *
 * @author Naotake.K
 */
public class AlarmDayOfWeekPreference extends MultiSelectListPreference {

    public AlarmDayOfWeekPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnPreferenceChangeListener(listener);
    }

    public AlarmDayOfWeekPreference(Context context) {
        super(context);
        setOnPreferenceChangeListener(listener);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        setSummary(DayOfWeekHelper.convertToLabel(getContext(), getValues()));
    }

    @VisibleForTesting
    static final OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            AlarmDayOfWeekPreference pref = AlarmDayOfWeekPreference.class.cast(preference);

            Set<String> selectedDays = (HashSet<String>) newValue;
            pref.setSummary(DayOfWeekHelper.convertToLabel(pref.getContext(), selectedDays));

            return true;
        }
    };
}
