package com.team7.wakeuptaroapp.models;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

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
}