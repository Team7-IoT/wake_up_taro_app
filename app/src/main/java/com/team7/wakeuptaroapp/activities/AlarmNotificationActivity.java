package com.team7.wakeuptaroapp.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.ble.RaspberryPi;
import com.team7.wakeuptaroapp.ble.RpiGattCallback;
import com.team7.wakeuptaroapp.ble.RpiLeScanCallback;
import com.team7.wakeuptaroapp.models.AlarmIntent;
import com.team7.wakeuptaroapp.utils.AppLog;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;
import com.team7.wakeuptaroapp.utils.Toasts;

import java.util.UUID;

import de.devland.esperandro.Esperandro;

import static com.team7.wakeuptaroapp.BuildConfig.NOTIFICATION_CHARACTERISTIC_UUID;
import static com.team7.wakeuptaroapp.BuildConfig.NOTIFICATION_SERVICE_UUID;

/**
 * アラームが鳴り始めた時に実行されるアクティビティ。
 *
 * @author Naotake.K
 */
public class AlarmNotificationActivity extends Activity {

    // 親機への接続検証待ち時間 (5秒)
    private static final long WAIT_CONNECT_PERIOD = 5000;

    // 端末の Bluetooth が有効になるまでの待ち時間 (3秒)
    private static final long WAIT_BLUETOOTH_ENABLED = 3000;

    // BLE 周りのコンポーネント群
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private BluetoothGatt bluetoothGatt;

    // SharedPreference
    private TaroSharedPreference preference;

    // アラーム登録時に指定したアラーム音
    private Ringtone ringtone;

    // アラーム起動時の Bluetooth 状態
    private boolean bluetoothDisabled;

    // アラーム起動時に親機との接続に成功したかどうか
    private boolean scanSuccessful;

    /**
     * 親機を BLE でスキャンする際のコールバック。
     */
    private RpiLeScanCallback scanCallback = new RpiLeScanCallback() {
        /**
         * スキャンで見つかったデバイスの情報を一覧に保存する。
         *
         * @param device {@link BluetoothDevice}
         */
        @Override
        protected void doLeScan(BluetoothDevice device) {
            AppLog.d("device.getName(): " + device.getName());
            AppLog.d("preference.deviceName(): " + preference.deviceName());
            if (TextUtils.equals(device.getName(), preference.deviceName())) {
                AppLog.d("Found device: " + preference.deviceName());
                bluetoothGatt = device.connectGatt(getApplicationContext(), false, gattCallback);
            }
        }
    };

