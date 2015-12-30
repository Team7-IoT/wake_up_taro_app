package com.team7.wakeuptaroapp.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;

import com.team7.wakeuptaroapp.utils.AppLog;

/**
 * Raspberry Pi に対する GATT 通信時に使用するコールバック。
 *
 * @author Naotake.K
 */
public class RpiGattCallback extends BluetoothGattCallback {

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);

        AppLog.d(String.format("onConnectionStateChange Current status(%s) to New Status(%s).", status, newState));

        if (newState == BluetoothProfile.STATE_CONNECTED) {
            AppLog.d("GATT New status is STATE_CONNECTED");

            doConnectionStateIfConnected(gatt);
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            AppLog.d("GATT New status is STATE_DISCONNECTED");

            doConnectionStateIfDisconnected(gatt);
        }
    }

    /**
     * GATT 通信のステータスが {@link BluetoothProfile#STATE_CONNECTED} になった時の処理を行う。
     *
     * @param gatt {@link BluetoothGatt}
     */
    protected void doConnectionStateIfConnected(BluetoothGatt gatt) {
    }

    /**
     * GATT 通信のステータスが {@link BluetoothProfile#STATE_DISCONNECTED} になった時の処理を行う。
     *
     * @param gatt {@link BluetoothGatt}
     */
    protected void doConnectionStateIfDisconnected(BluetoothGatt gatt) {
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        AppLog.d("onConnectionStateChange status is " + status);

        if (status != BluetoothGatt.GATT_SUCCESS) {
            AppLog.w("onServicesDiscovered failed.");
            return;
        }

        doServicesDiscoveredIfGattSuccess(gatt);
    }

    /**
     * GATT 通信が成功した時の処理を行う。
     *
     * @param gatt {@link BluetoothGatt}
     */
    protected void doServicesDiscoveredIfGattSuccess(BluetoothGatt gatt) {
    }
}
