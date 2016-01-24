package com.team7.wakeuptaroapp.views.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.preference.Preference;
import android.util.AttributeSet;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.ble.RpiGattCallback;
import com.team7.wakeuptaroapp.ble.RpiLeScanCallback;
import com.team7.wakeuptaroapp.utils.AppLog;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;
import com.team7.wakeuptaroapp.utils.Toasts;
import com.team7.wakeuptaroapp.views.dialogs.AlertDialogBuilder;

import java.util.HashMap;
import java.util.Map;

import de.devland.esperandro.Esperandro;

import static android.content.Context.BLUETOOTH_SERVICE;
import static android.content.DialogInterface.BUTTON_NEGATIVE;

/**
 * 設定画面でラズパイとの疎通検証を行うための Preference クラス。
 *
 * @author Naotake.K
 */
public class SettingConnectionPreference extends Preference {

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

    // SharedPreference
    private TaroSharedPreference preference;

    // Fragment を扱う Activity
    private Activity activity;

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
            devices.put(device.getName(), device);
        }
    };

    /**
     * 親機との GATT 通信時に使用するコールバック。
     */
    private RpiGattCallback gattCallback = new RpiGattCallback() {
        /**
         * GATT 通信に成功したデバイス名の保存を行い、メッセージを表示する。
         *
         * @param gatt {@link BluetoothGatt}
         */
        @Override
        protected void doConnectionStateIfConnected(BluetoothGatt gatt) {
            // アラーム停止用にデバイス名を保存しておく
            preference.deviceName(bluetoothGatt.getDevice().getName());
            preference.deviceAddress(bluetoothGatt.getDevice().getAddress());

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    setSummary(preference.deviceName());
                    Toasts.showMessageLong(activity, R.string.message_found_ble, preference.deviceName());
                }
            });

            stopScan();
        }
    };

    /**
     * 一定時間スキャン後に呼び出す後処理。
     */
    private Runnable scanFinalizer = new Runnable() {
        @Override
        public void run() {
            closeSearchingDialog();
            stopScan();

            if (devices.isEmpty()) {
                Toasts.showMessageLong(activity, R.string.message_not_found_ble);
            } else {
                buildDevicesDialog().show();
            }
        }
    };

    public SettingConnectionPreference(Context context) {
        super(context);
        initialize();
    }

    public SettingConnectionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    @Override
    protected void onClick() {
        AppLog.d("onClick SettingConnectionPreference");

        // Bluetooth 有効判定
        if (!bluetoothAdapter.isEnabled()) {
            new AlertDialogBuilder.DisabledBluetooth(getContext()).show();
            return;
        }

        // ラズパイ検索中のダイアログ表示
        searchingDialog = buildSearchingDialog();
        searchingDialog.show();

        // BLE の機能を使って親機を検索
        bluetoothGatt = null;
        startScan();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        this.scanCallback.setActivity(activity);
    }

    private void initialize() {
        Context context = getContext();

        // SharedPreference
        preference = Esperandro.getPreferences(TaroSharedPreference.class, context);
        setSummary(preference.deviceName());

        // BLE Components
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        handler = new Handler(context.getMainLooper());
    }

    private void startScan() {
        // 5秒後に接続が成功していればスキャンを停止する
        handler.postDelayed(scanFinalizer, SCAN_PERIOD);

        devices = new HashMap<>();

        // スキャン開始
        bluetoothAdapter.startLeScan(scanCallback);
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

    /**
     * 親機の検索中に表示するモーダルなダイアログを組み立てる。
     *
     * @return 組み立てた {@link ProgressDialog}
     */
    private ProgressDialog buildSearchingDialog() {
        Context context = getContext();

        ProgressDialog searchingDialog = new ProgressDialog(context);
        searchingDialog.setTitle(getLabel(R.string.title_dialog_ble_searching));
        searchingDialog.setMessage(getLabel(R.string.message_dialog_ble_searching));
        searchingDialog.setCancelable(false);
        searchingDialog.setButton(BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppLog.d("Cancel SearchingDialog.");

                        // BLE 検索停止
                        stopScan();
                        handler.removeCallbacksAndMessages(null);
                        devices.clear();

                        // ProgressDialog をキャンセル
                        dialog.cancel();
                    }
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
        final Context context = getContext();

        final String[] labels = devices.keySet().toArray(new String[devices.size()]);

        final int defaultIndex = 0;
        selectedDevice = labels[defaultIndex];

        return new AlertDialog.Builder(context)
                .setTitle(getLabel(R.string.title_dialog_ble_select))
                .setSingleChoiceItems(labels, defaultIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedDevice = labels[which];
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bluetoothGatt = devices.get(selectedDevice).connectGatt(context, false, gattCallback);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

    private void stopScan() {
        bluetoothAdapter.stopLeScan(scanCallback);

        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
        bluetoothGatt = null;

        handler.removeCallbacks(scanFinalizer);
    }

    private String getLabel(int resId) {
        return getContext().getString(resId);
    }
}
