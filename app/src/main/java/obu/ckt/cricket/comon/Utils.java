package obu.ckt.cricket.comon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Administrator on 10/13/2017.
 */

public class Utils {
    public static int RC_SIGN_IN = 101;
    public static String SHARED_USERID = "SHARED_USERID";
    public static String SHARED_EMAIL = "SHARED_EMAIL";
    public static String SHARED_NAME = "SHARED_NAME";
    public static String SHARED_PASSWORD = "SHARED_PASSWORD";

    public static String EXTRA_MATCHE_ID = "matchId";

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    public static int JSON_STRIKING = 1;
    public static int JSON_BOWLING = 1;

    //ViewTypes
    public static int VIEWTYPE_HEADING = 1, VIEWTYPE_BATSMEN_HEADING = 2, VIEWTYPE_BATSMEN = 3, VIEWTYPE_SCORES = 4,
            VIEWTYPE_BOWLER_HEADING = 6, VIEWTYPE_BOWLER = 7;


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
                uniqueID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                ;
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

    public static void singleAlertDialog(Context context, String message) {
        final AlertDialog builder = new AlertDialog.Builder(context).create();
        builder.setMessage(message);
        builder.setButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    public static String getTeamName(String team) {
        String name = "";

        if (team.contains(" ")) {
            String[] str = team.split("\\s+");
            for (String i : str) {
                name = name + i.substring(0, 1);
            }
        } else {
            name = team.toUpperCase().substring(0, 3);
        }

        return name.toUpperCase();

    }

    public interface Logout {
        void click();
    }
}
