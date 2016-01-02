package com.team7.wakeuptaroapp.views.preferences;

import android.preference.PreferenceManager;

import com.team7.wakeuptaroapp.BuildConfig;
import com.team7.wakeuptaroapp.activities.AlarmRegisterActivity;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.devland.esperandro.Esperandro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link AlarmDayOfWeekPreference}に対するテストクラス。
 *
 * @author Naotake.K
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class AlarmDayOfWeekPreferenceTest {

    private AlarmDayOfWeekPreference testee;
    private AlarmRegisterActivity activity;
    private PreferenceManager preferenceMock;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(AlarmRegisterActivity.class);
        testee = new AlarmDayOfWeekPreference(activity, null);
        preferenceMock = mock(PreferenceManager.class);
    }

    @Test
    public void 初期状態のサマリー情報を取得できること() {
        // 実行
        testee.onSetInitialValue(true, null);
        // 検証
        assertThat(testee.getSummary()).isEqualTo("なし");
    }

    @Test
    public void 選択した曜日の内容でサマリー情報を取得できること() {
        Set<String> values = new HashSet<>(Arrays.asList("3_wed", "1_mon"));
        // 実行
        testee.listener.onPreferenceChange(testee, values);
        // 検証
        assertThat(testee.getSummary()).isEqualTo("月, 水");
    }

    @Test
    public void 保存済みの内容でサマリー情報を取得できること() {
        // 保存済みの情報をセット
        TaroSharedPreference preference = Esperandro.getPreferences(TaroSharedPreference.class, RuntimeEnvironment.application);
        preference.alarmDayOfWeeks(new HashSet<>(Arrays.asList("5_fri", "1_mon")));

        // PreferenceManager の振る舞いを定義
        when(preferenceMock.getSharedPreferences()).thenReturn(preference.get());
        Whitebox.setInternalState(testee, "mPreferenceManager", preferenceMock);
        testee.setKey("alarmDayOfWeeks");
        testee.setPersistent(true);

        // 実行
        testee.onSetInitialValue(true, null);
        // 検証
        assertThat(testee.getSummary()).isEqualTo("月, 金");
        verify(preferenceMock, times(2)).getSharedPreferences();
    }
}
