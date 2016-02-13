package com.team7.wakeuptaroapp.utils;

import com.team7.wakeuptaroapp.models.Alarm;

import java.util.List;
import java.util.Set;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;

/**
 * {@link android.content.SharedPreferences}の操作を簡略化するラッパークラス。
 *
 * @author Naotake.K
 * @see <a href="http://dkunzler.github.io/esperandro/">Esperandro</a>
 */
@SharedPreferences
public interface TaroSharedPreference extends SharedPreferenceActions {

    public static String ALARM_TIME = "alarmTime";
    public static String ALARM_DAY_OF_WEEKS = "alarmDayOfWeeks";
    public static String ALARM_RINGTONE = "alarmRingtone";

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

    /**
     * 親機となるデバイスのアドレスを取得する。<br />
     * 親機が未設定の場合、空文字を返す。
     *
     * @return 親機のアドレス
     */
    @Default(ofString = "")
    String deviceAddress();

    /**
     * 親機となるデバイスのアドレスを保存する。
     *
     * @param deviceAddress 親機のアドレス
     */
    void deviceAddress(String deviceAddress);

    /**
     * アプリ共通で使用するアラーム音量を取得する。
     *
     * @return アラーム音量
     */
    @Default(ofInt = 0)
    int alarmVolume();

    /**
     * アプリ共通で使用するアラーム音量を保存する。
     *
     * @param volume アラーム音量
     */
    void alarmVolume(int volume);

    /**
     * アラーム登録・更新時に設定した時間を取得する。
     *
     * @return アラーム時間 (HH:MM 形式)
     */
    @Default(ofString = "")
    String alarmTime();

    /**
     * アラーム登録・更新用に時間を保存する。
     *
     * @param alarmTime アラーム時間 (HH:MM 形式)
     */
    void alarmTime(String alarmTime);

    /**
     * アラーム登録・更新時に設定した曜日を取得する。
     *
     * @return 曜日一覧
     */
    Set<String> alarmDayOfWeeks();

    /**
     * アラーム登録・更新用に曜日を保存する。
     *
     * @param alarmDayOfWeeks 曜日一覧
     */
    void alarmDayOfWeeks(Set<String> alarmDayOfWeeks);

    /**
     * アラーム登録・更新時に設定したアラーム音の名称を取得する。
     *
     * @return アラーム音の名称
     */
    @Default(ofString = "")
    String alarmRingtone();

    /**
     * アラーム登録・更新用にアラーム音の名称を保存する。
     *
     * @param alarmRingtone アラーム音の名称
     */
    void alarmRingtone(String alarmRingtone);

    /**
     * 登録済みのアラーム一覧を取得する。
     *
     * @return アラーム一覧
     */
    List<Alarm> alarms();

    /**
     * アラームが有効かどうかの情報を保存する。
     *
     * @return valid 有効な場合は true
     */
    Boolean valid();

    /**
     * アラームが有効かどうかの情報を取得する。
     *
     * @param valid 有効な場合は true
     */
    void valid(Boolean valid);

    /**
     * 登録済みのアラーム一覧を更新する。
     *
     * @param alarms アラーム一覧
     */
    void alarms(List<Alarm> alarms);
}
