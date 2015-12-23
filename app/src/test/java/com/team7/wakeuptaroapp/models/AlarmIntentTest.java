package com.team7.wakeuptaroapp.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.team7.wakeuptaroapp.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link AlarmIntent}に対するテストクラス。
 *
 * @author Naotake.K
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class AlarmIntentTest {

    private AlarmIntent testee;
    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application;
    }

    @Test
    public void 空のインスタンスを生成できること() {
        testee = new AlarmIntent();
        assertThat(testee.getAlarmKey()).isNull();
        assertThat(testee.getRingtoneUriAsString()).isNull();
        assertThat(testee.getRingtoneUri()).isNull();
    }

    @Test
    public void サービス用のインスタンスを生成できること() {
        Alarm alarm = newAlarm();
        testee = AlarmIntent.forService(context, alarm);

        assertThat(testee.getAlarmKey()).isNull();
        assertThat(testee.getRingtoneUriAsString()).isEqualTo(alarm.getRingtoneUri());
        assertThat(testee.getRingtoneUri()).isEqualTo(Uri.parse(alarm.getRingtoneUri()));
    }

    @Test
    public void アクティビティ用のインスタンスを生成できること() {
        testee = AlarmIntent.forActivity(context);

        assertThat(testee.getAlarmKey()).isNull();
        assertThat(testee.getRingtoneUriAsString()).isNull();
        assertThat(testee.getRingtoneUri()).isNull();
        assertThat(testee.getFlags()).isEqualTo(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Test
    public void Intentを基にインスタンスを生成できること() {
        Intent param = new Intent();
        param.putExtra("RINGTONE_URI", "RingRingRing");
        param.putExtra("ALARM_KEY", "DummyAlarmKey");

        testee = AlarmIntent.of(param);

        assertThat(testee.getAlarmKey()).isEqualTo("DummyAlarmKey");
        assertThat(testee.getRingtoneUriAsString()).isEqualTo("RingRingRing");
        assertThat(testee.getRingtoneUri()).isEqualTo(Uri.parse("RingRingRing"));
    }

    @Test
    public void 設定したアクション情報を取得できること() {
        testee = new AlarmIntent();

        testee.setActionAsUniqueKey(12345L);
        assertThat(testee.getAction()).isEqualTo("12345");

        testee.setActionAsTimerFinished();
        assertThat(testee.getAction()).isEqualTo("TIMER_FINISHED");
    }

    private Alarm newAlarm() {
        return new Alarm("19:30", null, "RingRing");
    }
}
