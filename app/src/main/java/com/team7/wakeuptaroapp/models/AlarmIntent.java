package com.team7.wakeuptaroapp.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.team7.wakeuptaroapp.activities.AlarmNotificationActivity;
import com.team7.wakeuptaroapp.services.AlarmService;
import com.team7.wakeuptaroapp.utils.Preconditions;

/**
 * アラーム起動時に受け渡す {@link Intent} を起こしタロウ用に拡張したクラス。
 *
 * @author Naotake.K
 */
public class AlarmIntent extends Intent {

    /**
     * インテントにアラームを一意に識別する情報を保存するためのキー。
     */
    private static final String ALARM_KEY = "ALARM_KEY";

    /**
     * インテントにアラーム音の URI を保存するためのキー。
     */
    private static final String RINGTONE_URI = "RINGTONE_URI";

    /**
     * {@link com.team7.wakeuptaroapp.services.AlarmReceiver} を起動させるためのアクション名。<br />
     * ここで指定した値は AndroidManifest.xml の設定値と同一でなければいけない。
     */
    private static final String ACTION = "TIMER_FINISHED";

    public AlarmIntent() {
        super();
    }

    public AlarmIntent(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    /**
     * サービス呼び出し用の {@link AlarmIntent} を生成する。
     *
     * @param packageContext {@link Context}
     * @param alarm          アラーム情報
     * @return {@link AlarmIntent}
     * @see AlarmService
     */
    public static AlarmIntent forService(@NonNull Context packageContext, @NonNull Alarm alarm) {
        AlarmIntent intent = new AlarmIntent(packageContext, AlarmService.class);
        intent.setRingtoneUri(alarm.getRingtoneUri());
        return intent;
    }

    /**
     * アクティビティ呼び出し用の {@link AlarmIntent} を生成する。
     *
     * @param packageContext {@link Context}
     * @return {@link AlarmIntent}
     * @see AlarmNotificationActivity
     */
    public static AlarmIntent forActivity(@NonNull Context packageContext) {
        AlarmIntent intent = new AlarmIntent(packageContext, AlarmNotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * 必要な情報を取り出して新たな {@link AlarmIntent} を生成する。
     *
     * @param data 元となる {@link Intent}
     * @return {@link AlarmIntent}
     */
    public static AlarmIntent of(@NonNull Intent data) {
        AlarmIntent intent = new AlarmIntent();
        intent.setRingtoneUri(data.getStringExtra(RINGTONE_URI));
        intent.setAlarmKey(data.getStringExtra(ALARM_KEY));
        return intent;
    }

    /**
     * アラーム情報を一意に識別する情報を設定する。
     *
     * @param key 識別子
     */
    public void setAlarmKey(@NonNull Long key) {
        Preconditions.notNull(key, "Alarm key required!!");
        this.setAlarmKey(String.valueOf(key));
    }

    /**
     * @param key 文字列識別子
     * @see #setAlarmKey(Long)
     */
    public void setAlarmKey(@NonNull String key) {
        Preconditions.notNull(key, "Alarm key required!!");
        this.putExtra(ALARM_KEY, key);
    }

    /**
     * アラーム情報を一意に識別する情報を取得する。
     *
     * @return 文字列識別子
     */
    public String getAlarmKey() {
        return this.getStringExtra(ALARM_KEY);
    }

    /**
     * アラーム音の URI を設定する。
     *
     * @param uri アラーム音の URI を表す文字列
     */
    public void setRingtoneUri(@NonNull String uri) {
        Preconditions.notNull(uri, "Alarm ringtone required!!");
        this.putExtra(RINGTONE_URI, uri);
    }

    /**
     * アラーム音の URI を表す文字列を取得する。
     *
     * @return URI の文字列
     */
    public String getRingtoneUriAsString() {
        return this.getStringExtra(RINGTONE_URI);
    }

    /**
     * アラーム音の URI を取得する。
     *
     * @return アラーム音の URI
     */
    public Uri getRingtoneUri() {
        String uriStr = getRingtoneUriAsString();
        return (uriStr == null ? null : Uri.parse(uriStr));
    }

    /**
     * アクションにアラームを一意に識別する情報を設定する。<br />
     * 主にアクティビティ呼び出し前に使用されることを想定。
     *
     * @param key アラームを一意に識別する情報
     */
    public void setActionAsUniqueKey(@NonNull Long key) {
        Preconditions.notNull(key, "Alarm key required!!");
        this.setAction(String.valueOf(key));
    }

    /**
     * アクションに {@link com.team7.wakeuptaroapp.services.AlarmReceiver} 起動用の文字列を設定する。
     */
    public void setActionAsTimerFinished() {
        this.setAction(ACTION);
    }
}
