package com.team7.wakeuptaroapp.models;

import com.team7.wakeuptaroapp.exceptions.AlarmConstraintViolationsException;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Arrays;
import java.util.HashSet;

import static com.team7.wakeuptaroapp.assertions.AlarmAssertion.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Alarm}に対するテストクラス。
 *
 * @author Naotake.K
 */
public class AlarmTest {

    private Alarm testee;

    @Before
    public void setUp() {
        testee = new Alarm();
    }

    @Test
    public void 登録日時を基に_equals_の判定が行われること() {
        LocalDateTime now = LocalDateTime.now();
        Alarm other = new Alarm();
        other.setRegisteredDateTime(now.minusDays(1).toDateTime().getMillis());

        // 事前検証
        assertThat(testee).isNotEqualTo(other);

        // 登録日時を上書き
        Whitebox.setInternalState(testee, "registeredDateTime", now.toDateTime().getMillis());
        Whitebox.setInternalState(other, "registeredDateTime", now.toDateTime().getMillis());

        // 検証
        assertThat(testee).isEqualTo(other);
    }

    @Test
    public void 登録日時を基に_compareTo_の判定が行われること() {
        LocalDateTime now = LocalDateTime.now();
        Alarm other = new Alarm();
        other.setRegisteredDateTime(now.minusDays(1).toDateTime().getMillis());

        // 事前検証
        assertThat(testee.compareTo(other)).isNotEqualTo(0);

        // 登録日時を上書き
        Whitebox.setInternalState(testee, "registeredDateTime", now.toDateTime().getMillis());
        Whitebox.setInternalState(other, "registeredDateTime", now.toDateTime().getMillis());

        // 検証
        assertThat(testee.compareTo(other)).isEqualTo(0);
    }

    @Test
    public void アラーム時刻の情報を取得できること() {
        testee.setTime("19:32");
        assertThat(testee).hasTimeHour(19).hasTimeMinute(32);
    }

    @Test(expected = AlarmConstraintViolationsException.class)
    public void 曜日が未選択の場合に例外が送出されること() throws Exception {
        testee.validate();
    }

    @Test
    public void 曜日が1件でも選択されていた場合は例外が送出されないこと() throws Exception {
        testee.setDayOfWeeks(new HashSet<String>(Arrays.asList("日")));
        testee.validate();
        // 何もおきないこと
    }

    @Test
    public void 現在値から値の変更があった場合に_true_が返されること() {
        // 変更無し
        assertThat(testee.isChanged("", new HashSet<String>(), "")).isTrue();

        // 時間
        assertThat(testee.isChanged("12:34", new HashSet<String>(), "")).isTrue();

        // 曜日
        assertThat(testee.isChanged("", new HashSet<String>(Arrays.asList("月")), "")).isTrue();

        // アラーム音
        assertThat(testee.isChanged("", new HashSet<String>(), "HogeHoge")).isTrue();
    }
}