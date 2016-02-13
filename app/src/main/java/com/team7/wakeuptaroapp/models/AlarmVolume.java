package com.team7.wakeuptaroapp.models;

import com.team7.wakeuptaroapp.utils.Preconditions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * アラームの音量に関する情報を扱うモデル。
 *
 * @author Naotake.K
 */
public class AlarmVolume {

    private static final int VOLUME_INTERVAL = 5;
    private static final int VOLUME_MAX_PERCENT = 100;

    private final int value;

    private AlarmVolume(int value) {
        this.value = value;
    }

    public static AlarmVolume of(int value) {
        return new AlarmVolume(value);
    }

    /**
     * 最大音量の数値を加味した現在の音量を調整する。
     *
     * @param max 最大音量
     * @return 調整結果
     */
    public int adjust(int max) {
        Preconditions.checkPositive(max, "Alarm max volume required positive, but was " + max);

        BigDecimal rate = BigDecimal.valueOf(value * VOLUME_INTERVAL).divide(BigDecimal.valueOf(VOLUME_MAX_PERCENT));
        BigDecimal result = BigDecimal.valueOf(max).multiply(rate).setScale(0, RoundingMode.UP);

        return result.intValue();
    }

    public String format() {
        return String.format(Locale.JAPAN, "%d%%", value * VOLUME_INTERVAL);
    }

    @Override
    public String toString() {
        return format();
    }
}
