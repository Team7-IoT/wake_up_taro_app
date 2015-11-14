package com.team7.wakeuptaroapp.ble;

import java.util.UUID;

/**
 * Raspberry Pi に関する情報を定義するクラス。
 *
 * @author Naotake.K
 */
public class RaspberryPi {

    /**
     * プロトタイプで使用した BLE デバイス名。
     */
    public static final String TARGET_PERIPHERAL_NAME = "BSHSBTPT01BK";

    // TODO 下記の値は今後修正予定
    public static final UUID ALERT_SERVICE_UUID = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_POWER_STATE_UUID = UUID.fromString("00002a1b-0000-1000-8000-00805f9b34fb");
    public static final UUID ALERT_LEVEL_UUID = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");
}
