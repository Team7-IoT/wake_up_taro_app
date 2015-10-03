package com.team7.wakeuptaroapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.team7.activity.wakeuptaroapp.R;

/**
 * �N����̏�����ʂɑ΂���A�N�e�B�r�e�B�B<br />
 * ��{�I�ɂ̓A���[���ꗗ��ʂ֑J�ڂ��邽�߂����̉�ʁB
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
     * Start �{�^�����������ꂽ��ꗗ��ʂ֑J�ڂ���B<br />
     * �ꗗ��ʂ֑J�ڎ��ɁA���킹�Ė{��ʂ�{@code finish()}����B
     *
     * @param view {@link View}
     */
    public void toListOnStart(View view) {
        Intent intent = new Intent(StartActivity.this, AlarmListActivity.class);
        startActivity(intent);

        finish();
    }
}
