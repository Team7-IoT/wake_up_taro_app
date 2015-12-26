package com.team7.wakeuptaroapp.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Alarms}に対するテストクラス。
 *
 * @author Naotake.K
 */
public class AlarmsTest {

    @Test
    public void 時間と分から時刻情報を取得できること() {
        assertThat(Alarms.formatTime(1, 8)).isEqualTo("01:08");
        assertThat(Alarms.formatTime(9, 35)).isEqualTo("09:35");
        assertThat(Alarms.formatTime(22, 2)).isEqualTo("22:02");
    }

    @Test
    public void 時刻文字列から時間を取り出せること() {
        assertThat(Alarms.selectHour("01:08")).isEqualTo(1);
        assertThat(Alarms.selectHour("09:35")).isEqualTo(9);
        assertThat(Alarms.selectHour("22:02")).isEqualTo(22);
    }

    @Test
    public void testSelectMinute() {
        assertThat(Alarms.selectMinute("01:08")).isEqualTo(8);
        assertThat(Alarms.selectMinute("09:35")).isEqualTo(35);
        assertThat(Alarms.selectMinute("22:02")).isEqualTo(2);
    }
}
