package com.team7.wakeuptaroapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.models.AlarmIntent;

/**
 * アラームが鳴り始めた時に実行されるアクティビティ。
 *
 * @author Naotake.K
 */
public class AlarmNotificationActivity extends Activity {

    private Ringtone ringtone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notification);

        AlarmIntent intent = AlarmIntent.of(getIntent());
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), intent.getRingtoneUri());

        // スクリーンロックを解除する
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onStart() {
        super.onStart();
        ringtone.play();

        // TODO ここで親機との疎通を実施
        // TODO もし親機との疎通に失敗した場合、緊急停止用として停止ボタンを活性化させる
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Stop ボタン押下時にアラーム一覧画面へ遷移する。
     *
     * @param view {@link View}
     */
    public void stopAlarm(View view) {
        ringtone.stop();

        Intent intent = new Intent(this, AlarmListActivity.class);
        startActivity(intent);

        finish();
    }
}
