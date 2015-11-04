package com.team7.wakeuptaroapp.util;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.SharedPreferenceMode;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;

/**
 * {@link android.content.SharedPreferences}の操作を簡略化するラッパークラス。
 *
 * @author Naotake.K
 * @see <a href="http://dkunzler.github.io/esperandro/">Esperandro</a>
 */
@SharedPreferences(name = "wake_up_taro", mode = SharedPreferenceMode.PRIVATE)
public interface TaroSharedPreference extends SharedPreferenceActions {

    /**
     * 親機となるデバイス名を取得する。<br />
     * 親機が未設定の場合、空文字を返す。
     *
     * @return デバイス名
     */
    @Default(ofString = "")
    String deviceName();

    /**
     * 親機となるデバイス名を保存する。
     *
     * @param deviceName デバイス名
     */
    void deviceName(String deviceName);
}
