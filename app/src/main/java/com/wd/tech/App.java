package com.wd.tech;

import android.app.Application;
import android.content.Context;

/**
 * Created by mumu on 2018/12/20.
 */

public class App extends Application {
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static Context getAppContext() {
        return app;
    }
}
