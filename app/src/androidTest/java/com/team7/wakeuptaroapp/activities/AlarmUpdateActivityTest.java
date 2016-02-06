package com.team7.wakeuptaroapp.activities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TimePicker;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.models.Alarm;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import de.devland.esperandro.Esperandro;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.team7.wakeuptaroapp.activities.AlarmRegisterActivityTest.setTime;
import static com.team7.wakeuptaroapp.assertions.AlarmAssertion.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AnyOf.anyOf;
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
    public ActivityTestRule<AlarmListActivity> alarmActivityRule = new ActivityTestRule<>(AlarmListActivity.class);

    private TaroSharedPreference preference;

    @Before
    public void setUp() {
        preference = Esperandro.getPreferences(TaroSharedPreference.class,
                alarmActivityRule.getActivity().getApplicationContext());
        preference.alarms(new ArrayList<Alarm>());

        // 一覧画面から開始
        onView(withClassName(is("AlarmListActivity")));
    }

    @After
    public void tearDown() {
        preference.clear();
    }

    @Test
    public void 登録画面が入力した内容が更新画面に表示されていること() {
        registerAlarm();

        // 一覧画面へ戻る
        onView(withClassName(is("AlarmListActivity")));

        // 更新画面へ
        onData(anything()).inAdapterView(withId(R.id.alarm_list)).atPosition(0).perform(click());

        // 検証
        onData(withKey("alarmTime")).onChildView(
                withId(android.R.id.summary)).check(matches(withText("13:51")));
        onData(withKey("alarmDayOfWeeks")).onChildView(
                withId(android.R.id.summary)).check(matches(withText("月, 金")));
        onData(withKey("alarmRingtone")).onChildView(
                withId(android.R.id.summary)).check(matches(withText("サイレント")));
    }

    @Test
    public void 更新したアラーム情報が_SharedPreference_に保存されていること() {
        registerAlarm();
        assertThat(preference.alarms()).hasSize(1);

        // 一覧画面へ戻る
        onView(withClassName(is("AlarmListActivity")));

        // 更新画面へ
        onData(anything()).inAdapterView(withId(R.id.alarm_list)).atPosition(0).perform(click());

        // 時刻を選択
        onData(withKey("alarmTime")).perform(click());
        onView(withClassName(equalTo(TimePicker.class.getName()))).perform(setTime(8, 15));
        onView(withText("Set")).perform(click());

        // 曜日を選択
        onData(withKey("alarmDayOfWeeks")).perform(click());
        onView(withText("水")).perform(click());
        onView(withText("金")).perform(click());
        onView(withText("OK")).perform(click());

        // 更新
        onView(withId(R.id.action_update_alarm)).perform(click());

        // 一覧画面へ戻る
        onView(withClassName(is("AlarmListActivity")));

        // SharedPreference を検証
        List<Alarm> storedAlarms = preference.alarms();
        assertThat(storedAlarms).hasSize(1);
        assertThat(storedAlarms.get(0)).hasTime("08:15").hasDayOfWeeks("1_mon", "3_wed");
    }

    @Test
    public void 曜日が未選択の場合にエラーダイアログが表示されること() {
        registerAlarm();

        // 一覧画面へ戻る
        onView(withClassName(is("AlarmListActivity")));

        // 更新画面へ
        onData(anything()).inAdapterView(withId(R.id.alarm_list)).atPosition(0).perform(click());

        // 曜日の選択を解除
        onData(withKey("alarmDayOfWeeks")).perform(click());
        onView(withText("月")).perform(click());
        onView(withText("金")).perform(click());
        onView(withText("OK")).perform(click());

        // 更新
        onView(withId(R.id.action_update_alarm)).perform(click());

        // エラーダイアログを検証
        onView(withText("アラームを鳴らす曜日を 1 つ以上選択してください。")).check(matches(isDisplayed()));
    }

    @Test
    public void 値を変更して一覧画面へ戻ろうとした場合に確認ダイアログが表示されること() {
        registerAlarm();

        // 一覧画面へ戻る
        onView(withClassName(is("AlarmListActivity")));

        // 更新画面へ
        onData(anything()).inAdapterView(withId(R.id.alarm_list)).atPosition(0).perform(click());

        // 曜日の選択を解除
        onData(withKey("alarmDayOfWeeks")).perform(click());
        onView(withText("金")).perform(click());
        onView(withText("OK")).perform(click());

        // 更新
        // CircleCI 環境依存に対応
        onView(anyOf(withContentDescription("上へ移動"), withContentDescription("Navigate up"))).perform(click());

        // ダイアログを検証
        onView(withText("アラーム情報が変更されていますが、戻りますか?")).check(matches(isDisplayed()));
    }

    private void registerAlarm() {
        // 登録画面へ
        onView(withId(R.id.new_alarm)).perform(click());
        onView(withClassName(is("AlarmRegisterActivity")));

        // 時刻を選択
        onData(withKey("alarmTime")).perform(click());
        onView(withClassName(equalTo(TimePicker.class.getName()))).perform(setTime(13, 51));
        onView(withText("Set")).perform(click());

        // 曜日を選択
        onData(withKey("alarmDayOfWeeks")).perform(click());
        onView(withText("火")).perform(click());
        onView(withText("水")).perform(click());
        onView(withText("木")).perform(click());
        onView(withText("OK")).perform(click());

        // 登録
        onView(withId(R.id.action_store_alarm)).perform(click());
    }
}
