package com.team7.wakeuptaroapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.models.Alarm;

import java.util.ArrayList;
import java.util.List;

/** データ一覧をビューに渡すために使用されるクラス */
public class ListItemAdapter extends BaseAdapter {

    Context context;
    // 他のxmlリソースのviewを扱うための変数
    LayoutInflater layoutInflater = null;
    List<Alarm> itemList;

    public ListItemAdapter(Context context) {
        this.context = context;
        // LayoutInflaterを取得
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * アラーム一覧を設定する
     *
     * @param itemList
     */
    public void setItemList(List<Alarm> itemList) {
        this.itemList = itemList;
    }

    /**
     * 設定されているアラームの数を取得する
     *
     * @return アラーム一覧のオブジェクト数
     */
    @Override
    public int getCount() {
        return itemList.size();
    }

    /**
     * リストの指定した位置のアラームを取得する
     *
     * @param position
     * @return 指定位置のアラーム情報
     */
    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    /**
     * 指定位置のリストIDを取得する
     *
     * @param position
     * @return リストID
     */
    @Override
    public long getItemId(int position) {
        return itemList.get(position).getId();
    }

    /**
     * アラーム一覧画面を表示させる
     *
     * @param position
     * @param convertView
     * @param parent
     * @return View(アラーム一覧画面)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_item, parent, false);

        // アラーム時刻を取得
        ((TextView)convertView.findViewById(R.id.textView1)).setText(itemList.get(position).getTime());
        // 曜日を取得
        ((TextView)convertView.findViewById(R.id.textView2)).setText((itemList.get(position).getDayOfWeeks()).toString());

        return convertView;
    }

}
