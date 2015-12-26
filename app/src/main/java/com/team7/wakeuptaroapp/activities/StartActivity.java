package com.team7.wakeuptaroapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.team7.wakeuptaroapp.BuildConfig;
import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.models.Alarm;
import com.team7.wakeuptaroapp.utils.AppLog;
import com.team7.wakeuptaroapp.utils.TaroAlarmManager;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;
import com.team7.wakeuptaroapp.utils.Toasts;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.devland.esperandro.Esperandro;

/**
 * 起動後の初期画面に対するアクティビティ。<br />
 * 基本的にはアラーム一覧画面へ遷移するためだけの画面。
 *
 * @author Naotake.K
 */
public class StartActivity extends Activity {

    @Bind(R.id.app_version)
    TextView appVersionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // ButterKnife DI
        ButterKnife.bind(this);

        appVersionView.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
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
     * Start ボタン押下時にアラーム一覧画面へ遷移する。<br />
     * 遷移するタイミングでこの画面は {@code finish()} される。
     *
     * @param view {@link View}
     */
    public void toListOnStart(View view) {
        Intent intent = new Intent(StartActivity.this, AlarmListActivity.class);
        startActivity(intent);

        finish();
    }

    /**
     * 開発時用にアラーム情報の一括削除を行う。
     *
     * @param view {@link View}
     */
    public void deleteAllAlarm(View view) {
        AppLog.d("Delete All Alarm");

        TaroSharedPreference preference = Esperandro.getPreferences(TaroSharedPreference.class, getApplicationContext());
        TaroAlarmManager alarmManager = new TaroAlarmManager(getApplicationContext());

        if (preference.alarms() == null) {
            Toasts.showMessageShort(this, R.string.message_alarm_delete_none);
            return;
        }

        for (Alarm alarm : preference.alarms()) {
            alarmManager.cancel(alarm);
        }
        preference.alarms(new ArrayList<Alarm>());

        Toasts.showMessageShort(this, R.string.message_alarm_delete_all);
    }
}
