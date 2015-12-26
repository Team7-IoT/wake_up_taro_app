package com.team7.wakeuptaroapp.utils;

/**
 * {@code Guava} に代わる事前条件式を検証するためのクラス。<br />
 * {@code Guava} を使うとメソッド数 65k の制限に引っかかる恐れが高くなるため依存していない。
 *
 * @author Naotake.K
 */
public class Preconditions {

    /**
     * インスタンス化を抑制。
     */
    private Preconditions() {
        // NOP
    }

    /**
     * 指定した条件式が true であることを検証する。
     *
     * @param condition 条件式
     * @param message   条件を満たしていない場合のメッセージ
     * @throws IllegalArgumentException 条件式が false の場合
     */
    public static void checkArgument(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 指定されたオブジェクトが　NULL でないことを検証する。
     *
     * @param condition 検証値
     * @param message   例外送出時のメッセージ
     * @throws NullPointerException 指定されたオブジェクトが NULL の場合
     */
    public static void notNull(Object condition, String message) {
        if (condition == null) {
            throw new NullPointerException(message);
        }
    }
}
