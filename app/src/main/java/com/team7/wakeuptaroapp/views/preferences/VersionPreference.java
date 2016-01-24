package com.team7.wakeuptaroapp.views.preferences;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import com.team7.wakeuptaroapp.BuildConfig;

/**
 * 設定画面でアプリのバージョン情報を確認するための Preference クラス。
 *
 * @author Naotake.K
 */
public class VersionPreference extends Preference {

    public VersionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        setSummary(BuildConfig.VERSION_NAME);
    }
}
