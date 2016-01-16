package com.team7.wakeuptaroapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.fragments.SettingFragment;
import com.team7.wakeuptaroapp.utils.AppLog;


/**
 * 起こしタロウに関する各種設定を行うためのアクティビティ。
 *
 * @author Naotake.K
 */
public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 登録項目をバインド
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingFragment())
                .commit();

        // 戻る
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // 戻るボタン押下
        if (id == android.R.id.home) {
            AppLog.d("Tap to back on setting.");
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
