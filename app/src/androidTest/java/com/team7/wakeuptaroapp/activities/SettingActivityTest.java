package com.team7.wakeuptaroapp.activities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.team7.wakeuptaroapp.utils.TaroSharedPreference;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.devland.esperandro.Esperandro;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

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

    private TaroSharedPreference preference;

    @Before
    public void setUp() {
        preference = Esperandro.getPreferences(TaroSharedPreference.class,
                settingActivityRule.getActivity().getApplicationContext());
    }

    @After
    public void tearDown() {
        preference.clear();
    }

    @Test
    public void 画面が表示されていること() {
        onView(isDisplayed());
    }
}