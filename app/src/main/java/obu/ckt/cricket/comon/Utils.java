package obu.ckt.cricket.comon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import obu.ckt.cricket.R;

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
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_single_button);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        dialog.show();
        TextView tv = (TextView) dialog.findViewById(R.id.tv_single_dialog);
        Button btn = (Button) dialog.findViewById(R.id.btn_singleDialog);
        tv.setText(message);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                logout.click();
            }
        });
    }

    public static void singleAlertDialog(Context context, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_single_button);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        dialog.show();
        TextView tv = (TextView) dialog.findViewById(R.id.tv_single_dialog);
        Button btn = (Button) dialog.findViewById(R.id.btn_singleDialog);
        tv.setText(message);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static Dialog congratulations(Activity activity, String won) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_congratulation);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        TextView tvWon = (TextView) dialog.findViewById(R.id.tv_dialog_cong);
        tvWon.append(" : " + getTeamName(won));
        //lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        dialog.show();
        return dialog;
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

    public static String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy");
        String formattedDate = df.format(Calendar.getInstance().getTime());
        return formattedDate;
    }

    public interface Logout {
        void click();
    }
}
