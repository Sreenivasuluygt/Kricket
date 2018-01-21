package obu.ckt.cricket.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import obu.ckt.cricket.HomeActivity;
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
    private Button tvCreate, tvHistory;
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
        tvCreate = view.findViewById(R.id.tv_createMatch_home);
        tvHistory = view.findViewById(R.id.tv_History_home);
        rv_matches = (RecyclerView) view.findViewById(R.id.rv_matches_home);
        rv_matches.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new HomeAdapter(activity, new HomeAdapter.ItemClick() {
            @Override
            public void onClick(String matchId, String status) {
               /* if (!Utils.isNetworkAvailable(activity)) {*/
                if (false) {
                    Utils.singleAlertDialog(activity, "Your not connected to internet, please connect to internet and try again");
                } else {
                    Intent i;
                    if (status.equalsIgnoreCase("completed"))
                        i = new Intent(activity, MatchHistoryActivity.class);
                    else i = new Intent(activity, MatchActivity.class);
                    i.putExtra(Utils.EXTRA_MATCHE_ID, matchId);
                    startActivity(i);
                    activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }

            }
        });
        rv_matches.setAdapter(adapter);
        tvCreate.setOnClickListener(this);
        tvHistory.setOnClickListener(this);
        pref = SharePref.getInstance(activity);
        layer = DataLayer.getInstance(activity);
        user = layer.getUser(pref);
        db = new DatabaseHandler(activity);
        if (!getArguments().getString(BUNDLE).equalsIgnoreCase("progress")) {
            tvCreate.setVisibility(View.GONE);
            tvHistory.setVisibility(View.GONE);
            recycler();
        }
    }

    private void recycler() {
        final ProgressDialog pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Loading.");
        view.findViewById(R.id.tv_noMatches).setVisibility(View.GONE);
        pDialog.show();
        db.getMatches(user.userId, new MatchHistory() {
            @Override
            public void success(List<Match> matches) {
                matchList.clear();
                pDialog.dismiss();
               /* if (getArguments().getString(BUNDLE).equalsIgnoreCase("progress"))
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
                }*/
                if (matches.size() == 0)
                    view.findViewById(R.id.tv_noMatches).setVisibility(View.VISIBLE);
                matchList.addAll(matches);

                Collections.reverse(matchList);
                adapter.setMatchList(matchList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure() {
                pDialog.dismiss();
                view.findViewById(R.id.tv_noMatches).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        /*if (Utils.isNetworkAvailable(activity)) {*/
        if (true) {
            switch (view.getId()) {
                case R.id.tv_createMatch_home:
                    createMatchDialog(activity);
                    break;
                case R.id.tv_History_home:
                    ((HomeActivity) activity).addFragment(QuickFragment.newInstance("Completed"));
                    break;
            }
        } else {
            Utils.singleAlertDialog(activity, "Your not connected to internet, please connect to internet and try again");
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
                        match = new Match(null, user.userId, Utils.getText(et1stTeam)
                                , Utils.getText(et2ndTeam), obj.toString(), "created");
                        db.insertMatch(match, null, new CreateMatch() {
                            @Override
                            public void success(String matchId) {
                                dialog.dismiss();
                                ((HomeActivity) activity).addFragment(QuickFragment.newInstance("Completed"));
                            }

                            @Override
                            public void failure() {
                                dialog.dismiss();
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
       /* if (!getArguments().getString(BUNDLE).equalsIgnoreCase("progress")) {
            recycler();
        }*/
    }
}
