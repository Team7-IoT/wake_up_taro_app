package com.team7.wakeuptaroapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.common.base.Preconditions;
import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.util.AppLog;
import com.team7.wakeuptaroapp.util.TaroSharedPreference;
import com.team7.wakeuptaroapp.util.Toasts;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import de.devland.esperandro.Esperandro;

import static android.content.DialogInterface.BUTTON_NEGATIVE;


/**
 * 起動後の初期画面に対するアクティビティ。<br />
 * 基本的にはアラーム一覧画面へ遷移するためだけの画面。
 *
 * @author Naotake.K
 */
public class SettingActivity extends AppCompatActivity {

    @BindString(R.string.setting_connect)
    String labelSettingConnect;

    @BindString(R.string.title_dialog_disabled_bluetooth)
    String titleDialogDisabledBluetooth;

    @BindString(R.string.message_dialog_disabled_bluetooth)
    String messageDialogDisabledBluetooth;

    @BindString(R.string.title_dialog_ble_searching)
    String titleDialogBleSearching;

    @BindString(R.string.message_dialog_ble_searching)
    String messageDialogBleSearching;

    @BindString(R.string.title_dialog_ble_select)
    String titleDialogBleSelect;

    @Bind(R.id.setting_list)
    ListView listView;

    // スキャン時間 (5秒)
    private static final long SCAN_PERIOD = 5000;

    // BLE 周りのコンポーネント群
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private BluetoothGatt bluetoothGatt;

    // 親機を検索中に表示するダイアログ
    private ProgressDialog searchingDialog;

    // 検索中の親機一覧
    private Map<String, BluetoothDevice> devices;
    private String selectedDevice;
    private boolean needToastMessage;

    private TaroSharedPreference preference;

