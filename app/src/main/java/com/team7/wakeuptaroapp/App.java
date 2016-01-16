package com.team7.wakeuptaroapp;

import android.app.Application;

import com.deploygate.sdk.DeployGate;
import com.team7.wakeuptaroapp.models.Alarm;
import com.team7.wakeuptaroapp.utils.TaroSharedPreference;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;

import de.devland.esperandro.Esperandro;
import de.devland.esperandro.serialization.JacksonSerializer;

/**
 * Application クラス。
 * <p/>
 * Created by naotake on 2015/10/10.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DeployGate.install(this);
        JodaTimeAndroid.init(this);
        Esperandro.setSerializer(new JacksonSerializer());

        // アラーム一覧の NULL チェックを意識しないで済むように初期化
        TaroSharedPreference preference = Esperandro.getPreferences(TaroSharedPreference.class, this);
        if (preference.alarms() == null) {
            preference.alarms(new ArrayList<Alarm>());
        }
    }
}
