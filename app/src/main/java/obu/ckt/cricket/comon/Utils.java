package obu.ckt.cricket.comon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

import static java.security.AccessController.getContext;

/**
 * Created by Administrator on 10/13/2017.
 */

public class Utils {
    public static int RC_SIGN_IN = 101;
    public static String SHARED_USERID = "SHARED_USERID";
    public static String SHARED_EMAIL = "SHARED_EMAIL";
    public static String SHARED_NAME = "SHARED_NAME";
    public static String SHARED_PASSWORD = "SHARED_PASSWORD";

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    public static int JSON_STRIKING = 1, JSON_NONSTRIKING = 0;
    public static int JSON_BOWLING = 1;
    public static int JSON_NOTOUT = 1, JSON_OUT = 0;

    public static boolean checkEmpty(EditText et) {
        return et.getText().toString().isEmpty();
    }

    public static String getText(EditText et) {
        return et.getText().toString();
    }

    public static String getText(TextView et) {
        return et.getText().toString();
    }

    public synchronized static String udid(Context context) {
        if (uniqueID == null) {
            SharePref sharedPrefs = SharePref.getInstance(context);
            uniqueID = sharedPrefs.getValue(PREF_UNIQUE_ID);
            if (uniqueID.length() == 0) {
                uniqueID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);;
                sharedPrefs.saveValue(PREF_UNIQUE_ID, uniqueID);
            }
        }
        return uniqueID;
        //1723399700dfaa1c
    }

    public static void singleAlertDialogCallBack(Context context, String message, final Logout logout) {
        final AlertDialog builder = new AlertDialog.Builder(context).create();
        builder.setMessage(message);
        builder.setButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.dismiss();
                logout.click();
            }
        });
        builder.show();
    }

    public interface Logout {
        void click();
    }
}
