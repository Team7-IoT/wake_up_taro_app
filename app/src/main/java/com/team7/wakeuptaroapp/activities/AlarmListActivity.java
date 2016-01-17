package com.team7.wakeuptaroapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.adapters.ListItemAdapter;
import com.team7.wakeuptaroapp.models.Alarm;
import com.team7.wakeuptaroapp.utils.AppLog;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;
import com.team7.wakeuptaroapp.utils.Toasts;

import java.util.List;

import de.devland.esperandro.Esperandro;

/**
 * アラーム一覧画面に対するアクティビティ。<br />
 * 設定済みのアラームを確認できる画面。
 *
 * @author Shiori.K
 * @author Naotake.K
 */
public class AlarmListActivity extends AppCompatActivity {

    private ListView lv;
    private List<Alarm> list;
    private ListItemAdapter adapter;
    private TaroSharedPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        lv = (ListView) findViewById(R.id.alarm_list);

        list = this.getListItem();

        // アラームデータをビューに渡す
        adapter = new ListItemAdapter(this);
        adapter.setItemList(list);
        lv.setAdapter(adapter);

        preference = Esperandro.getPreferences(TaroSharedPreference.class, getApplicationContext());

        // ListView クリック時アラーム更新画面へ遷移させる
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppLog.d("onClickEditAlarm");

                ListView listView = (ListView) parent;

                Intent intent = new Intent(AlarmListActivity.this, AlarmUpdateActivity.class);
                intent.putExtra(AlarmUpdateActivity.ALARM_KEY, preference.alarms().get(position).getAlarmKey());
                startActivity(intent);
            }
        });

        //ロングタップ時に表示されるコンテキストを登録する。
        registerForContextMenu(lv);
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
                adapter.notifyDataSetChanged();

                // 端末から対象のアラーム情報を削除する。
                preference = Esperandro.getPreferences(TaroSharedPreference.class, getApplicationContext());
                preference.alarms(list);

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

            // 設定 Activity 呼び出し
            Intent intent = new Intent(AlarmListActivity.this, SettingActivity.class);
            startActivity(intent);

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 端末に保存されているアラーム情報を取得する。
     *
     * @return alarms アラーム一覧
     */
    private List<Alarm> getListItem() {

        preference = Esperandro.getPreferences(TaroSharedPreference.class, getApplicationContext());
        List<Alarm> alarms = preference.alarms();

        return alarms;
    }

}
