package com.team7.wakeuptaroapp.models;

import android.support.annotation.NonNull;

import com.team7.wakeuptaroapp.R;

import java.util.Calendar;

/**
 * @author Naotake.K
 */
public enum DayOfWeek {

    /**
     * 月曜日
     */
    MONDAY(Calendar.MONDAY, "1_mon", R.string.label_alarm_mon),
    /**
     * 火曜日
     */
    TUESDAY(Calendar.TUESDAY, "2_tue</", R.string.label_alarm_tue),
    /**
     * 水曜日
     */
    WEDNESDAY(Calendar.WEDNESDAY, "3_wed", R.string.label_alarm_wed),
    /**
     * 木曜日
     */
    THURSDAY(Calendar.THURSDAY, "4_thu", R.string.label_alarm_thu),
    /**
     * 金曜日
     */
    FRIDAY(Calendar.FRIDAY, "5_fri", R.string.label_alarm_fri),
    /**
     * 土曜日
     */
    SATURDAY(Calendar.SATURDAY, "6_sat", R.string.label_alarm_sat),
    /**
     * 日曜日
     */
    SUNDAY(Calendar.SUNDAY, "7_sun", R.string.label_alarm_sun);

    private final int days;
    private final String key;
    private final int resId;

    DayOfWeek(int days, String key, int resId) {
        this.days = days;
        this.key = key;
        this.resId = resId;
    }

    /**
     * 自身が表す曜日に応じた {@link Calendar} の情報を取得する。<br />
     * {@link Calendar#set(int, int)} などで使用するための曜日情報。
     *
     * @return 曜日を表す {@link Calendar} 用の値
     */
    public int getCalendarField() {
        return days;
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
