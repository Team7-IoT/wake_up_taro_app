package com.team7.wakeuptaroapp.util;

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
     * 指定した条件式が {@code true} であることを検証する。
     *
     * @param condition 条件式
     * @param message 条件を満たしていない場合のメッセージ
     * @throws IllegalAccessException 条件式が {@code false} の場合
     */
    public static void checkArgument(boolean condition, String message) {
        if (!condition) {
            throw new IllegalAccessError(message);
        }
    }
}
