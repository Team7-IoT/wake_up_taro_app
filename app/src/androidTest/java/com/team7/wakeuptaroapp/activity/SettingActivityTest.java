package com.team7.wakeuptaroapp.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link SettingActivity}に対するテストクラス。<br />
 * テスト実行にはエミュレータ、もしくは接続された実機経由で行う必要がある。
 *
 * @author Naotake.K
 */
@RunWith(AndroidJUnit4.class)
public class SettingActivityTest {

    @Rule
    public ActivityTestRule<SettingActivity> settingActivityRule = new ActivityTestRule<>(SettingActivity.class);

    private SettingActivity testee;

    @Before
    public void setUp() {
        testee = settingActivityRule.getActivity();
    }

    @Test
    public void 画面の初期化が行われていること() throws Exception {
        onView(isDisplayed());

        ListView actual = testee.listView;
        String expectedLabel = testee.labelSettingConnect;

        assertThat(actual).isNotNull();
        assertThat(actual.getAdapter().getCount()).isEqualTo(1);
        assertThat(actual.getAdapter().getItem(0)).isEqualTo(expectedLabel);

        assertThat(readField(testee, "needToastMessage", true)).isEqualTo(false);
    }
}