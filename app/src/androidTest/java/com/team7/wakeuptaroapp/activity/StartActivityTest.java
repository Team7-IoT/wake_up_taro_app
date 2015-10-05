package com.team7.wakeuptaroapp.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.team7.activity.wakeuptaroapp.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.Is.is;

/**
 * {@link StartActivity}に対するテストクラス。<br />
 * テスト実行にはエミュレータ、もしくは接続された実機経由で行う必要がある。
 */
@RunWith(AndroidJUnit4.class)
public class StartActivityTest {

    @Rule
    public ActivityTestRule<StartActivity> startActivityRule = new ActivityTestRule<>(StartActivity.class);

    @Test
    public void 画面が表示されていること() {
        onView(isDisplayed());
    }

    @Test
    public void スタートボタンが押下され画面遷移すること() {
        // 事前状態
        onView(withClassName(is("StartActivity")));

        // 実行
        onView(withId(R.id.start)).perform(click());

        // 事後状態
        onView(withClassName(is("AlarmListActivity")));
    }
}
