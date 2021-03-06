package com.team7.wakeuptaroapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.exceptions.AlarmConstraintViolationsException;
import com.team7.wakeuptaroapp.fragments.AlarmFragment;
import com.team7.wakeuptaroapp.models.Alarm;
import com.team7.wakeuptaroapp.utils.AppLog;
import com.team7.wakeuptaroapp.utils.Preconditions;
import com.team7.wakeuptaroapp.utils.TaroAlarmManager;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;
import com.team7.wakeuptaroapp.utils.Toasts;
import com.team7.wakeuptaroapp.views.dialogs.AlertDialogBuilder;

import java.util.List;
import java.util.Set;

import de.devland.esperandro.Esperandro;

/**
 * アラーム更新画面に対するアクティビティ。
 *
 * @author Naotake.K
 */
public class AlarmUpdateActivity extends AppCompatActivity {

    // SharedPreference
    private TaroSharedPreference preference;

    // 更新対象のアラームを識別するキー
    private Long targetAlarmKey;

    // 更新対象のアラーム情報
    private Alarm targetAlarm;

    // アラーム更新画面へ必要な情報を渡すためのキー
    public static final String ALARM_KEY = "UPDATE_TARGET_KEY";

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

        // 更新対象を抽出
        targetAlarmKey = getIntent().getLongExtra(ALARM_KEY, 0);
        Preconditions.checkArgument((targetAlarmKey != 0), "Illegal Update Alarm Key");

        targetAlarm = selectAlarm(preference.alarms());
        Preconditions.notNull(targetAlarm, "Not Exists Update Alarm(onCreate) " + targetAlarmKey);

        preference.alarmTime(targetAlarm.getTime());
        preference.alarmDayOfWeeks(targetAlarm.getDayOfWeeks());
        preference.alarmRingtone(targetAlarm.getRingtoneUri());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // 更新ボタン押下
        if (id == R.id.action_update_alarm) {
            AppLog.d("Tap to update on alarm.");

            // アラームの更新
            TaroAlarmManager alarmManager = new TaroAlarmManager(getApplicationContext());
            alarmManager.cancel(targetAlarm);
            try {
                updateAlarm();
            } catch (AlarmConstraintViolationsException e) {
                new AlertDialogBuilder.ValidationFailureDialog(this).cause(e.getCauseMessageId()).show();
                return true;
            }
            if (targetAlarm.isValid()) {
                alarmManager.register(targetAlarm);
            }

            // メッセージ
            Toasts.showMessageLong(this, R.string.message_update_alarm);

            finish();
            return true;
        }

        // 戻るボタン押下
        if (id == android.R.id.home) {
            AppLog.d("Tap to back on menu_alarm_update.");

            if (isChanged()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_dialog_alarm_validation_failure)
                        .setMessage(R.string.message_alarm_changed_ignore)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create().show();

                return true;
            }

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 入力内容を基にアラーム情報を SharedPreference に保存する。
     */
    private void updateAlarm() throws AlarmConstraintViolationsException {

        String time = preference.alarmTime();
        Set<String> dayOfWeeks = preference.alarmDayOfWeeks();
        String ringtoneUri = preference.alarmRingtone();

        AppLog.d("Alarm Time: " + time);
        AppLog.d("Alarm Week: " + dayOfWeeks);
        AppLog.d("Alarm Ring: " + ringtoneUri);

        List<Alarm> alarms = preference.alarms();
        Preconditions.notNull(targetAlarm, "Not Exists Update Alarm " + targetAlarmKey);

        targetAlarm.setTime(time);
        targetAlarm.setDayOfWeeks(dayOfWeeks);
        targetAlarm.setRingtoneUri(ringtoneUri);
        targetAlarm.setValid(true);  // アラーム更新のタイミングで必ず有効化
        targetAlarm.validate();

        alarms.remove(targetAlarm);
        alarms.add(targetAlarm);

        preference.alarms(alarms);

        // アラーム一覧画面の Activity を呼び出す。
        Intent intent = new Intent(AlarmUpdateActivity.this, AlarmListActivity.class);
        startActivity(intent);
    }

    /**
     * 更新前のアラーム情報から変更があるかどうかを判定する。
     *
     * @return いずれかの項目で変更があった場合は true
     */
    private boolean isChanged() {
        String newTime = preference.alarmTime();
        Set<String> newDayOfWeeks = preference.alarmDayOfWeeks();
        String newRingtoneUri = preference.alarmRingtone();

        return targetAlarm.isChanged(newTime, newDayOfWeeks, newRingtoneUri);
    }

    /**
     * アラーム一覧からキーが一致するアラームを取り出す。
     *
     * @param alarms アラーム一覧
     * @return キーが一致したアラーム情報
     */
    private Alarm selectAlarm(List<Alarm> alarms) {
        Preconditions.notNull(alarms, "Not Exists Alarms " + targetAlarmKey);

        for (Alarm alarm : alarms) {
            if (alarm.equalsKey(targetAlarmKey)) {
                return alarm;
            }
        }
        return null;
    }

    /**
     * 戻るボタンをホームボタン押下と同じ振る舞いに上書きする。
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        return true;
    }
}
