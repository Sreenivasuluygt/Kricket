package obu.ckt.cricket.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import in.myinnos.customfontlibrary.TypefaceUtil;

/**
 * Created by Administrator on 10/11/2017.
 */

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Aleo-Regular.otf");
    }

}
