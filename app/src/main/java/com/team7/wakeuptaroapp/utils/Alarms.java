package com.team7.wakeuptaroapp.utils;

import java.util.Locale;

/**
 * アラーム情報に関する横断的な振る舞いを定義するユーティリティクラス。
 *
 * @author Naotake.K
 */
public final class Alarms {

    public static final String DEFAULT_VALUE = "00:00";

    /**
     * インスタンス化を抑制。
     */
    private Alarms() {
        // NOP
    }

    /**
     * 時間と分の情報を基に HH:MM 形式の文字列へ整形する。
     *
     * @param hour   時間
     * @param minute 分
     * @return HH:MM
     */
    public static String formatTime(int hour, int minute) {
        return String.format(Locale.JAPAN, "%02d:%02d", hour, minute);
    }

    /**
     * HH:MM 形式の時刻情報から時間 (HH) 部分だけを取り出す。
     *
     * @param time HH:MM
     * @return 時間
     */
    public static int selectHour(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[0]);
    }

    /**
     * HH:MM 形式の時刻情報から分 (MM) 部分だけを取り出す。
     *
     * @param time HH:MM
     * @return 分
     */
    public static int selectMinute(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[1]);
    }
}
