package com.team7.wakeuptaroapp.activities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TimePicker;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.models.Alarm;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import de.devland.esperandro.Esperandro;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.team7.wakeuptaroapp.activities.AlarmRegisterActivityTest.setTime;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;

/**
 * {@link AlarmUpdateActivity}に対するテストクラス。<br />
 * テスト実行にはエミュレータ、もしくは接続された実機経由で行う必要がある。
 *
 * @author Naotake.K
 */
@RunWith(AndroidJUnit4.class)
public class AlarmUpdateActivityTest {

    @Rule
    public ActivityTestRule<AlarmListActivity> alarmActivityRule = new ActivityTestRule<AlarmListActivity>(AlarmListActivity.class);

    private static final Long DUMMY_KEY = 1234567890L;

    private TaroSharedPreference preference;

    @Before
    public void setUp() {
        preference = Esperandro.getPreferences(TaroSharedPreference.class,
                alarmActivityRule.getActivity().getApplicationContext());
        preference.alarms(new ArrayList<Alarm>());
    }

    @After
    public void tearDown() {
        preference.clear();
    }

    @Ignore
    @Test
    public void 登録画面が入力した内容が更新画面に表示されていること() {
        onView(withClassName(is("AlarmListActivity")));

        // 登録画面へ
        onView(withId(R.id.new_alarm)).perform(click());
        onView(withClassName(is("AlarmRegisterActivity")));

        // 時刻を選択
        onData(withKey("alarmTime")).perform(click());
        onView(withClassName(equalTo(TimePicker.class.getName()))).perform(setTime(13, 51));
        onView(withText("Set")).perform(click());

        // 曜日を選択
        onData(withKey("alarmDayOfWeeks")).perform(click());
        onView(withText("水")).perform(click());
        onView(withText("OK")).perform(click());

        // 登録
        onView(withId(R.id.action_store_alarm)).perform(click());

        // 一覧画面へ
        onView(withClassName(is("AlarmListActivity")));

        // 更新画面へ
//        onView(withId(R.id.edit_alarm)).perform(click()); // TODO 仮実装
//        onView(withClassName(is("AlarmUpdateActivity")));

        // 検証
        onData(withKey("alarmTime")).onChildView(
                withId(android.R.id.summary)).check(matches(withText("13:51")));
        onData(withKey("alarmDayOfWeeks")).onChildView(
                withId(android.R.id.summary)).check(matches(withText("水")));
        onData(withKey("alarmRingtone")).onChildView(
                withId(android.R.id.summary)).check(matches(withText("サイレント")));
    }
}
