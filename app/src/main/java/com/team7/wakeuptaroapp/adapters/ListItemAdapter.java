package com.team7.wakeuptaroapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.models.Alarm;
import com.team7.wakeuptaroapp.utils.TaroAlarmManager;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;
import com.team7.wakeuptaroapp.views.helpers.DayOfWeekHelper;

import java.util.List;

import de.devland.esperandro.Esperandro;

/**
 * アラーム一覧情報をビューに渡すアダプタークラス。
 *
 * @author Shiori.K
 * @author Naotake.K
 */
public class ListItemAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Alarm> itemList;
    private TaroSharedPreference preference;
    private TaroAlarmManager alarmManager;

    public ListItemAdapter(Context context) {
        this.context = context;
        // LayoutInflaterを取得
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // SharedPreference
        preference = Esperandro.getPreferences(TaroSharedPreference.class, context);
        // AlarmManager
        alarmManager = new TaroAlarmManager(context);
    }

    /**
     * アラーム一覧を設定する。
     *
     * @param itemList
     */
    public void setItemList(List<Alarm> itemList) {
        this.itemList = itemList;
    }

    /**
     * 設定されているアラームの数を取得する。
     *
     * @return アラーム一覧のオブジェクト数
     */
    @Override
    public int getCount() {
        if (itemList == null) {
            return 0;
        }
        return itemList.size();
    }

    /**
     * リストの指定した位置のアラームを取得する。
     *
     * @param position
     * @return 指定位置のアラーム情報
     */
    @Override
    public Object getItem(int position) {
        if (itemList == null || position < 0 || position >= itemList.size()) {
            return null;
        }
        return itemList.get(position);
    }

    /**
     * 指定位置のリストIDを取得する。
     *
     * @param position
     * @return リストID
     */
    @Override
    public long getItemId(int position) {
        return itemList.get(position).getRegisteredDateTime();
    }

    /**
     * アラーム一覧画面を表示させる。
     *
     * @param position
     * @param convertView
     * @param parent
     * @return View(アラーム一覧画面)
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = layoutInflater.inflate(R.layout.list_item, parent, false);

        // アラーム時刻設定する
        ((TextView) convertView.findViewById(R.id.alarm_time)).setText(itemList.get(position).getTime());
        // アラーム設定曜日を設定する
        ((TextView) convertView.findViewById(R.id.alarm_day_of_time)).setText(DayOfWeekHelper.convertToLabel(context, itemList.get(position).getDayOfWeeks()));
        // アラーム状態 (有効/無効) を設定する
        ((Switch) convertView.findViewById(R.id.alarm_switch)).setChecked(itemList.get(position).isValid());

        // スイッチの ON/OFF 変更時の挙動を設定
        Switch aSwitch = (Switch) convertView.findViewById(R.id.alarm_switch);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // ON
                    alarmManager.register(itemList.get(position));

                    List<Alarm> alarms = preference.alarms();
                    Alarm alarm = alarms.get(position);
                    alarms.remove(alarm);
                    alarm.setValid(true);
                    alarms.add(alarm);
                    preference.alarms(alarms);

                } else { // OFF
                    alarmManager.cancel(itemList.get(position));

                    List<Alarm> alarms = preference.alarms();
                    Alarm alarm = alarms.get(position);
                    alarms.remove(alarm);
                    alarm.setValid(false);
                    alarms.add(alarm);
                    preference.alarms(alarms);

                }
            }
        });

        return convertView;
    }

    public void addList(Alarm alarm) {
        itemList.add(alarm);
    }
}
