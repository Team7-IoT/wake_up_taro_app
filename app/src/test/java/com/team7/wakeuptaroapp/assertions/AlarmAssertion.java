package com.team7.wakeuptaroapp.assertions;

import com.team7.wakeuptaroapp.models.Alarm;

import org.assertj.core.api.AbstractAssert;

import java.util.Arrays;
import java.util.Date;

/**
 * {@link Alarm}の検証を行うためのカスタム Assertion クラス。
 *
 * @author Naotake.K
 */
public class AlarmAssertion extends AbstractAssert<AlarmAssertion, Alarm> {

    protected AlarmAssertion(Alarm actual) {
        super(actual, AlarmAssertion.class);
    }

    public static AlarmAssertion assertThat(Alarm actual) {
        return new AlarmAssertion(actual);
    }

    public AlarmAssertion hasTime(String expected) {
        isNotNull();

        if (!actual.getTime().equals(expected)) {
            failWithMessage("Expected alarm time to be <%s> but was <%s>", expected, actual.getTime());
        }

        return this;
    }

    public AlarmAssertion hasTimeHour(int expected) {
        isNotNull();

        if (actual.getTimeHour() != expected) {
            failWithMessage("Expected alarm time hour to be <%s> but was <%s>", expected, actual.getTimeHour());
        }

        return this;
    }

    public AlarmAssertion hasTimeMinute(int expected) {
        isNotNull();

        if (actual.getTimeMinute() != expected) {
            failWithMessage("Expected alarm time minute to be <%s> but was <%s>", expected, actual.getTimeMinute());
        }

        return this;
    }

    public AlarmAssertion hasDayOfWeeks(String... expecteds) {
        isNotNull();

        String[] actuals = actual.getDayOfWeeks().toArray(new String[]{});
        Arrays.sort(actuals);
        Arrays.sort(expecteds);
        if (!Arrays.equals(actuals, expecteds)) {
            failWithMessage("Expected alarm DOWs to be <%s> but was <%s>",
                    Arrays.toString(expecteds), Arrays.toString(actuals));
        }

        return this;
    }

    public AlarmAssertion hasRingtoneUri(String expected) {
        isNotNull();

        if (!actual.getRingtoneUri().equals(expected)) {
            failWithMessage("Expected alarm ringtone uri to be <%s> but was <%s>", expected, actual.getRingtoneUri());
        }

        return this;
    }

    public AlarmAssertion isValid() {
        isNotNull();

        if (!actual.isValid()) {
            failWithMessage("Expected alarm to be valid but was not valid");
        }

        return this;
    }

    public AlarmAssertion isNotValid() {
        isNotNull();

        if (actual.isValid()) {
            failWithMessage("Expected alarm to be not valid but was valid");
        }

        return this;
    }

    public AlarmAssertion hasRegisteredDateTime(Long expected) {
        isNotNull();

        if (!actual.getAlarmKey().equals(expected)) {
            failWithMessage("Expected alarm registered datetime to be <%s> but was <%s>",
                    new Date(expected), new Date(actual.getRegisteredDateTime()));
        }

        return this;
    }
}
