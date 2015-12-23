package com.team7.wakeuptaroapp.views.helpers;

import android.content.Context;
import android.support.annotation.NonNull;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.models.DayOfWeek;
import com.team7.wakeuptaroapp.utils.Alarms;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Set;

/**
 * アラームの曜日に関する操作をまとめたヘルパー。
 *
 * @author Naotake.K
 */
public class DayOfWeekHelper {

    private static final String DOW_LABEL_PREFIX = "label_alarm_";

    /**
     * 選択された曜日に応じて表示用のラベルへ変換する。
     * <ul>
     * <li>一つの選択されていない場合: なし</li>
     * <li>全ての曜日が選択された場合: 毎日</li>
     * <li>月 ～ 金だけが選択された場合: 平日</li>
     * <li>土日だけが選択された場合: 週末</li>
     * </ul>
     *
     * @param days    選択された曜日の一覧
     * @param context Context
     * @return 表示用のラベル
     */
    public static String convertToLabel(@NonNull Context context, @NonNull Set<String> days) {
        return convertToLabel(context, days.toArray(new String[]{}));
    }

    /**
     * @see #convertToLabel(Context, Set)
     */
    public static String convertToLabel(@NonNull Context context, @NonNull String... days) {
        Arrays.sort(days);
        if (days.length == 0) {
            return context.getString(R.string.label_alarm_none);
        }
        if (isEveryday(days)) {
            return context.getString(R.string.label_alarm_everyday);
        }
        if (isWeekday(days)) {
            return context.getString(R.string.label_alarm_weekday);
        }
        if (isWeekend(days)) {
            return context.getString(R.string.label_alarm_weekend);
        }
        return convertToDayOfWeeks(context, days);
    }

    private static boolean isEveryday(String... days) {
        return (days.length == 7);
    }

    private static boolean isWeekday(String... days) {
        if (days.length != 5) {
            return false;
        }
        return (!containsSaturday(days) && !containsSunday(days));
    }

    private static boolean isWeekend(String... days) {
        if (days.length != 2) {
            return false;
        }
        return (containsSaturday(days) && containsSunday(days));
    }

    private static boolean containsSaturday(String... days) {
        return ArrayUtils.contains(days, Alarms.DOW_SAT);
    }

    private static boolean containsSunday(String... days) {
        return ArrayUtils.contains(days, Alarms.DOW_SUN);
    }

    private static String convertToDayOfWeeks(Context context, String... days) {
        String[] labels = new String[days.length];
        for (int i = 0; i < days.length; i++) {
            int resId = DayOfWeek.resolve(days[i]).getResId();
            labels[i] = context.getString(resId);
        }

        String daysStr = Arrays.toString(labels);
        // "[" と "]" を取り除く
        return daysStr.substring(1, daysStr.length() - 1);
    }
}
