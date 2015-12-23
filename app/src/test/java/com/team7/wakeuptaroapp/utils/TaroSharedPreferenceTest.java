package com.team7.wakeuptaroapp.utils;

import com.team7.wakeuptaroapp.BuildConfig;
import com.team7.wakeuptaroapp.models.Alarm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.devland.esperandro.Esperandro;

import static com.team7.wakeuptaroapp.assertions.AlarmAssertion.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link TaroSharedPreference}に対するテストクラス。
 *
 * @author Naotake.K
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class TaroSharedPreferenceTest {

    private TaroSharedPreference testee;

    @Before
    public void setUp() {
        testee = Esperandro.getPreferences(TaroSharedPreference.class, RuntimeEnvironment.application);
    }

    @After
    public void tearDown() {
        testee.clear();
    }

    @Test
    public void デバイス名を取得保存できること() {
        assertThat(testee.deviceName()).isEmpty();

        testee.deviceName("HogeFuga");
        assertThat(testee.deviceName()).isEqualTo("HogeFuga");
    }

    @Test
    public void 曜日一覧を取得保存できること() {
        assertThat(testee.alarmDayOfWeeks()).isNull();

        Set<String> days = new HashSet<>(3);
        days.add("月");
        days.add("水");
        days.add("金");
        testee.alarmDayOfWeeks(days);

        assertThat(testee.alarmDayOfWeeks()).hasSize(3).contains("月", "水", "金");
    }

    @Test
    public void アラーム情報一覧を取得保存できること() {
        assertThat(testee.alarms()).isNull();

        // 空を保存
        testee.alarms(new ArrayList<Alarm>());
        assertThat(testee.alarms()).isNotNull().isEmpty();

        // アラーム 1 件保存
        Alarm alarm = newAlarm("08:00", "Ringtone1", "土", "日");
        ArrayList<Alarm> alarms = testee.alarms();
        alarms.add(alarm);
        testee.alarms(alarms);

        // アラーム取得
        alarms = testee.alarms();
        assertThat(alarms).isNotNull().hasSize(1);
        assertThat(alarms.get(0)).hasTime("08:00").isNotValid()
                .hasRegisteredDateTime(alarm.getRegisteredDateTime())
                .hasRingtoneUri("Ringtone1").hasDayOfWeeks("土", "日");
    }

    private Alarm newAlarm(String time, String ringtoneUri, String... days) {
        Set<String> dayOfWeeks = new HashSet<>(Arrays.asList(days));
        return new Alarm(time, dayOfWeeks, ringtoneUri);
    }
}
