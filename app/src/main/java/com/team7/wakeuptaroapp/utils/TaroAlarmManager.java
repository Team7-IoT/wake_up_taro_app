package com.team7.wakeuptaroapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.NonNull;

import com.team7.wakeuptaroapp.models.Alarm;
import com.team7.wakeuptaroapp.models.AlarmIntent;
import com.team7.wakeuptaroapp.models.DayOfWeek;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import static android.app.AlarmManager.INTERVAL_DAY;

/**
 * 起こしタロウのアラーム通知に関する処理を扱うクラス。
 *
 * @author Naotake.K
 */
public class TaroAlarmManager {

    private static final int SNOOZE_INTERVAL_MINUTE = 5;

    private final Context context;
    private final AlarmManager alarmManager;

    /**
     * インスタンスを生成する。
     *
     * @param context {@link Context}
     */
    public TaroAlarmManager(@NonNull Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * アラーム情報を登録する。<br />
     * 指定されたアラーム情報の内容に基づき、繰り返し指定でアラームの情報を登録する。<br />
     * 仮にアラーム情報に含まれる曜日が「月、水、金」の場合、アラーム情報としては 3 件登録する。
     *
     * @param alarm 登録対象のアラーム情報
     */
    public void register(@NonNull Alarm alarm) {
        Preconditions.notNull(alarm, "Register alarm required!!");

        for (String dayOfWeek : alarm.getDayOfWeeks()) {
            DayOfWeek dow = DayOfWeek.resolve(dayOfWeek);

            // アラーム時間
            Long alarmTime = generateAlarmTime(alarm, dow);

            AlarmIntent intent = AlarmIntent.forService(context, alarm);
            intent.setActionAsUniqueKey(alarm.getAlarmKey() + dow.getConstants()); // アラーム情報を重複させないよう識別させる
            intent.setAlarmKey(alarm.getAlarmKey());
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // FIXME アラームという性質上、時間の (多少の) 不正確を許可する setInexactRepeating を使用していない
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, (INTERVAL_DAY * 7), pendingIntent);
        }
    }

    /**
     * アラームのスヌーズを登録する。<br />
     * 現在時刻から一定時間後に再度アラームが鳴るように設定する。
     *
     * @param ringtoneUri アラーム音の URI
     */
    public void snoozeRegister(@NonNull String ringtoneUri) {
        Preconditions.notNull(ringtoneUri, "Snooze register alarm ringtone uri required!!");

        Long snoozeTime = generateSnoozeTime();

        AlarmIntent intent = AlarmIntent.forService(context, ringtoneUri);
        intent.setActionAsUniqueKey(Long.valueOf(snoozeTime.hashCode())); // アラーム情報を重複させないよう識別させる
        intent.setAlarmKey(Long.valueOf(snoozeTime.hashCode()));
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent);
    }

    /**
     * アラーム情報の取り消しを行う。<br />
     * 指定されたアラーム情報の内容に基づき、キーとなるアラーム情報 ({@link Alarm#getRegisteredDateTime()}) が同一のアラーム情報を全て取り消す。
     *
     * @param alarm 取り消し対象のアラーム情報
     */
    public void cancel(@NonNull Alarm alarm) {
        Preconditions.notNull(alarm, "Cancel alarm required!!");

        for (String dayOfWeek : alarm.getDayOfWeeks()) {
            DayOfWeek dow = DayOfWeek.resolve(dayOfWeek);

            AlarmIntent intent = AlarmIntent.forService(context, alarm);
            intent.setActionAsUniqueKey(alarm.getAlarmKey() + dow.getConstants()); // アラーム情報を重複させないよう識別させる
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // 同一 Action 値のアラームを全てキャンセル
            alarmManager.cancel(pendingIntent);
        }
    }

    /**
     * 直近のアラーム時刻を生成する。<br />
     * アラーム情報 ({@link Alarm})、曜日 ({@link DayOfWeek}) を基に、直近のアラーム時刻を生成する。
     * <pre>
     *     例えば、現在時刻「2015/12/23(水) 15:32」で、アラーム時間が「8:00」、曜日が「木」なら、生成される時刻は「2015/12/24(木) 8:00」。
     *     同じく、曜日が「水」なら、生成される時刻は「2015/12/30(水) 8:00」となる。
     * </pre>
     *
     * @param alarm アラーム情報
     * @param dow   アラームを起動する曜日
     * @return アラーム時刻を表す値
     */
    private Long generateAlarmTime(Alarm alarm, DayOfWeek dow) {

        LocalDateTime dateTime = LocalDateTime.now().minuteOfHour().roundFloorCopy();

        if (dateTime.getDayOfWeek() != dow.getConstants()) {
            dateTime = dateTime.withDayOfWeek(dow.getConstants());
            // 曜日設定後、日付が過去日の場合は 1 週間後を設定
            if (dateTime.isBefore(LocalDateTime.now())) {
                dateTime = dateTime.plusWeeks(1);
            }
        } else if (isOverAlarmTime(dateTime, alarm)) {
            dateTime = dateTime.plusWeeks(1);
        }

        dateTime = dateTime.withHourOfDay(alarm.getTimeHour()).withMinuteOfHour(alarm.getTimeMinute());

        AppLog.d("AlarmTime(DOW): " + dateTime.toString(DateTimeFormat.fullDateTime()) + "(" + dow.getResId() + ")");
        return dateTime.toDateTime().getMillis();
    }

    /**
     * スヌーズ用に現在時刻から 5 分後の時刻を生成する。
     *
     * @return アラーム時刻を表す値
     */
    private Long generateSnoozeTime() {

        LocalDateTime dateTime = LocalDateTime.now().minuteOfHour().roundFloorCopy();
        dateTime = dateTime.plusMinutes(SNOOZE_INTERVAL_MINUTE);

        AppLog.d("SnoozeTime: " + dateTime.toString(DateTimeFormat.fullDateTime()));
        return dateTime.toDateTime().getMillis();
    }

    /**
     * 現在時刻を基にアラーム時刻が過ぎているかどうかを判定する。<br />
     * 判定には時分 (HH:MM) の情報で行い、同時刻の場合は過ぎている (true) と判断する。
     *
     * @param now   現在日時 (時刻)
     * @param alarm アラーム情報
     * @return アラーム時刻が既に過ぎている場合は true
     */
    private boolean isOverAlarmTime(LocalDateTime now, Alarm alarm) {
        String nowTime = String.format("%02d:%02d", now.getHourOfDay(), now.getMinuteOfHour());
        return (nowTime.compareTo(alarm.getTime()) >= 0);
    }
}
