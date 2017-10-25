package obu.ckt.cricket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import obu.ckt.cricket.comon.SharePref;
import obu.ckt.cricket.comon.Utils;
import obu.ckt.cricket.data.DataLayer;

public class SplashActivity extends AppCompatActivity {
    private DataLayer layer;
    private SharePref prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        layer = DataLayer.getInstance(this);
        prefs = SharePref.getInstance(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (prefs.getValue(Utils.SHARED_USERID).length() > 0)
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                else startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 2000);
    }
}
