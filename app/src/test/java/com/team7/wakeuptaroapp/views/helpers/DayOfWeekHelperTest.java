package com.team7.wakeuptaroapp.views.helpers;

import android.content.Context;

import com.team7.wakeuptaroapp.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link DayOfWeekHelper}に対するテストクラス。
 *
 * @author Naotake.K
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class DayOfWeekHelperTest {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application;
    }

    @Test
    public void 選択された曜日のラベル一覧を取得できること() {
        String actual = DayOfWeekHelper.convertToLabel(context, "1_mon", "3_wed", "5_fri");
        assertThat(actual).isEqualTo("月, 水, 金");
    }

    @Test
    public void 曜日が未指定なら_なし_のラベルを取得できること() {
        String actual = DayOfWeekHelper.convertToLabel(context);
        assertThat(actual).isEqualTo("なし");
    }

    @Test
    public void 毎日のラベルを取得できること() {
        String actual = DayOfWeekHelper.convertToLabel(context, "1_mon", "2_tue", "3_wed", "4_thu", "5_fri", "7_sat", "7_sun");
        assertThat(actual).isEqualTo("毎日");
    }

    @Test
    public void 平日のラベルを取得できること() {
        String actual = DayOfWeekHelper.convertToLabel(context, "1_mon", "2_tue", "3_wed", "4_thu", "5_fri");
        assertThat(actual).isEqualTo("平日");
    }

    @Test
    public void 週末のラベルを取得できること() {
        String actual = DayOfWeekHelper.convertToLabel(context, "6_sat", "7_sun");
        assertThat(actual).isEqualTo("週末");
    }

    @Test
    public void Set形式から選択された曜日のラベル一覧を取得できること() {
        Set<String> params = new HashSet<>(3);
        params.add("1_mon");
        params.add("3_wed");
        params.add("4_thu");
        String actual = DayOfWeekHelper.convertToLabel(context, params);
        assertThat(actual).isEqualTo("月, 水, 木");
    }
}
