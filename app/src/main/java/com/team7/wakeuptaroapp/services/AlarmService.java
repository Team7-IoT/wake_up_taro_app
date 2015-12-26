package com.team7.wakeuptaroapp.services;

import android.app.IntentService;
import android.content.Intent;

import com.team7.wakeuptaroapp.models.AlarmIntent;
import com.team7.wakeuptaroapp.utils.AppLog;

/**
 * アラーム情報を受け取るサービスクラス。
 *
 * @author Naotake.K
 */
public class AlarmService extends IntentService {

    public AlarmService() {
        super(AlarmService.class.getSimpleName());
    }

    /**
     * 受け取ったインテント情報を基にレシーバーを起動する。
     *
     * @param data {@link android.app.PendingIntent} から渡される {@link Intent}
     */
    @Override
    protected void onHandleIntent(Intent data) {

        AlarmIntent intent = AlarmIntent.of(data);
        intent.setActionAsTimerFinished();
        sendBroadcast(intent);

        AppLog.d("AlarmService.onHandleIntent: " + intent.getAlarmKey());
    }
}
