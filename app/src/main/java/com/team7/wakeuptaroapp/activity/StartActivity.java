package com.team7.wakeuptaroapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.team7.wakeuptaroapp.R;

/**
 * 起動後の初期画面に対するアクティビティ。<br />
 * 基本的にはアラーム一覧画面へ遷移するためだけの画面。
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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
}
