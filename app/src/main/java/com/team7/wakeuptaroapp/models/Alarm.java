package com.team7.wakeuptaroapp.models;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.exceptions.AlarmConstraintViolationsException;
import com.team7.wakeuptaroapp.utils.Alarms;

import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * アラームの情報を保持するモデル。
 *
 * @author Naotake.K
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Alarm implements Comparable<Alarm>, Serializable {

    // アラーム時間 (HH:MM)
    private String time;

    // アラームの曜日
    private Set<String> dayOfWeeks;

    // アラーム音の URI
    private String ringtoneUri;

    // 現在有効なアラームかどうか
    private boolean valid;

    // アラーム登録日時
    private Long registeredDateTime;

    /**
     * 空のアラーム情報を生成する。<br />
     * 主に Esperandro がデシリアライズするときに使われることを想定。
     */
    public Alarm() {
        this("", new HashSet<String>(), "");
    }

    /**
     * 登録内容を基にアラーム情報を生成する。
     *
     * @param time        アラーム時刻 (HH:MM 形式)
     * @param dayOfWeeks  曜日
     * @param ringtoneUri アラーム音の URI 文字列
     */
    public Alarm(String time, Set<String> dayOfWeeks, String ringtoneUri) {
        this.time = time;
        this.dayOfWeeks = dayOfWeeks;
        this.ringtoneUri = ringtoneUri;
        this.valid = false;
        this.registeredDateTime = LocalDateTime.now().toDateTime().getMillis();
    }

    /**
     * アラーム時刻を取得する。
     *
     * @return アラーム時刻 (HH:MM 形式)
     */
    public String getTime() {
        return time;
    }

    /**
     * アラーム時刻から時間 (HH) 部分だけを取得する。
     *
     * @return 時間
     */
    @JsonIgnore
    public int getTimeHour() {
        return Alarms.selectHour(time);
    }

    /**
     * アラーム時刻から分 (MM) 部分だけを取得する。
     *
     * @return 分
     */
    @JsonIgnore
    public int getTimeMinute() {
        return Alarms.selectMinute(time);
    }

    /**
     * アラーム時刻を設定する。
     *
     * @param time アラーム時刻 (HH:MM 形式)
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 曜日一覧を取得する。<br />
     * 未設定の場合、空の一覧 ({@link Collections#EMPTY_SET}) を返す。
     *
     * @return アラームの曜日一覧
     */
    public Set<String> getDayOfWeeks() {
        return (dayOfWeeks == null ? Collections.EMPTY_SET : dayOfWeeks);
    }

    /**
     * 曜日一覧を設定する。
     *
     * @param dayOfWeeks アラームの曜日一覧
     */
    public void setDayOfWeeks(Set<String> dayOfWeeks) {
        this.dayOfWeeks = dayOfWeeks;
    }

    /**
     * アラーム音の URI (文字列) を取得する。
     *
     * @return アラーム音の URI (文字列)
     */
    public String getRingtoneUri() {
        return ringtoneUri;
    }

    /**
     * アラーム音の URI (文字列) を取得する。
     *
     * @param ringtoneUri アラーム音の URI (文字列)
     */
    public void setRingtoneUri(String ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

    /**
     * このアラームが有効かどうかを取得する。
     *
     * @return 有効な場合は true
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * このアラーム情報の有効/無効を設定する。
     *
     * @param valid 有効な場合は true
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * @deprecated この Setter は Esperandro 向けに用意しているため、通常のアプリケーションでの使用不可
     */
    public void setRegisteredDateTime(Long registeredDateTime) {
        this.registeredDateTime = registeredDateTime;
    }

    /**
     * このアラーム情報の登録日時を取得する。
     *
     * @return アラームの登録日時
     */
    public Long getRegisteredDateTime() {
        return registeredDateTime;
    }

    @JsonIgnore
    public void validate() throws AlarmConstraintViolationsException {
        if (dayOfWeeks.isEmpty()) {
            throw new AlarmConstraintViolationsException(R.string.message_day_of_week_not_selected);
        }
    }

    /**
     * このアラーム情報を一意に識別する値を取得する。
     *
     * @return キー情報
     */
    @JsonIgnore
    public Long getAlarmKey() {
        return registeredDateTime;
    }

    /**
     * このアラームが指定されたキー情報と等しいかどうかを判定する。
     *
     * @param key 判定対象となるキー情報
     * @return 等しい場合は true
     */
    @JsonIgnore
    public boolean equalsKey(Long key) {
        return (registeredDateTime.equals(key));
    }

    @Override
    public int hashCode() {
        return registeredDateTime.hashCode();
    }

    /**
     * アラーム登録日時を基に同一アラームの比較を行う。
     *
     * @param other 比較対象
     * @return 等しい場合は true
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Alarm)) {
            throw new IllegalArgumentException("Illegal type: " + other);
        }
        return (this.getAlarmKey().equals(Alarm.class.cast(other).getAlarmKey()));
    }

    /**
     * アラーム登録日時を基に並び順の比較を行う。
     *
     * @param other 比較対象
     * @return {@link Comparable#compareTo(Object)}
     */
    @Override
    public int compareTo(@NonNull Alarm other) {
        return this.getAlarmKey().compareTo(other.getAlarmKey());
    }
}
