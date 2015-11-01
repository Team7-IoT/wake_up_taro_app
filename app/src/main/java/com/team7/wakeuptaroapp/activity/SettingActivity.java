package com.team7.wakeuptaroapp.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.common.base.Objects;
import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.util.AppLog;
import com.team7.wakeuptaroapp.util.Toasts;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static com.team7.wakeuptaroapp.ble.RaspberryPi.TARGET_PERIPHERAL_NAME;


/**
 * 起動後の初期画面に対するアクティビティ。<br />
 * 基本的にはアラーム一覧画面へ遷移するためだけの画面。
 *
 * @author Naotake.K
 */
public class SettingActivity extends AppCompatActivity {

    @BindString(R.string.setting_connect)
    String labelSettingConnect;

    @BindString(R.string.title_dialog_ble_searching)
    String titleDialogBleSearching;

    @BindString(R.string.message_dialog_ble_searching)
    String messageDialogBleSearching;

    @Bind(R.id.setting_list)
    ListView listView;

    // スキャン時間 (5秒)
    private static final long SCAN_PERIOD = 5000;

    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private BluetoothGatt bluetoothGatt;

    // 親機を検索中に表示するダイアログ
    private ProgressDialog searchingDialog;

    private boolean needToastMessage;

    /**
     * BLE を使った親機の検索時に使用するコールバック。
     */
    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppLog.d("Scan device: " + toStringOfDevice(device));
                    if (Objects.equal(device.getName(), TARGET_PERIPHERAL_NAME)) {
                        AppLog.d("DeviceName: " + TARGET_PERIPHERAL_NAME);

                        bluetoothGatt = device.connectGatt(getApplicationContext(), false, gattCallback);
                        AppLog.i("Scan device connet gatt " + (bluetoothGatt == null));
                    }
                }
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

                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                AppLog.d("GATT New status is STATE_DISCONNECTED");

                stopScan();
            }
        }

        /**
         * {@link BluetoothGatt#discoverServices()}の呼び出し完了後に非同期で呼び出される。
         *
         * @see BluetoothGatt#discoverServices()
         * @see BluetoothGattCallback#onServicesDiscovered(BluetoothGatt, int)
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            AppLog.d("onConnectionStateChange status is " + status);
            if (status != BluetoothGatt.GATT_SUCCESS) {
                AppLog.d("onServicesDiscovered failed.");
                return;
            }

            for (BluetoothGattService service : gatt.getServices()) {
                if ((service == null) || (service.getUuid() == null)) {
                    AppLog.d("BluetoothGattService is Empty!!");
                    continue;
                }
                AppLog.d("BluetoothGattService UUID is " + service.getUuid().toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        // 戻る
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeSettingItems();
        initializeBleComponents();
        needToastMessage = false;
    }

    /**
     * 「接続検証」がクリックされた時の振る舞いを定義する。
     *
     * @param position 選択位置 (0 以外は例外を送出)
     */
    @OnItemClick(R.id.setting_list)
    public void onClickConnectValidate(int position) {
        if (position != 0) {
            throw new IllegalArgumentException("不正な項目が選択されました: " + position);
        }
        AppLog.d("onClickConnectValidate");

        // ラズパイ検索中のダイアログ表示
        searchingDialog = buildSearchingDialog();
        searchingDialog.show();
        needToastMessage = true;

        // BLE の機能を使って親機を検索
        bluetoothGatt = null;
        startScan();

        // TODO 検索結果 (device.getName()) をポップアップの一覧で表示し、そこから接続する親機を選択してもらう (TBD)
        // TODO 一覧から選択されたハードの情報（名前？アドレス？）を端末内に保存
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
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AppLog.d("Cancel SearchingDialog.");

                        // BLE 検索停止
                        stopScan();
                        needToastMessage = false;

                        // ProgressDialog をキャンセル
                        dialog.cancel();
                    }
                });

        return searchingDialog;
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

    private void initializeSettingItems() {
        // データ準備
        List<String> items = new ArrayList<>(1);
        items.add(labelSettingConnect);

        // Adapter - ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, items);
        listView.setAdapter(adapter);
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeSearchingDialog();
                showMessageByToast();
                stopScan();
            }
        }, SCAN_PERIOD);

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

    private boolean isConnected() {
        return (bluetoothGatt != null);
    }

    /**
     * 親機の検索結果に応じたメッセージを{@link android.widget.Toast}を使って表示する。<br />
     * ただし、検索中にキャンセルを押下された場合にはメッセージ表示は行わない。
     */
    private void showMessageByToast() {
        if (!needToastMessage) {
            return;
        }

        if (isConnected()) {
            Toasts.showMessageLong(this, R.string.message_found_ble, bluetoothGatt.getDevice().getName());
        } else {
            Toasts.showMessageLong(this, R.string.message_not_found_ble);
        }
    }
}
