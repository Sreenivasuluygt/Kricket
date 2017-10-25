package obu.ckt.cricket.data;

import android.content.Context;

import obu.ckt.cricket.comon.SharePref;
import obu.ckt.cricket.comon.Utils;
import obu.ckt.cricket.model.User;

/**
 * Created by Administrator on 10/14/2017.
 */

public class DataLayer {
    private static final DataLayer ourInstance = new DataLayer();
    private static Context mContext;

    public static DataLayer getInstance(Context context) {
        mContext = context;
        return ourInstance;
    }

    private DataLayer() {
    }

    public void saveUser(SharePref prefs, User user) {
        prefs.saveValue(Utils.SHARED_EMAIL, user.email);
        prefs.saveValue(Utils.SHARED_NAME, user.name);
        prefs.saveValue(Utils.SHARED_USERID, user.userId);
        prefs.saveValue(Utils.SHARED_PASSWORD, user.password);
    }

    public User getUser(SharePref prefs) {
        return new User(prefs.getValue(Utils.SHARED_USERID), prefs.getValue(Utils.SHARED_NAME),
                prefs.getValue(Utils.SHARED_EMAIL), prefs.getValue(Utils.SHARED_PASSWORD));
    }
}
