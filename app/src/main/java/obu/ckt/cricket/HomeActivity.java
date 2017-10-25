package obu.ckt.cricket;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import obu.ckt.cricket.adapters.HomeAdapter;
import obu.ckt.cricket.comon.SharePref;
import obu.ckt.cricket.comon.Utils;
import obu.ckt.cricket.data.DataLayer;
import obu.ckt.cricket.database.DatabaseHandler;
import obu.ckt.cricket.interfaces.CreateMatch;
import obu.ckt.cricket.interfaces.MatchHistory;
import obu.ckt.cricket.model.Match;
import obu.ckt.cricket.model.User;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView rv_matches;
    private TextView tvCreate;
    private SharePref pref;
    private DataLayer layer;
    private User user;
    private Match match;
    private DatabaseHandler db;
    private List<Match> matchList = new ArrayList<>();
    private HomeAdapter adapter;
    private ImageView ivLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initControls();
    }

    private void initControls() {
        rv_matches = (RecyclerView) findViewById(R.id.rv_matches_home);
        rv_matches.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HomeAdapter(this, new HomeAdapter.ItemClick() {
            @Override
            public void onClick(String matchId) {
                Intent i = new Intent(HomeActivity.this, MatchActivity.class);
                i.putExtra(MatchActivity.EXTRA_MATCHE_ID, matchId);
                startActivity(i);
            }
        });
        rv_matches.setAdapter(adapter);
        tvCreate = (TextView) findViewById(R.id.tv_createMatch_home);
        ivLogout = (ImageView) findViewById(R.id.iv_logout_home);
        tvCreate.setOnClickListener(this);
        ivLogout.setOnClickListener(this);
        pref = SharePref.getInstance(this);
        layer = DataLayer.getInstance(this);
        user = layer.getUser(pref);
        db = new DatabaseHandler(this);
        recycler();
    }

    private void recycler() {
        db.getMatches(user.userId, new MatchHistory() {
            @Override
            public void success(List<Match> matches) {
                matchList.clear();
                matchList = matches;
                adapter.setMatchList(matchList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure() {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_createMatch_home:
                createMatchDialog(HomeActivity.this);
                break;
            case R.id.iv_logout_home:
                Utils.singleAlertDialogCallBack(HomeActivity.this, "Do you want to logout", new Utils.Logout() {
                    @Override
                    public void click() {
                        pref.clearAll();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    }
                });
                break;
        }
    }

    public Dialog createMatchDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_match);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        final EditText et1stTeam, et2ndTeam, etToss, et1stBat, etOvers;
        Button btnCreate;
        et1stTeam = (EditText) dialog.findViewById(R.id.et_firstTeam_create);
        et2ndTeam = (EditText) dialog.findViewById(R.id.et_secondTeam_create);
        etToss = (EditText) dialog.findViewById(R.id.et_toss_create);
        et1stBat = (EditText) dialog.findViewById(R.id.et_firstBat_create);
        etOvers = (EditText) dialog.findViewById(R.id.et_overs_create);
        btnCreate = (Button) dialog.findViewById(R.id.btnCreate_create);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (Utils.checkEmpty(et1stTeam) || Utils.checkEmpty(et2ndTeam) || Utils.checkEmpty(et1stBat)
                            || Utils.checkEmpty(etToss) || Utils.checkEmpty(etOvers)) {
                        Toast.makeText(getApplicationContext(), "enter all the fields", Toast.LENGTH_SHORT).show();
                    } else if (Utils.getText(et1stTeam).length() < 2 || Utils.getText(et2ndTeam).length() < 2) {
                        Toast.makeText(getApplicationContext(), "Team names should be more than 3 characters", Toast.LENGTH_SHORT).show();
                    } else if (!(Utils.getText(et1stBat).equalsIgnoreCase(Utils.getText(et1stTeam))
                            || Utils.getText(et1stBat).equalsIgnoreCase(Utils.getText(et2ndTeam)))) {
                        Toast.makeText(getApplicationContext(), "Enter whose batting first?", Toast.LENGTH_SHORT).show();
                    } else if (!(Utils.getText(etToss).equalsIgnoreCase(Utils.getText(et1stTeam))
                            || Utils.getText(etToss).equalsIgnoreCase(Utils.getText(et2ndTeam)))) {
                        Toast.makeText(getApplicationContext(), "Enter who won toss?", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject obj = new JSONObject();
                        obj.put("toss", Utils.getText(etToss));
                        obj.put("1stBatting", Utils.getText(et1stBat));
                        obj.put("overs", Utils.getText(etOvers));
                        match = new Match("", user.userId, Utils.getText(et1stTeam)
                                , Utils.getText(et2ndTeam), obj.toString(), "created");
                        db.insertMatch(match, -1, new CreateMatch() {
                            @Override
                            public void success(int matchId) {
                                dialog.dismiss();
                                match.userId = String.valueOf(matchId);
                                recycler();
                            }

                            @Override
                            public void failure() {

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
        return dialog;
    }

    @Override
    protected void onResume() {
        super.onResume();
        recycler();
    }
}
