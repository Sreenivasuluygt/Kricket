package obu.ckt.cricket.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import obu.ckt.cricket.MatchActivity;
import obu.ckt.cricket.MatchHistoryActivity;
import obu.ckt.cricket.R;
import obu.ckt.cricket.adapters.HomeAdapter;
import obu.ckt.cricket.comon.SharePref;
import obu.ckt.cricket.comon.Utils;
import obu.ckt.cricket.data.DataLayer;
import obu.ckt.cricket.database.DatabaseHandler;
import obu.ckt.cricket.interfaces.CreateMatch;
import obu.ckt.cricket.interfaces.MatchHistory;
import obu.ckt.cricket.model.Match;
import obu.ckt.cricket.model.User;

public class QuickFragment extends Fragment implements View.OnClickListener {
    private RecyclerView rv_matches;
    private TextView tvCreate;
    private SharePref pref;
    private DataLayer layer;
    private User user;
    private Match match;
    private DatabaseHandler db;
    private List<Match> matchList = new ArrayList<>();
    private HomeAdapter adapter;
    private View view;
    private Activity activity;
    private static String BUNDLE = "Bundle";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_quick, container, false);
        initControls();
        return view;
    }

    public static QuickFragment newInstance(String current) {
        QuickFragment fragment = new QuickFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE, current);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initControls() {
        rv_matches = (RecyclerView) view.findViewById(R.id.rv_matches_home);
        rv_matches.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new HomeAdapter(activity, new HomeAdapter.ItemClick() {
            @Override
            public void onClick(String matchId, String status) {
                Intent i;
                if (status.equalsIgnoreCase("completed"))
                    i = new Intent(activity, MatchHistoryActivity.class);
                else i = new Intent(activity, MatchActivity.class);
                i.putExtra(Utils.EXTRA_MATCHE_ID, matchId);
                startActivity(i);
            }
        });
        rv_matches.setAdapter(adapter);
        tvCreate = (obu.ckt.cricket.comon.RegularTextView) view.findViewById(R.id.tv_createMatch_home);
        tvCreate.setOnClickListener(this);
        pref = SharePref.getInstance(activity);
        layer = DataLayer.getInstance(activity);
        user = layer.getUser(pref);
        db = new DatabaseHandler(activity);
        recycler();
    }

    private void recycler() {
        db.getMatches(user.userId, new MatchHistory() {
            @Override
            public void success(List<Match> matches) {
                matchList.clear();
                if (getArguments().getString(BUNDLE).equalsIgnoreCase("progress"))
                    for (Match match : matches) {
                        if (!match.result.equalsIgnoreCase("Completed")) {
                            matchList.add(match);
                        }
                    }
                else {
                    for (Match match : matches) {
                        if (match.result.equalsIgnoreCase("Completed")) {
                            matchList.add(match);
                        }
                    }
                }
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
                createMatchDialog(activity);
                break;
        }
    }

    public Dialog createMatchDialog(final Activity activity) {
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
        final Button btnCreate;
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
                    if (Utils.checkEmpty(et1stTeam) || Utils.checkEmpty(et2ndTeam) || Utils.checkEmpty(etOvers)) {
                        Snackbar.make(btnCreate, "enter all the fields", Snackbar.LENGTH_SHORT).show();
                    } else if (Utils.getText(et1stTeam).length() < 2 || Utils.getText(et2ndTeam).length() < 2) {
                        Snackbar.make(btnCreate, "Team names should be more than 3 characters", Snackbar.LENGTH_SHORT).show();
                    } else {
                        JSONObject obj = new JSONObject();
                        obj.put("toss", Utils.getText(etToss));
                        obj.put("1stBatting", Utils.getText(et1stTeam));
                        obj.put("overs", Utils.getText(etOvers));
                        match = new Match("", user.userId, Utils.getText(et1stTeam)
                                , Utils.getText(et2ndTeam), obj.toString(), "created");
                        db.insertMatch(match, -1, new CreateMatch() {
                            @Override
                            public void success(int matchId) {
                                dialog.dismiss();
                                Intent i = new Intent(activity, MatchActivity.class);
                                i.putExtra(Utils.EXTRA_MATCHE_ID, String.valueOf(matchId));
                                startActivity(i);
                                //recycler();
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
    public void onResume() {
        super.onResume();
        recycler();
    }
}
