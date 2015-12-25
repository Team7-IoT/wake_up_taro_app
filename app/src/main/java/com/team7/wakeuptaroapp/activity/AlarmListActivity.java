package com.team7.wakeuptaroapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.team7.wakeuptaroapp.adapter.ListItemAdapter;
import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.models.Alarm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * アラーム一覧画面に対するアクティビティ。<br />
 * 設定済みのアラームを確認できる画面。
 *
 * @author Naotake.K
 */
public class AlarmListActivity extends AppCompatActivity {

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        lv = (ListView) findViewById(R.id.listView);

        List<Alarm> list = new ArrayList<Alarm>();
        list = this.getListItem();

        // TODO:アラームが登録されていないときの処理
        if(list == null || list.size() == 0) {

        }

        // ListView に取得してきたリストをセットする
        ListItemAdapter adapter = new ListItemAdapter(this);
        adapter.setItemList(list);
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_list, menu);
        return true;
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

    /**
     *保存されているアラーム情報を取得する
     *
     * @return
     */
    private List<Alarm> getListItem() {

        // TODO:マージ後、ダミーでなく保存されているデータから取得するようにする
        //  保存されているデータを取得し、リストに格納
        //TaroSharedPreference preference = Esperandro.getPreferences(TaroSharedPreference.class, getApplicationContext());
        //ArrayList<Alarm> alarms = preference.alarms();
        ArrayList<Alarm> alarms = new ArrayList<>();

        // ダミーを使用
        alarms = creatDammy();

        return alarms;
    }

    // TODO;削除
    // ダミ―データを作成
    private  ArrayList<Alarm> creatDammy() {

        // 時刻
        String time1 = "time1";
        String time2 = "time2";
        String time3 = "time3";
        // 曜日
        String date1 = "date1";
        String date2 = "date1";
        String date3 = "date3";
        // 曜日一覧リストに格納
        Set<String> dateOfWeek = new HashSet<String>();
        dateOfWeek.add(date1);
        dateOfWeek.add(date2);
        dateOfWeek.add(date3);
        // アラーム音の URI
        String uri1 = "uri1";
        String uri2 = "uri2";
        String uri3 = "uri31";
        // Alarmオブジェクト作成
        Alarm am1 = new Alarm(time1, dateOfWeek, uri1);
        Alarm am2 = new Alarm(time2, dateOfWeek, uri2);
        Alarm am3 = new Alarm(time3, dateOfWeek, uri3);

        ArrayList<Alarm> dammyList = new ArrayList<>();
        // アラームリストに格納
        dammyList.add(am1);
        dammyList.add(am2);
        dammyList.add(am3);

        return dammyList;
    }
}
