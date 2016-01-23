package com.team7.wakeuptaroapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.adapters.ListItemAdapter;
import com.team7.wakeuptaroapp.models.Alarm;
import com.team7.wakeuptaroapp.utils.AppLog;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;
import com.team7.wakeuptaroapp.utils.Toasts;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.devland.esperandro.Esperandro;

/**
 * アラーム一覧画面に対するアクティビティ。<br />
 * 設定済みのアラームを確認できる画面。
 *
 * @author Shiori.K
 * @author Naotake.K
 */
public class AlarmListActivity extends AppCompatActivity {

    @Bind(R.id.alarm_list)
    ListView lv;
    private List<Alarm> list;
    private ListItemAdapter adapter;
    private TaroSharedPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        preference = Esperandro.getPreferences(TaroSharedPreference.class, getApplicationContext());
        ButterKnife.bind(this);

        // アラームデータをビューに渡す
        list = preference.alarms();

        adapter = new ListItemAdapter(this);
        adapter.setItemList(list);
        lv.setAdapter(adapter);

        // ListView クリック時アラーム更新画面へ遷移させる
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppLog.d("onClickEditAlarm");

                Intent intent = new Intent(AlarmListActivity.this, AlarmUpdateActivity.class);
                intent.putExtra(AlarmUpdateActivity.ALARM_KEY, list.get(position).getAlarmKey());
                startActivity(intent);
            }
        });

        //ロングタップ時に表示されるコンテキストを登録する。
        registerForContextMenu(lv);

        ViewTarget target = new ViewTarget(R.id.new_alarm, this);
        new ShowcaseView.Builder(this)
                .withHoloShowcase()
                .setTarget(target)
                .setStyle(R.style.CustomShowcaseTheme)
                .setContentTitle("はじめに")
                .setContentText("タロウのエサについて設定を行ってください。")
                .hideOnTouchOutside()
                .build().hideButton();

        // TODO どうやら ActionBar の item に対して設定できないみたい...
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyListChanged();
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
     * 削除時のコンテキストメニュー作成。
     *
     * @param menu
     * @param view
     * @param info
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu, view, info);
        getMenuInflater().inflate(R.menu.alarm_delete, menu);
    }

    /**
     * 削除のコンテキストメニューを選択した時の処理。
     *
     * @param item
     * @return 削除されたかどうか
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.alarm_delete:
                Alarm alarm = list.get(info.position);
                list.remove(alarm);

                // 端末から対象のアラーム情報を削除する。
                preference.alarms(list);

                notifyListChanged();
                Toasts.showMessageLong(this, R.string.message_delete_alarm);

                break;
            default:
                break;
        }
        return false;
    }

    /**
     * メニューバーの設定アイコン選択時の処理。
     *
     * @param item
     * @return 設定画面が呼び出されたかどうか
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(AlarmListActivity.this, SettingActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
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

    /**
     * アラーム一覧をソートしなおし、アダプタに変更を通知する。
     */
    private void notifyListChanged() {
        if (list != null) {
            Collections.sort(list);
        }
        adapter.notifyDataSetChanged();
    }
}
