package com.team7.wakeuptaroapp.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.views.preferences.SettingCensorPreference;
import com.team7.wakeuptaroapp.views.preferences.SettingConnectionPreference;

/**
 * 起こしタロウの設定項目を扱うフラグメント。
 *
 * @author Naotake.K
 */
public class SettingFragment extends PreferenceFragment {

    // 疎通検証 Preference の INDEX
    private static final int INDEX_OF_TRY_CONNECT = 0;
    // センサー検証 Preference の INDEX
    private static final int INDEX_OF_TRY_CENSOR = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        // 疎通検証 Preference
        Preference pref = getPreferenceScreen().getPreference(INDEX_OF_TRY_CONNECT);
        SettingConnectionPreference.class.cast(pref).setActivity(getActivity());

        // センサー検証 Preference
        pref = getPreferenceScreen().getPreference(INDEX_OF_TRY_CENSOR);
        SettingCensorPreference.class.cast(pref).setActivity(getActivity());
    }
}
