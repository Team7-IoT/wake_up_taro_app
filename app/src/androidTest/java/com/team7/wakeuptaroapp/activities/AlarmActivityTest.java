package com.team7.wakeuptaroapp.activities;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TimePicker;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.models.Alarm;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.devland.esperandro.Esperandro;

import static com.team7.wakeuptaroapp.assertions.AlarmAssertion.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;

/**
 * {@link AlarmActivity}に対するテストクラス。<br />
 * テスト実行にはエミュレータ、もしくは接続された実機経由で行う必要がある。
 *
 * @author Naotake.K
 */
@RunWith(AndroidJUnit4.class)
public class AlarmActivityTest {

    @Rule
    public ActivityTestRule<AlarmActivity> alarmActivityRule = new ActivityTestRule<>(AlarmActivity.class);

    private TaroSharedPreference preference;

    @Before
    public void setUp() {
        preference = Esperandro.getPreferences(TaroSharedPreference.class,
                alarmActivityRule.getActivity().getApplicationContext());
    }

    @After
    public void tearDown() {
        preference.clear();
    }

    @Test
    public void 画面が表示されていること() {
        onView(isDisplayed());
    }

    @Test
    public void 選択した時刻が画面に表示されること() {
        // 事前検証
        onData(withKey("alarmTime")).onChildView(
                withId(android.R.id.summary)).check(matches(withText("00:00")));

        // アラーム時刻を選択
        selectTime();

        // 検証
        onData(withKey("alarmTime")).onChildView(
                withId(android.R.id.summary)).check(matches(withText("13:51")));
    }

    @Test
    public void 選択した曜日が画面に表示されること() {
        // 事前検証
        onData(withKey("alarmDayOfWeeks")).onChildView(
                withId(android.R.id.summary)).check(matches(withText("なし")));

        // 曜日を選択
        selectDayOfWeeks();

        // 検証
        onData(withKey("alarmDayOfWeeks")).onChildView(
                withId(android.R.id.summary)).check(matches(withText("週末")));
    }

    @Ignore("アラーム一覧から値を選択するには?")
    @Test
    public void 選択したアラーム音が画面に表示されること() {
        // 事前検証
        onData(withKey("alarmRingtone")).onChildView(
                withId(android.R.id.summary)).check(matches(withText("サイレント")));

        // ダイアログ表示
        onData(withKey("alarmRingtone")).perform(click());

        // アラーム音を選択
        onData(anything()).atPosition(3).perform(click());

        // OK
        onView(withText("OK")).perform(click());

        // 検証
        onData(withKey("alarmRingtone")).onChildView(
                withId(android.R.id.summary)).check(matches(not(withText("サイレント"))));
    }

    @Test
    public void 登録したアラーム情報が_SharedPreference_に保存されていること() {
        assertThat(preference.alarms()).isNull();

        // 登録内容を設定
        selectTime();
        selectDayOfWeeks();
        // TODO アラーム音を選択

        // 登録
        onView(withId(R.id.action_store_alarm)).perform(click());

        // 画面遷移を検証
        onView(withClassName(is("AlarmListActivity")));

        // SharedPreference を検証
        List<Alarm> storedAlarms = preference.alarms();
        assertThat(storedAlarms).hasSize(1);
        assertThat(storedAlarms.get(0)).hasTime("13:51").hasDayOfWeeks("6_sat", "7_sun");
    }

    private void selectTime() {
        // ダイアログ表示
        onData(withKey("alarmTime")).perform(click());

        // 時刻を選択
        onView(withClassName(equalTo(TimePicker.class.getName()))).perform(setTime(13, 51));

        // OK
        onView(withText("Set")).perform(click());
    }

    private void selectDayOfWeeks() {
        // ダイアログ表示
        onData(withKey("alarmDayOfWeeks")).perform(click());

        // 曜日を選択
        onView(withText("土")).perform(click());
        onView(withText("日")).perform(click());

        // OK
        onView(withText("OK")).perform(click());
    }

    public static ViewAction setTime(final int hour, final int minute) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                ((TimePicker) view).setCurrentHour(hour);
                ((TimePicker) view).setCurrentMinute(minute);
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(TimePicker.class);
            }

            @Override
            public String getDescription() {
                return "Set the date into the TimePicker(hour, minute)";
            }
        };
    }
}
