package obu.ckt.cricket.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import in.myinnos.customfontlibrary.TypefaceUtil;

/**
 * Created by Administrator on 10/11/2017.
 */

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Aleo-Regular.otf");
    }

}
