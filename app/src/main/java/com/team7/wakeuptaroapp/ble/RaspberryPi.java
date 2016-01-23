package com.team7.wakeuptaroapp.ble;

import java.util.UUID;

/**
 * Raspberry Pi に関する情報を定義するクラス。
 *
 * @author Naotake.K
 */
public class RaspberryPi {

    /**
     * キャラクタリスティック設定UUID (BluetoothLeGattプロジェクト、SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIGより
     */
    private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final UUID CLIENT_CHARACTERISTIC_UUID = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG);
}
