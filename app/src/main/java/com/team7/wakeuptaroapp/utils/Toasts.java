package com.team7.wakeuptaroapp.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * {@link android.widget.Toast}を使ったメッセージ表示のユーティリティクラス。
 *
 * @author Naotake.K
 */
public class Toasts {

    /**
     * 短時間のメッセージ表示を行う。
     *
     * @param context 表示を行うアクティビティ自身を
     * @param resId   表示するメッセージのリソース ID
     * @param values  メッセージに埋め込むパラメータ
     * @see Toast#makeText(Context, CharSequence, int)
     * @see Toast#show()
     */
    public static void showMessageShort(Context context, int resId, Object... values) {
        String message = composeMessage(context, resId, values);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 長時間のメッセージ表示を行う。
     *
     * @param context 表示を行うアクティビティ自身を
     * @param resId   表示するメッセージのリソース ID
     * @param values  メッセージに埋め込むパラメータ
     * @see Toast#makeText(Context, CharSequence, int)
     * @see Toast#show()
     */
    public static void showMessageLong(Context context, int resId, Object... values) {
        String message = composeMessage(context, resId, values);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private static String composeMessage(Context context, int resId, Object... values) {
        return context.getResources().getString(resId, values);
    }
}
