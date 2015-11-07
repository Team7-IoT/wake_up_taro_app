package com.team7.wakeuptaroapp;

import android.app.Application;

import com.deploygate.sdk.DeployGate;

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
    }
}
