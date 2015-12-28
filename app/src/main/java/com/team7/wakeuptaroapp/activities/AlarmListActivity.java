package com.team7.wakeuptaroapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.utils.AppLog;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;

import de.devland.esperandro.Esperandro;

/**
 * アラーム一覧画面に対するアクティビティ。<br />
 * 設定済みのアラームを確認できる画面。
 *
 * @author Naotake.K
 */
public class AlarmListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_list, menu);
        return true;
    }

    /**
     * アラーム追加ボタンが押下されたときの振る舞いを定義する。
     */
    public void onClickNewAlarm(View view) {
        AppLog.d("onClickNewAlarm");

        // アラーム登録 Activity 呼び出し
        Intent intent = new Intent(AlarmListActivity.this, AlarmRegisterActivity.class);
        startActivity(intent);
    }

    /**
     * アラーム編集ボタンが押下されたときの振る舞いを定義する。
     */
    public void onClickEditAlarm(View view) {
        AppLog.d("onClickEditAlarm");

        TaroSharedPreference preference =
                Esperandro.getPreferences(TaroSharedPreference.class, getApplicationContext());

        // FIXME 仮実装
        Intent intent = new Intent(AlarmListActivity.this, AlarmUpdateActivity.class);
        intent.putExtra(AlarmUpdateActivity.ALARM_KEY, preference.alarms().get(0).getAlarmKey());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            // 設定 Activity 呼び出し
            Intent intent = new Intent(AlarmListActivity.this, SettingActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
