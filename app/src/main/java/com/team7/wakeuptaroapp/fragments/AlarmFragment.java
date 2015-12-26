package com.team7.wakeuptaroapp.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.team7.wakeuptaroapp.R;

/**
 * アラームの登録 / 更新の項目を扱うフラグメント。
 *
 * @author Naotake.K
 */
public class AlarmFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.alarm_settings);
    }
}