    /**
     * 親機との GATT 通信時に使用するコールバック。
     */
    private RpiGattCallback gattCallback = new RpiGattCallback() {
        /**
         * GATT 通信に成功後、{@link #onServicesDiscovered(BluetoothGatt, int)} を実行する。
         *
         * @param gatt {@link BluetoothGatt}
         */
        @Override
        protected void doConnectionStateIfConnected(BluetoothGatt gatt) {
            bluetoothGatt.discoverServices();
        }

        /**
         * 親機を経由したセンサーからの通知を受け取るための設定を行う。
         *
         * @param gatt {@link BluetoothGatt}
         */
        @Override
        protected void doServicesDiscoveredIfGattSuccess(BluetoothGatt gatt) {
            if (isConnected()) {
                // Notification を要求
                BluetoothGattCharacteristic c = findCharacteristic(
                        NOTIFICATION_SERVICE_UUID, NOTIFICATION_CHARACTERISTIC_UUID);
                boolean result = bluetoothGatt.setCharacteristicNotification(c, true);
                AppLog.d("setCharacteristicNotification result: " + result);

                // Characteristic の Notification 有効化
                BluetoothGattDescriptor descriptor = c.getDescriptor(RaspberryPi.CLIENT_CHARACTERISTIC_UUID);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }

        /**
         * 親機を経由したセンサーからの通知を受け取った場合に、アラーム停止を行う。
         *
         * @param gatt {@link BluetoothGatt}
         * @param characteristic {@link BluetoothGattCharacteristic}
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            AppLog.d("onCharacteristicChanged UUID: " + characteristic.getUuid().toString());

            if (TextUtils.equals(characteristic.getUuid().toString(), NOTIFICATION_CHARACTERISTIC_UUID)) {
                AppLog.d("Notification characteristic: " + characteristic.getValue()[0]);

                final Activity activity = AlarmNotificationActivity.this;
                if (characteristic.getValue()[0] == 1) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopAlarm();
                            Toasts.showMessageLong(activity, R.string.message_alarm_stop_success);
                        }
                    });
                }
            }
        }
    };

    /**
     * 一定時間スキャン後に呼び出す後処理。
     */
    private Runnable scanFinalizer = new Runnable() {
        @Override
        public void run() {

            if (scanSuccessful) {
                AppLog.d("AlarmNotificationActivity Scan successful");
                return;
            }

            // TODO 接続失敗時に、接続をリトライするか？
        }
    };

    /**
     * Bluetooth が有効になるのを待った後に再度スキャンを行うコールバック。
     */
    private Runnable retryScanner = new Runnable() {
        @Override
        public void run() {
            if (bluetoothAdapter.isEnabled()) {
                startScan();
            } else {
                AppLog.d("Still Bluetooth disabled on Handler.");
                // TODO リトライ後でもダメだった場合どうする？
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notification);

        // SharedPreference
        preference = Esperandro.getPreferences(TaroSharedPreference.class, getApplicationContext());

        // BLE Components
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        this.scanCallback.setActivity(this);

        // AlarmReceiver 経由で受け取ったアラーム音
        AlarmIntent intent = AlarmIntent.of(getIntent());
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), intent.getRingtoneUri());

        bluetoothGatt = null;
        handler = new Handler(getApplicationContext().getMainLooper());

        // スクリーンロックを解除する
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 一時的に Bluetooth を有効にする
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothDisabled = true;
            bluetoothAdapter.enable();
        }

        // 接続検証済み親機の存在判定
        if (TextUtils.isEmpty(preference.deviceName())) {
            // TODO アラーム起動時に、接続検証済みの親機が居ない場合
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ringtone.play();

        // TODO もし親機との疎通に失敗した場合、緊急停止用として停止ボタンを活性化させる？

        // スキャン開始
        startScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_notification, menu);
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

    private void startScan() {
        if (!bluetoothAdapter.isEnabled()) {
            AppLog.d("Still Bluetooth disabled.");

            // 一定時間後に再スキャン
            handler.postDelayed(retryScanner, WAIT_BLUETOOTH_ENABLED);

            return;
        }
        // 5秒後に接続が成功していればスキャンを停止する
        handler.postDelayed(scanFinalizer, WAIT_CONNECT_PERIOD);

        // スキャン開始
        bluetoothAdapter.startLeScan(scanCallback);
    }

    private void stopScan() {
        bluetoothAdapter.stopLeScan(scanCallback);

        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
        bluetoothGatt = null;

        handler.removeCallbacks(scanFinalizer);
    }

    private boolean isConnected() {
        return (bluetoothGatt != null);
    }

    /**
     * {@link BluetoothGatt}からサービス・キャラクタリスティックの UUID を指定して{@link BluetoothGattCharacteristic}を取得する。<br />
     * 該当するサービス、キャラクタリスティックが存在しない場合は null を返す。
     *
     * @param sid サービスの UUID 文字列
     * @param cid キャラクタリスティックの UUID 文字列
     * @return 見つかった{@link BluetoothGattCharacteristic}
     */
    private BluetoothGattCharacteristic findCharacteristic(String sid, String cid) {
        if (!isConnected()) {
            return null;
        }

        UUID suuid = UUID.fromString(sid);
        UUID cuuid = UUID.fromString(cid);

        BluetoothGattService s = bluetoothGatt.getService(suuid);
        if (s == null) {
            AppLog.w("Service NOT found :" + sid);
            return null;
        }
        BluetoothGattCharacteristic c = s.getCharacteristic(cuuid);
        if (c == null) {
            AppLog.w("Characteristic NOT found :" + cid);
            return null;
        }
        return c;
    }

    /**
     * アラームを停止し、アラーム一覧画面へ遷移する。
     */
    public void stopAlarm() {
        ringtone.stop();
        stopScan();

        // 端末の Bluetooth 状態を元に戻す
        if (bluetoothDisabled) {
            bluetoothAdapter.disable();
        }

        Intent intent = new Intent(this, AlarmListActivity.class);
        startActivity(intent);

        finish();
    }

    /**
     * アラームを強制的に停止する。
     *
     * @param view {@link View}
     */
    public void stopAlarmForce(View view) {
        stopAlarm();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }

        // 何もしない
        return true;
    }
}
