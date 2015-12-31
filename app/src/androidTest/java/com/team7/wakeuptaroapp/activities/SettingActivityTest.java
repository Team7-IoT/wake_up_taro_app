package com.team7.wakeuptaroapp.activities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.team7.wakeuptaroapp.BuildConfig;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.devland.esperandro.Esperandro;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringStartsWith.startsWith;

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

        // バージョン情報
        onData(withKey("appVersion")).onChildView(
                withId(android.R.id.summary)).check(matches(withText(startsWith(BuildConfig.VERSION_NAME))));
    }
}