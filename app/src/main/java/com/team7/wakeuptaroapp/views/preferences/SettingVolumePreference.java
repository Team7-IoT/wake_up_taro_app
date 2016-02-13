package com.team7.wakeuptaroapp.views.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.team7.wakeuptaroapp.R;
import com.team7.wakeuptaroapp.models.AlarmVolume;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;

import de.devland.esperandro.Esperandro;

/**
 * アプリ内共通で使用するアラームのボリューム音量を設定するための Preference クラス。
 *
 * @author Naotake.K
 */
public class SettingVolumePreference extends DialogPreference {

    private static final int LAYOUT_PADDING = 10;
    private static final int SEEK_BAR_MAX = 20;

    private SeekBar bar;
    private Context context;
    private TaroSharedPreference preference;

    public SettingVolumePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        preference = Esperandro.getPreferences(TaroSharedPreference.class, context);
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout layout = new LinearLayout(context);
        layout.setPadding(LAYOUT_PADDING, LAYOUT_PADDING, LAYOUT_PADDING, LAYOUT_PADDING);
        bar = new SeekBar(context);
        bar.setMax(SEEK_BAR_MAX);
        bar.setProgress(preference.alarmVolume());
        layout.addView(bar, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        return layout;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            preference.alarmVolume(bar.getProgress());

            // persist
            updateSummary();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        updateSummary();
    }

    private void updateSummary() {
        int storedVolume = preference.alarmVolume();
        if (storedVolume == 0) {
            setSummary(R.string.label_mute);
        } else {
            setSummary(AlarmVolume.of(storedVolume).format());
        }
    }
}
