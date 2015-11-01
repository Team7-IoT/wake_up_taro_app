package com.team7.wakeuptaroapp.util;

import android.util.Log;

/**
 * 起こしタロウ用に{@link Log}を拡張したログクラス。<br />
 * 内部的にはアプリ固有のタグ文字を指定した形で{@link Log}の各ログ出力メソッドを呼び出している。
 *
 * @author Naotake.K
 */
public class AppLog {

    // インスタンス化を抑制
    private AppLog() {
        // NOP
    }

    /**
     * {@link Log#v(String, String)}相当のログメッセージを出力する。
     *
     * @param msg 出力メッセージ
     */
    public static int v(String msg) {
        return Log.v(prefix(), msg);
    }

    /**
     * {@link Log#v(String, String, Throwable)}相当のログメッセージを出力する。
     *
     * @param msg 出力メッセージ
     * @param tr  発生した例外
     */
    public static int v(String msg, Throwable tr) {
        return Log.v(prefix(), msg, tr);
    }

    /**
     * {@link Log#d(String, String)}相当のログメッセージを出力する。
     *
     * @param msg 出力メッセージ
     */
    public static int d(String msg) {
        return Log.d(prefix(), msg);
    }

    /**
     * {@link Log#d(String, String, Throwable)}相当のログメッセージを出力する。
     *
     * @param msg 出力メッセージ
     * @param tr  発生した例外
     */
    public static int d(String msg, Throwable tr) {
        return Log.d(prefix(), msg, tr);
    }

    /**
     * {@link Log#i(String, String)}相当のログメッセージを出力する。
     *
     * @param msg 出力メッセージ
     */
    public static int i(String msg) {
        return Log.i(prefix(), msg);
    }

    /**
     * {@link Log#i(String, String, Throwable)}相当のログメッセージを出力する。
     *
     * @param msg 出力メッセージ
     * @param tr  発生した例外
     */
    public static int i(String msg, Throwable tr) {
        return Log.i(prefix(), msg, tr);
    }

    /**
     * {@link Log#w(String, String)}相当のログメッセージを出力する。
     *
     * @param msg 出力メッセージ
     */
    public static int w(String msg) {
        return Log.w(prefix(), msg);
    }

    /**
     * {@link Log#w(String, String, Throwable)}相当のログメッセージを出力する。
     *
     * @param msg 出力メッセージ
     * @param tr  発生した例外
     */
    public static int w(String msg, Throwable tr) {
        return Log.w(prefix(), msg, tr);
    }

    /**
     * {@link Log#w(String, Throwable)}相当のログメッセージを出力する。
     *
     * @param tr 発生した例外
     */
    public static int w(Throwable tr) {
        return Log.w(prefix(), tr);
    }

    /**
     * {@link Log#e(String, String)}相当のログメッセージを出力する。
     *
     * @param msg 出力メッセージ
     */
    public static int e(String msg) {
        return Log.e(prefix(), msg);
    }

    /**
     * {@link Log#e(String, String, Throwable)}相当のログメッセージを出力する。
     *
     * @param msg 出力メッセージ
     * @param tr  発生した例外
     */
    public static int e(String msg, Throwable tr) {
        return Log.e(prefix(), msg, tr);
    }

    private static String prefix() {
        return "TARO";
    }
}
