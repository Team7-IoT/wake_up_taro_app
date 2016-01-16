package com.team7.wakeuptaroapp.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.team7.wakeuptaroapp.utils.AppLog;

/**
 * BLE を使った Raspberry Pi の検索時に使用するコールバック。
 *
 * @author Naotake.K
 */
public class RpiLeScanCallback implements BluetoothAdapter.LeScanCallback {

    protected Activity activity;

    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        AppLog.d("RpiLeScanCallback.onLeScan: " + toString(device));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                doLeScan(device);
            }
        });
    }

    /**
     * スキャン時に行う処理。
     *
     * @param device {@link BluetoothDevice}
     */
    protected void doLeScan(BluetoothDevice device) {
    }

    private String toString(BluetoothDevice device) {
        StringBuilder sb = new StringBuilder();
        sb = sb.append("name=").append(device.getName());
        sb = sb.append(", bondStatus=").append(device.getBondState());
        sb = sb.append(", address=").append(device.getAddress());
        sb = sb.append(", type=").append(device.getType());
        return sb.toString();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
