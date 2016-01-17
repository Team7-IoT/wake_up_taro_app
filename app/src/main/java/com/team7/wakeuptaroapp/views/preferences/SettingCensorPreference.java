package com.team7.wakeuptaroapp.views.preferences;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.ble.RaspberryPi;
import com.team7.wakeuptaroapp.ble.RpiGattCallback;
import com.team7.wakeuptaroapp.ble.RpiLeScanCallback;
import com.team7.wakeuptaroapp.utils.AppLog;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;
import com.team7.wakeuptaroapp.utils.Toasts;
import com.team7.wakeuptaroapp.views.dialogs.AlertDialogBuilder;

import java.util.UUID;

import de.devland.esperandro.Esperandro;

import static android.content.Context.BLUETOOTH_SERVICE;
import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static com.team7.wakeuptaroapp.BuildConfig.NOTIFICATION_CHARACTERISTIC_UUID;
import static com.team7.wakeuptaroapp.BuildConfig.NOTIFICATION_SERVICE_UUID;

/**
 * 設定画面でラズパイとのセンサー検証を行うための Preference クラス。
 *
 * @author Naotake.K
 */
public class SettingCensorPreference extends Preference {

    // センサーの検証待ち時間 (10秒)
    private static final long WAIT_MOTION_PERIOD = 10000;

    // BLE 周りのコンポーネント群
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private BluetoothGatt bluetoothGatt;

    // センサー検証中に表示するダイアログ
    private ProgressDialog waitingDialog;

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
            AppLog.d("device.getName(): " + device.getName());
            AppLog.d("preference.deviceName(): " + preference.deviceName());
            if (TextUtils.equals(device.getName(), preference.deviceName())) {
                AppLog.d("Found device: " + preference.deviceName());
                bluetoothGatt = device.connectGatt(getContext(), false, gattCallback);
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
            // デバイスのボタン押下の通知受け取り設定
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
         * 親機を経由したセンサーからの通知を受け取った場合に、検証成功のメッセージを表示する。
         *
         * @param gatt {@link BluetoothGatt}
         * @param characteristic {@link BluetoothGattCharacteristic}
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            AppLog.d("onCharacteristicChanged UUID: " + characteristic.getUuid().toString());

            if (TextUtils.equals(characteristic.getUuid().toString(), NOTIFICATION_CHARACTERISTIC_UUID)) {
                AppLog.d("Notification characteristic: " + characteristic.getValue()[0]);

                if (characteristic.getValue()[0] == 1) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeWaitingDialog();
                            Toasts.showMessageLong(activity, R.string.message_motion_success);
                        }
                    });

                    stopScan();
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
            closeWaitingDialog();
            stopScan();

            Toasts.showMessageLong(activity, R.string.message_motion_failure);
        }
    };

    public SettingCensorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    @Override
    protected void onClick() {
        AppLog.d("onClick SettingCensorPreference");

        // Bluetooth 有効判定
        if (!bluetoothAdapter.isEnabled()) {
            new AlertDialogBuilder.DisabledBluetooth(getContext()).show();
            return;
        }

        // 接続検証済み親機の存在判定
        if (TextUtils.isEmpty(preference.deviceName())) {
            new AlertDialogBuilder.UnknownDevice(getContext()).show();
            return;
        }

        // センサー検証中のダイアログ表示
        waitingDialog = buildWaitingDialog();
        waitingDialog.show();

        // BLE の機能を使って親機を検索
        bluetoothGatt = null;
        handler = new Handler(getContext().getMainLooper());
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

        // BLE Components
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    private void startScan() {

        // 中途半端な接続情報をクリア
        stopScan();

        // 5秒後に接続が成功していればスキャンを停止する
        handler.postDelayed(scanFinalizer, WAIT_MOTION_PERIOD);

        // スキャン開始
        bluetoothAdapter.startLeScan(scanCallback);
    }

    /**
     * センサー検証のダイアログを閉じ、同時に BLE 検索も終了する。
     */
    private void closeWaitingDialog() {
        if (waitingDialog == null) {
            return;
        }
        waitingDialog.cancel();
        waitingDialog = null;
    }

    /**
     * 親機の検索中に表示するモーダルなダイアログを組み立てる。
     *
     * @return 組み立てた {@link ProgressDialog}
     */
    private ProgressDialog buildWaitingDialog() {
        Context context = getContext();

        ProgressDialog waitingDialog = new ProgressDialog(context);
        waitingDialog.setTitle(R.string.title_dialog_motion_waiting);
        waitingDialog.setMessage(getLabel(R.string.message_dialog_motion_waiting));
        waitingDialog.setCancelable(false);
        waitingDialog.setButton(BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppLog.d("Cancel WaitingDialog.");

                        // BLE 検索停止
                        stopScan();
                        handler.removeCallbacksAndMessages(null);

                        // ProgressDialog をキャンセル
                        dialog.cancel();
                    }
                });

        return waitingDialog;
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

    private String getLabel(int resId) {
        return getContext().getString(resId);
    }
}
