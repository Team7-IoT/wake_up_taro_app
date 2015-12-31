package com.team7.wakeuptaroapp.views.preferences;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.team7.wakeuptaroapp.utils.AppLog;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;
import com.team7.wakeuptaroapp.views.dialogs.AlertDialogBuilder;

import de.devland.esperandro.Esperandro;

import static android.content.Context.BLUETOOTH_SERVICE;

/**
 * 設定画面でラズパイとのセンサー検証を行うための Preference クラス。
 *
 * @author Naotake.K
 */
public class SettingCensorPreference extends Preference {

    // BLE 周りのコンポーネント群
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;

    // SharedPreference
    private TaroSharedPreference preference;

    // Fragment を扱う Activity
    private Activity activity;

    public SettingCensorPreference(Context context) {
        super(context);
        initialize();
    }

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

        // TODO 未実装
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private void initialize() {
        Context context = getContext();

        // SharedPreference
        preference = Esperandro.getPreferences(TaroSharedPreference.class, context);

        // BLE Components
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        handler = new Handler(context.getMainLooper());
    }
}
