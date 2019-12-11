package ym.pdf;

import android.app.Application;
import android.content.Context;

/**
 * @author : Gavin.GaoTJ 19.06.2019
 * @description :
 */
public class App extends Application {

    private static App app;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static App getApplication() {
        return app;
    }
}
