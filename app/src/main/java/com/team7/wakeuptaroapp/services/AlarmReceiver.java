package com.team7.wakeuptaroapp.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.team7.wakeuptaroapp.models.AlarmIntent;
import com.team7.wakeuptaroapp.utils.AppLog;

/**
 * アラーム情報を受け取るレシーバークラス。
 *
 * @author Naotake.K
 */
public class AlarmReceiver extends BroadcastReceiver {

    /**
     * 受け取ったインテント情報を基にアクティビティを起動する。
     *
     * @param context {@link Context}
     * @param data    {@link AlarmService} から渡される {@link Intent}
     */
    @Override
    public void onReceive(Context context, Intent data) {

        AlarmIntent param = AlarmIntent.of(data);
        AppLog.d("AlarmReceiver.onReceive: " + param.getAlarmKey());

        AlarmIntent intent = AlarmIntent.forActivity(context);
        intent.setRingtoneUri(param.getRingtoneUriAsString());
        intent.setAlarmKey(param.getAlarmKey());
        intent.setType(param.getAlarmKey());  // Activity 側で Intent を識別するため
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
