package com.team7.wakeuptaroapp.views.preferences;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.Preference;
import android.preference.RingtonePreference;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.team7.wakeuptaroapp.R;

/**
 * アラームの音を選択するための Preference クラス。
 *
 * @author Naotake.K
 */
public class AlarmRingtonePreference extends RingtonePreference {

    public AlarmRingtonePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnPreferenceChangeListener(listener);
    }

    public AlarmRingtonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnPreferenceChangeListener(listener);
    }

    public AlarmRingtonePreference(Context context) {
        super(context);
        setOnPreferenceChangeListener(listener);
    }

    /**
     * サイレントとしてサマリー情報を設定する。
     */
    public void setAsSilentSummary() {
        setSummary(R.string.label_alarm_silent);
    }

    /**
     * 指定された着信音のタイトルをサマリー情報に設定する。
     *
     * @param ringtone 着信音
     */
    public void setAsRingtoneSummary(Ringtone ringtone) {
        if (ringtone == null) {
            setSummary(null);
        } else {
            String name = ringtone.getTitle(this.getContext());
            setSummary(name);
        }
    }

    /**
     * 着信音のサマリー情報を制御する。<br />
     * 指定された着信音の URI が null の場合、サイレントを設定する。
     * それ以外の場合、対応する着信音のタイトルを設定する。
     *
     * @param ringtoneUri 着信音の URI
     */
    public void handleSummary(String ringtoneUri) {
        if (TextUtils.isEmpty(ringtoneUri)) {
            setAsSilentSummary();
        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(getContext(), Uri.parse(ringtoneUri));
            setAsRingtoneSummary(ringtone);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String persisted = getPersistedString(null);
        handleSummary(persisted);
    }

    @VisibleForTesting
    static final OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            AlarmRingtonePreference pref = AlarmRingtonePreference.class.cast(preference);
            String ringtoneUri = newValue.toString();
            pref.handleSummary(ringtoneUri);

            return true;
        }
    };
}
