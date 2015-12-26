package com.team7.wakeuptaroapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.models.Alarm;

import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static android.app.AlarmManager.INTERVAL_DAY;
import static android.app.AlarmManager.RTC_WAKEUP;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TaroAlarmManager}に対するテストクラス。
 *
 * @author Naotake.K
 */
public class TaroAlarmManagerTest {

    private TaroAlarmManager testee;
    private Context context;
    private AlarmManager alarmManager;

    @Before
    public void setUp() {
        context = mock(Context.class);
        alarmManager = mock(AlarmManager.class);
        when(context.getSystemService(Context.ALARM_SERVICE)).thenReturn(alarmManager);

        when(context.getString(R.string.label_alarm_mon)).thenReturn("月");
        when(context.getString(R.string.label_alarm_tue)).thenReturn("火");
        when(context.getString(R.string.label_alarm_wed)).thenReturn("水");
        when(context.getString(R.string.label_alarm_thu)).thenReturn("木");
        when(context.getString(R.string.label_alarm_fri)).thenReturn("金");
        when(context.getString(R.string.label_alarm_sat)).thenReturn("土");
        when(context.getString(R.string.label_alarm_sun)).thenReturn("日");

        testee = new TaroAlarmManager(context);
    }

    @Test
    public void アラーム登録が_1_回呼び出されること() {
        doNothing().when(alarmManager).setRepeating(
                eq(RTC_WAKEUP), any(Long.class), eq((INTERVAL_DAY * 7)), any(PendingIntent.class));

        testee.register(newAlarm("1_mon"));

        verify(alarmManager, times(1)).setRepeating(
                eq(RTC_WAKEUP), any(Long.class), eq((INTERVAL_DAY * 7)), any(PendingIntent.class));
    }

    @Test
    public void アラーム登録が_3_回呼び出されること() {
        doNothing().when(alarmManager).setRepeating(
                eq(RTC_WAKEUP), any(Long.class), eq((INTERVAL_DAY * 7)), any(PendingIntent.class));

        testee.register(newAlarm("1_mon", "3_wed", "5_fri"));

        verify(alarmManager, times(3)).setRepeating(
                eq(RTC_WAKEUP), any(Long.class), eq((INTERVAL_DAY * 7)), any(PendingIntent.class));
    }

    @Test
    public void アラーム取り消しが_1_回呼び出されること() {
        doNothing().when(alarmManager).cancel(any(PendingIntent.class));

        testee.cancel(newAlarm("1_mon"));

        verify(alarmManager, times(1)).cancel(any(PendingIntent.class));
    }

    @Test
    public void アラーム取り消しが_5_回呼び出されること() {
        doNothing().when(alarmManager).cancel(any(PendingIntent.class));

        testee.cancel(newAlarm("1_mon", "3_wed", "5_fri", "6_sat", "7_sun"));

        verify(alarmManager, times(5)).cancel(any(PendingIntent.class));
    }

    private Alarm newAlarm(String... targetDays) {
        Set<String> days = Sets.newLinkedHashSet(targetDays);
        return new Alarm("09:00", days, "RingRing");
    }
}
