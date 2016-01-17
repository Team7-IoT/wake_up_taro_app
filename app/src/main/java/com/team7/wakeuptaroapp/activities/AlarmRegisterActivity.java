package com.team7.wakeuptaroapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.fragments.AlarmFragment;
import com.team7.wakeuptaroapp.models.Alarm;
import com.team7.wakeuptaroapp.utils.AppLog;
import com.team7.wakeuptaroapp.utils.TaroAlarmManager;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;
import com.team7.wakeuptaroapp.utils.Toasts;

import java.util.List;
import java.util.Set;

import de.devland.esperandro.Esperandro;

/**
 * アラーム登録画面に対するアクティビティ。
 *
 * @author Naotake.K
 */
public class AlarmRegisterActivity extends AppCompatActivity {

    // SharedPreference
    private TaroSharedPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 登録項目をバインド
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AlarmFragment())
                .commit();

        // 戻る
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SharedPreference
        preference = Esperandro.getPreferences(TaroSharedPreference.class, getApplicationContext());
        preference.alarmTime(null);
        preference.alarmDayOfWeeks(null);
        preference.alarmRingtone(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // 登録ボタン押下
        if (id == R.id.action_store_alarm) {
            AppLog.d("Tap to store on alarm.");

            // アラームの保存
            Alarm newAlarm = storeAlarm();
            registerAlarm(newAlarm);

            // アラーム一覧画面の Activity を呼び出す。
            Intent intent = new Intent(AlarmRegisterActivity.this, AlarmListActivity.class);
            startActivity(intent);

            // メッセージ
            Toasts.showMessageLong(this, R.string.message_store_alarm);

            finish();
            return true;
        }

        // 戻るボタン押下
        if (id == android.R.id.home) {
            AppLog.d("Tap to back on alarm.");

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 入力内容を基にアラーム情報を SharedPreference に保存する。
     */
    private Alarm storeAlarm() {

        String time = preference.alarmTime();
        Set<String> dayOfWeeks = preference.alarmDayOfWeeks();
        String ringtoneUri = preference.alarmRingtone();

        AppLog.d("Alarm Time: " + time);
        AppLog.d("Alarm Week: " + dayOfWeeks);
        AppLog.d("Alarm Ring: " + ringtoneUri);

        Alarm newAlarm = new Alarm(time, dayOfWeeks, ringtoneUri);
        newAlarm.setValid(true);

        List<Alarm> alarms = preference.alarms();
        alarms.add(newAlarm);
        preference.alarms(alarms);

        return newAlarm;
    }

    /**
     * 入力内容を基にアラーム情報を登録する。
     */
    private void registerAlarm(Alarm alarm) {
        TaroAlarmManager alarmManager = new TaroAlarmManager(getApplicationContext());
        alarmManager.register(alarm);
    }
}
