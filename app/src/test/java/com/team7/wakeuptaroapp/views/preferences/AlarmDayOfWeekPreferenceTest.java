package com.team7.wakeuptaroapp.views.preferences;

import com.team7.wakeuptaroapp.BuildConfig;
import com.team7.wakeuptaroapp.activities.AlarmActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link AlarmDayOfWeekPreference}に対するテストクラス。
 *
 * @author Naotake.K
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class AlarmDayOfWeekPreferenceTest {

    private AlarmDayOfWeekPreference testee;
    private AlarmActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(AlarmActivity.class);
        testee = new AlarmDayOfWeekPreference(activity);
    }

    @Test
    public void 初期状態のサマリー情報を取得できること() {
        // 実行
        testee.onSetInitialValue(false, null);
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
}
