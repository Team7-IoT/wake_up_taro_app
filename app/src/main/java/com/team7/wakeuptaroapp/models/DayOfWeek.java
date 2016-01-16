package com.team7.wakeuptaroapp.models;

import android.support.annotation.NonNull;

import com.team7.wakeuptaroapp.R;

import org.joda.time.DateTimeConstants;

/**
 * @author Naotake.K
 */
public enum DayOfWeek {

    /**
     * 月曜日
     */
    MONDAY(DateTimeConstants.MONDAY, "1_mon", R.string.label_alarm_mon),
    /**
     * 火曜日
     */
    TUESDAY(DateTimeConstants.TUESDAY, "2_tue", R.string.label_alarm_tue),
    /**
     * 水曜日
     */
    WEDNESDAY(DateTimeConstants.WEDNESDAY, "3_wed", R.string.label_alarm_wed),
    /**
     * 木曜日
     */
    THURSDAY(DateTimeConstants.THURSDAY, "4_thu", R.string.label_alarm_thu),
    /**
     * 金曜日
     */
    FRIDAY(DateTimeConstants.FRIDAY, "5_fri", R.string.label_alarm_fri),
    /**
     * 土曜日
     */
    SATURDAY(DateTimeConstants.SATURDAY, "6_sat", R.string.label_alarm_sat),
    /**
     * 日曜日
     */
    SUNDAY(DateTimeConstants.SUNDAY, "7_sun", R.string.label_alarm_sun);

    private final int constants;
    private final String key;
    private final int resId;

    DayOfWeek(int constants, String key, int resId) {
        this.constants = constants;
        this.key = key;
        this.resId = resId;
    }

    /**
     * 自身が表す曜日に応じた {@link DateTimeConstants} の値を取得する。
     *
     * @return 曜日を表す {@link DateTimeConstants}
     */
    public int getConstants() {
        return constants;
    }

    /**
     * 自身が表す曜日に応じたリソース ID を取得する。<br />
     * {@link android.content.Context#getString(int)} などで使用するためのキー情報。
     *
     * @return リソース ID
     */
    public int getResId() {
        return resId;
    }

    /**
     * 曜日の文字列を基にオブジェクトを解決する。
     *
     * @param key 曜日を表すキー文字列
     * @return 該当する {@link DayOfWeek} のオブジェクト
     * @throws IllegalArgumentException 不正な曜日のキーを指定した場合
     */
    public static DayOfWeek resolve(@NonNull String key) {
        for (DayOfWeek dow : DayOfWeek.values()) {
            if (key.equals(dow.key)) {
                return dow;
            }
        }
        throw new IllegalArgumentException("Illegal Day of Week: " + key);
    }
}