    /**
     * BLE を使った親機の検索時に使用するコールバック。
     */
    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            runOnUiThread(() -> {
                AppLog.d("Scan device: " + toStringOfDevice(device));

                devices.put(device.getName(), device);
            });
        }

        /**
         * {@link BluetoothDevice}の概要を表す文字列表現を返す。
         *
         * TODO コールバック内に存在する必要性は無いので移動させたい
         *
         * @param device スキャンして見つかった {@link BluetoothDevice}
         * @return {@link BluetoothDevice}の概要
         */
        private String toStringOfDevice(BluetoothDevice device) {
            StringBuilder sb = new StringBuilder();
            sb = sb.append("name=").append(device.getName());
            sb = sb.append(", bondStatus=").append(device.getBondState());
            sb = sb.append(", address=").append(device.getAddress());
            sb = sb.append(", type=").append(device.getType());
            return sb.toString();
        }
    };

    /**
     * GATT 通信時に使用するコールバック。
     */
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            AppLog.d(String.format("onConnectionStateChange Current status(%s) to New Status(%s).", status, newState));

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                AppLog.d("GATT New status is STATE_CONNECTED");

                // アラーム停止用にデバイス名を保存しておく
                preference.deviceName(bluetoothGatt.getDevice().getName());

                final Activity activity = SettingActivity.this;
                activity.runOnUiThread(() -> {
                    initializeSettingItems();
                    Toasts.showMessageLong(activity, R.string.message_found_ble, bluetoothGatt.getDevice().getName());
                });

                stopScan();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // 戻る
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ButterKnife DI
        ButterKnife.bind(this);

        // SharedPreference
        preference = Esperandro.getPreferences(TaroSharedPreference.class, getApplicationContext());

        initializeSettingItems();
        initializeBleComponents();
    }

    /**
     * 「接続検証」がクリックされた時の振る舞いを定義する。
     *
     * @param position 選択位置 (0 以外は例外を送出)
     */
    @OnItemClick(R.id.setting_list)
    public void onClickConnectValidate(int position) {
        Preconditions.checkArgument((position != 0), "不正な項目が選択されました: " + position);

        AppLog.d("onClickConnectValidate");

        // Bluetooth 有効判定
        if (!bluetoothAdapter.isEnabled()) {
            buildDisabledBluetoothAlertDialog().show();
            return;
        }

        // ラズパイ検索中のダイアログ表示
        searchingDialog = buildSearchingDialog();
        searchingDialog.show();
        needToastMessage = true;

        // BLE の機能を使って親機を検索
        bluetoothGatt = null;
        startScan();
    }

    /**
     * Bluetooth が無効である旨を通知するダイアログを組み立てる。
     *
     * @return 組み立てた {@link AlertDialog}
     */
    private AlertDialog buildDisabledBluetoothAlertDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(titleDialogDisabledBluetooth)
                .setMessage(messageDialogDisabledBluetooth)
                .setPositiveButton("OK", null)
                .create();
    }

    /**
     * 親機の検索中に表示するモーダルなダイアログを組み立てる。
     *
     * @return 組み立てた {@link ProgressDialog}
     */
    private ProgressDialog buildSearchingDialog() {
        ProgressDialog searchingDialog = new ProgressDialog(this);
        searchingDialog.setTitle(titleDialogBleSearching);
        searchingDialog.setMessage(messageDialogBleSearching);
        searchingDialog.setCancelable(false);
        searchingDialog.setButton(BUTTON_NEGATIVE, "Cancel",
                (dialog, which) -> {
                    AppLog.d("Cancel SearchingDialog.");

                    // BLE 検索停止
                    stopScan();
                    handler.removeCallbacksAndMessages(null);
                    devices.clear();
                    needToastMessage = false;

                    // ProgressDialog をキャンセル
                    dialog.cancel();
                });

        return searchingDialog;
    }

    /**
     * 見つかった親機の一覧を表示するダイアログを組み立てる。<br />
     * 一覧の先頭を選択状態とする。
     *
     * @return 組み立てた {@link AlertDialog}
     */
    private AlertDialog buildDevicesDialog() {
        final String[] labels = devices.keySet().toArray(new String[devices.size()]);

        final int defaultIndex = 0;
        selectedDevice = labels[defaultIndex];

        return new AlertDialog.Builder(this)
                .setTitle(titleDialogBleSelect)
                .setSingleChoiceItems(labels, defaultIndex, (dialog, which) -> {
                    selectedDevice = labels[which];
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    bluetoothGatt = devices.get(selectedDevice).connectGatt(getApplicationContext(), false, gattCallback);
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
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

        // 戻るボタン押下
        if (id == android.R.id.home) {
            AppLog.d("Tap to back on setting.");
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 設定項目を初期化する。
     * TODO イケてない実装です。Android での設定項目のイディオムを知りたい
     */
    private void initializeSettingItems() {

        List<String> items = new ArrayList<>(1);
        items.add(composeLabelSettingConnect());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, items);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    private String composeLabelSettingConnect() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(labelSettingConnect);

        String deviceName = preference.deviceName();
        if (StringUtils.isNotEmpty(deviceName)) {
            buffer.append("   (").append(deviceName).append(")");
        }

        return buffer.toString();
    }

    /**
     * BLE 接続時に必要なコンポーネントを初期化する。
     */
    private void initializeBleComponents() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        handler = new Handler(getApplicationContext().getMainLooper());
    }

    private void startScan() {

        // 5秒後に接続が成功していればスキャンを停止する
        handler.postDelayed(() -> {
            closeSearchingDialog();
            stopScan();

            // キャンセル時は何もしない
            if (!needToastMessage) {
                return;
            }

            if (devices.isEmpty()) {
                Toasts.showMessageLong(SettingActivity.this, R.string.message_not_found_ble);
            } else {
                buildDevicesDialog().show();
            }
        }, SCAN_PERIOD);

        devices = new HashMap<>();

        // スキャン開始
        bluetoothAdapter.startLeScan(scanCallback);
    }

    private void stopScan() {
        bluetoothAdapter.stopLeScan(scanCallback);

        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
        bluetoothGatt = null;
    }

    /**
     * 親機検索のダイアログを閉じ、同時に BLE 検索も終了する。
     */
    private void closeSearchingDialog() {
        if (searchingDialog == null) {
            return;
        }
        searchingDialog.cancel();
        searchingDialog = null;
    }
}
