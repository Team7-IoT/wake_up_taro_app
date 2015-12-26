package com.team7.wakeuptaroapp.views.preferences;

import android.media.Ringtone;

import com.team7.wakeuptaroapp.BuildConfig;
import com.team7.wakeuptaroapp.activities.AlarmRegisterActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * {@link AlarmRingtonePreference}に対するテストクラス。
 *
 * @author Naotake.K
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class AlarmRingtonePreferenceTest {

    private AlarmRingtonePreference testee;
    private AlarmRegisterActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(AlarmRegisterActivity.class);
        testee = new AlarmRingtonePreference(activity);
    }

    @Test
    public void 初期状態のサマリー情報を取得できること() {
        // 実行
        testee.onSetInitialValue(false, null);
        // 検証
        assertThat(testee.getSummary()).isEqualTo("サイレント");
    }

    @Test
    public void 通知音にnullを指定した場合_サマリー情報を取得できないこと() {
        // 実行
        testee.setAsRingtoneSummary(null);
        // 検証
        assertThat(testee.getSummary()).isNull();
    }

    @Test
    public void 特定の通知音を指定した場合_サマリー情報を取得できること() {
        // モック準備
        Ringtone mock = mock(Ringtone.class);
        doReturn("TestRingtone").when(mock).getTitle(activity);
        // 実行
        testee.setAsRingtoneSummary(mock);
        // 検証
        assertThat(testee.getSummary()).isEqualTo("TestRingtone");
    }
}
