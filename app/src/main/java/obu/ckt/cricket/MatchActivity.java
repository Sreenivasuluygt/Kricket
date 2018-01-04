package obu.ckt.cricket;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import obu.ckt.cricket.adapters.MatchGridAdapter;
import obu.ckt.cricket.comon.SharePref;
import obu.ckt.cricket.comon.Utils;
import obu.ckt.cricket.data.DataLayer;
import obu.ckt.cricket.data.SelectPlayerDialog;
import obu.ckt.cricket.database.DatabaseHandler;
import obu.ckt.cricket.interfaces.CreateMatch;
import obu.ckt.cricket.model.Match;

public class MatchActivity extends AppCompatActivity implements View.OnClickListener {

    JSONObject inningsJson = new JSONObject();
    JSONObject matchJson = new JSONObject();
    SelectPlayerDialog dialog = new SelectPlayerDialog(MatchActivity.this);
    private TextView tvDot, tv1, tv2, tv3, tv4, tv5, tv6, tvNoBall, tvWide, tvBuys, tvLb, tvOut, tvRunOut, tvOverThrow;
    private TextView tvInning1, tvInnings2, tvB1, tvB2, tvB1Runs, tvB1Balls, tvB2Runs, tvB2Balls, tvB1s4s, tvB1s6s, tvB2s4s, tvB2s6s,
            tvChangeStriker, tvBowler, tvBowlerOvers, tvBowlerRuns, tvBowlerWickets, tvChangeBowler, tvScore, tvOvers, tvMatchHeading, tvDate, tvRunsAdded, tvThisOver;
    private Button btnOk, btnClear, btnUndo;
    private String regexStr = "^[0-9]*$";
    private DatabaseHandler db;
    private SharePref prefs;
    private DataLayer dl;
    private Match match;
    private String TAG = "MatchActivity";
    private boolean isDialogOpen = false;
    //["batsmen1", 0, 0, "notout", 1]}  refers[name,runs,balls,out,striking]


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        initControls();
        clickListeners();
        getData();
    }

    private void initControls() {
        db = new DatabaseHandler(this);
        dl = DataLayer.getInstance(this);
        prefs = SharePref.getInstance(this);
        tvDot = (TextView) findViewById(R.id.tv_dot_match);
        tv1 = (TextView) findViewById(R.id.tv_one_match);
        tv2 = (TextView) findViewById(R.id.tv_two_match);
        tv3 = (TextView) findViewById(R.id.tv_three_match);
        tv4 = (TextView) findViewById(R.id.tv_four_match);
        tv5 = (TextView) findViewById(R.id.tv_five_match);
        tv6 = (TextView) findViewById(R.id.tv_six_match);
        tvNoBall = (TextView) findViewById(R.id.tv_noball_match);
        tvWide = (TextView) findViewById(R.id.tv_wide_match);
        tvBuys = (TextView) findViewById(R.id.tv_byes_match);
        tvLb = (TextView) findViewById(R.id.tv_lbs_match);
        tvOut = (TextView) findViewById(R.id.tv_out_match);
        tvRunOut = (TextView) findViewById(R.id.tv_runOut_match);
        tvOverThrow = (TextView) findViewById(R.id.tv_overthrow_match);
        tvInning1 = (TextView) findViewById(R.id.tv_firstInnings_match);
        tvInnings2 = (TextView) findViewById(R.id.tv_secondInnings_match);
        tvB1 = (TextView) findViewById(R.id.tv_player1_match);
        tvB2 = (TextView) findViewById(R.id.tv_player2_match);
        tvB1Runs = (TextView) findViewById(R.id.tv_player1Runs_match);
        tvB1Balls = (TextView) findViewById(R.id.tv_player1Balls_match);
        tvB2Balls = (TextView) findViewById(R.id.tv_player2Balls_match);
        tvB2Runs = (TextView) findViewById(R.id.tv_player2Runs_match);

        tvB1s4s = (TextView) findViewById(R.id.tv_p1s4s_match);
        tvB1s6s = (TextView) findViewById(R.id.tv_p1s6s_match);
        tvB2s4s = (TextView) findViewById(R.id.tv_p2s4s_match);
        tvB2s6s = (TextView) findViewById(R.id.tv_p26s_match);

        tvChangeStriker = (TextView) findViewById(R.id.tv_changeStriker_match);
        tvBowler = (TextView) findViewById(R.id.tv_bowlerName_match);
        tvBowlerRuns = (TextView) findViewById(R.id.tv_bowlerRuns_match);
        tvBowlerWickets = (TextView) findViewById(R.id.tv_bowlerWickets_match);
        tvBowlerOvers = (TextView) findViewById(R.id.tv_bowlerOvers_match);
        tvChangeBowler = (TextView) findViewById(R.id.tv_changeBowler_match);
        tvScore = (TextView) findViewById(R.id.tv_score_match);
        tvOvers = (TextView) findViewById(R.id.tv_overs_match);
        tvMatchHeading = (TextView) findViewById(R.id.tv_matchhHeading_match);
        tvDate = (TextView) findViewById(R.id.tv_date_match);
        btnClear = (Button) findViewById(R.id.btn_clear_match);
        btnUndo = (Button) findViewById(R.id.btn_undo_match);
        btnOk = (Button) findViewById(R.id.btn_ok_match);
        tvRunsAdded = (TextView) findViewById(R.id.et_runsAdded_match);
        // tvThisOver = (TextView) findViewById(R.id.tv_thisOver_match);
    }

    private void clickListeners() {
        tvDot.setOnClickListener(this);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
        tv5.setOnClickListener(this);
        tv6.setOnClickListener(this);
        tvNoBall.setOnClickListener(this);
        tvWide.setOnClickListener(this);
        tvBuys.setOnClickListener(this);
        tvLb.setOnClickListener(this);
        tvOut.setOnClickListener(this);
        tvRunOut.setOnClickListener(this);
        tvOverThrow.setOnClickListener(this);
        tvB1.setOnClickListener(this);
        tvB2.setOnClickListener(this);
        tvChangeStriker.setOnClickListener(this);
        tvBowler.setOnClickListener(this);
        tvChangeBowler.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnUndo.setOnClickListener(this);
    }

    private void getData() {
        try {
            db.getMatchInfo(prefs.getValue(Utils.SHARED_USERID), getIntent().getStringExtra(Utils.EXTRA_MATCHE_ID), new DatabaseHandler.matchDetails() {
                @Override
                public void onSuccess(Match m) {
                    try {
                        match = m;
                        tvMatchHeading.setText(Utils.getTeamName(match.teamA) + " VS " + Utils.getTeamName(match.teamB));
                        tvDate.setText(Utils.getCurrentDate());
                        if (match.result.equalsIgnoreCase("created"))
                            matchJson = new JSONObject(getDataFromFile());
                        else matchJson = new JSONObject(match.json);
                        JSONObject dummy = new JSONObject(match.json);
                        matchJson.put("toss", dummy.getString("toss"));
                        matchJson.put("1stBatting", dummy.getString("1stBatting"));
                        matchJson.put("overs", dummy.getString("overs"));
                        loadData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure() {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getDataFromFile() {
        String text = "";
        try {
            InputStream is = getAssets().open("Json.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);

            text = new String(buffer, 0, size); //this line was missing

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    private void loadData() {
        try {
            switch (match.result.toLowerCase()) {
                case "created":
                    tvInning1.setText("1st Innings:" + Utils.getTeamName(matchJson.getString("1stBatting")));
                    //tvInnings2.setText("Toss:" + matchJson.getString("toss").toUpperCase().substring(0, 3));
                    inningsJson = matchJson.getJSONObject("1stInnings");
                    loadInning(inningsJson);
                    break;
                case "firstinnings":
                    tvInning1.setText("1st Innings:" + Utils.getTeamName(matchJson.getString("1stBatting")));
                    // tvInnings2.setText("Toss:" + matchJson.getString("1stBatting").toUpperCase().substring(0, 3));
                    inningsJson = matchJson.getJSONObject("1stInnings");
                    loadInning(inningsJson);
                    break;
                case "secondinnings":
                    if (match.teamA.equalsIgnoreCase(matchJson.getString("1stBatting"))) {
                        tvInning1.setText("2nd Innings:" + Utils.getTeamName(match.teamB));
                        tvInnings2.setText(Utils.getTeamName(match.teamA) + ":" + matchJson.getJSONObject("1stInnings").getString("score").toUpperCase());
                    } else {
                        tvInning1.setText("2nd Innings:" + Utils.getTeamName(match.teamA.toUpperCase()));
                        tvInnings2.setText(match.teamB.toUpperCase() + ":" + matchJson.getJSONObject("1stInnings").getString("score").toUpperCase());
                    }
                    inningsJson = matchJson.getJSONObject("2ndInnings");
                    loadInning(inningsJson);
                    break;
                case "completed":
                    if (match.teamA.equalsIgnoreCase(matchJson.getString("1stBatting"))) {
                        tvInning1.setText("2nd Innings:" + Utils.getTeamName(match.teamB));
                        tvInnings2.setText(Utils.getTeamName(match.teamA) + ":" + matchJson.getJSONObject("1stInnings").getString("score").toUpperCase());
                    } else {
                        tvInning1.setText("2nd Innings:" + Utils.getTeamName(match.teamA));
                        tvInnings2.setText(Utils.getTeamName(match.teamB) + ":" + matchJson.getJSONObject("1stInnings").getString("score").toUpperCase());
                    }
                    inningsJson = matchJson.getJSONObject("2ndInnings");
                    loadInning(inningsJson);
                    findViewById(R.id.runs_Layout_match).setVisibility(View.GONE);
                    btnUndo.setVisibility(View.GONE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadInning(JSONObject inningsJson) {
        try {
//Batsmen
            JSONArray dummy = inningsJson.getJSONArray("batsmen");
            boolean is1stBatsmenAdded = false;
            for (int i = 0; i < dummy.length(); i++) {
                JSONArray jArr = dummy.getJSONArray(i);
                if (jArr.get(3).equals("notout"))
                    if (!is1stBatsmenAdded) {
                        tvB1.setText(jArr.getString(0));
                        tvB1Runs.setText(String.valueOf(jArr.get(1)));
                        tvB1Balls.setText(String.valueOf(jArr.get(2)));
                        tvB1s4s.setText(String.valueOf(jArr.get(5)));
                        tvB1s6s.setText(String.valueOf(jArr.get(6)));
                        if (jArr.getInt(4) == Utils.JSON_STRIKING)
                            tvB1.append("*");
                        is1stBatsmenAdded = true;
                    } else {
                        tvB2.setText(jArr.getString(0));
                        tvB2Runs.setText(String.valueOf(jArr.get(1)));
                        tvB2s4s.setText(String.valueOf(jArr.get(5)));
                        tvB2s6s.setText(String.valueOf(jArr.get(6)));
                        tvB2Balls.setText(String.valueOf(jArr.get(2)));
                        if (jArr.get(4).equals(Utils.JSON_STRIKING))
                            tvB2.append("*");
                        break;
                    }
            }
            //Bowler
            dummy = inningsJson.getJSONArray("bowler");
            tvBowler.setText("");
            tvBowlerOvers.setText("");
            tvBowlerRuns.setText("");
            tvBowlerWickets.setText("");
            for (int i = 0; i < dummy.length(); i++) {
                JSONArray jArr = dummy.getJSONArray(i);
                if (jArr.get(3).equals(Utils.JSON_BOWLING)) {
                    tvBowler.setText(jArr.getString(0));
                    tvBowlerOvers.setText(jArr.getString(2));
                    tvBowlerRuns.setText(jArr.getString(1));
                    tvBowlerWickets.setText(jArr.getString(4));
                }

            }
            tvScore.setText(inningsJson.getString("score"));
            // tvThisOver.setText("This Over : " + inningsJson.getString("thisOver").replace("/", " "));
            addBallToLayout(inningsJson.getJSONArray("thisOver"));
            tvOvers.setText(inningsJson.getString("overs") + "(" + matchJson.getString("overs") + ")");
            if (!Utils.getText(tvB1).contains("*") && !Utils.getText(tvB2).contains("*") &&
                    !Utils.getText(tvScore).contains("/10")) {
                addStriker();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        if (!Utils.isNetworkAvailable(MatchActivity.this)) {
            Utils.singleAlertDialog(MatchActivity.this, "Your not connected to internet, please connect to internet and try again");
            return;
        }
        switch (v.getId()) {
            case R.id.tv_dot_match:
                setDataToEdiText("0");
                break;
            case R.id.tv_one_match:
                setDataToEdiText("1");
                break;
            case R.id.tv_two_match:
                setDataToEdiText("2");
                break;
            case R.id.tv_three_match:
                setDataToEdiText("3");
                break;
            case R.id.tv_four_match:
                setDataToEdiText("4");
                break;
            case R.id.tv_five_match:
                setDataToEdiText("5");
                break;
            case R.id.tv_six_match:
                setDataToEdiText("6");
                break;
            case R.id.tv_noball_match:
                setDataToEdiText("nb");
                break;
            case R.id.tv_wide_match:
                setDataToEdiText("wd");
                break;
            case R.id.tv_byes_match:
                setDataToEdiText("b");
                break;
            case R.id.tv_lbs_match:
                setDataToEdiText("lb");
                break;
            case R.id.tv_out_match:
                setDataToEdiText("out");
                break;
            case R.id.tv_runOut_match:
                setDataToEdiText("runout");
                break;
            case R.id.tv_overthrow_match:
                setDataToEdiText("Out");
                break;
            case R.id.tv_firstInnings_match:
                break;
            case R.id.tv_secondInnings_match:
                break;
            case R.id.tv_player1_match:
                changeBatsmenName(tvB1);
                break;
            case R.id.tv_player2_match:
                changeBatsmenName(tvB2);
                break;
            case R.id.tv_changeStriker_match:
                addStriker();
                break;
            case R.id.tv_bowlerName_match:
                changeBowlerName(tvBowler);
                break;
            case R.id.tv_changeBowler_match:
                changeBowler();
                break;
            case R.id.btn_clear_match:
                tvRunsAdded.setText("");
                break;
            case R.id.btn_ok_match:
                validateEditText();
                tvRunsAdded.setText("");
                break;
            case R.id.btn_undo_match:
                undoOperation();
                break;
        }
    }

    private void undoOperation() {
        try {
            if (inningsJson.getJSONArray("thisOver").length() > 0) {
                JSONArray arr = inningsJson.getJSONArray("thisOver");
                String deleteStr = arr.getString(arr.length() - 1);
                if (deleteStr.equalsIgnoreCase("w")) {
                    undoOut();
                } else if (deleteStr.equals("0")) {
                    undoDotBall();
                } else if (deleteStr.equals("1")) {
                    undoRun(1);
                } else if (deleteStr.equals("2")) {
                    undoRun(2);
                } else if (deleteStr.equals("3")) {
                    undoRun(3);
                } else if (deleteStr.equals("4")) {
                    undoRun(4);
                } else if (deleteStr.equals("5")) {
                    undoRun(5);
                } else if (deleteStr.equals("6")) {
                    undoRun(6);
                } else if (deleteStr.toLowerCase().contains("wd")) {
                    if (deleteStr.replace("wd", "").contains("w"))
                        undoRunOut(deleteStr);
                    else
                        undoWides(deleteStr);
                } else if (deleteStr.toLowerCase().contains("nb")) {
                    if (deleteStr.replace("nb", "").contains("w"))
                        undoRunOut(deleteStr);
                    else
                        undoNoBall(deleteStr);
                } else if (deleteStr.toLowerCase().contains("w")) {
                    undoRunOut(deleteStr);
                } else if (deleteStr.toLowerCase().contains("lb")) {
                    undoLegByes(deleteStr);
                } else if (deleteStr.toLowerCase().contains("b")) {
                    undoByes(deleteStr);
                }
                arr.remove(arr.length() - 1);
                if (match.result.toLowerCase().equalsIgnoreCase("FirstInnings") || match.result.toLowerCase().equalsIgnoreCase("Created")) {
                    matchJson.put("1stInnings", inningsJson);
                    match.json = matchJson.toString();
                    match.result = "FirstInnings";
                } else if (match.result.toLowerCase().equalsIgnoreCase("SecondInnings")) {
                    matchJson.put("2ndInnings", inningsJson);
                    match.json = matchJson.toString();
                    match.result = "SecondInnings";
                } else if (match.result.toLowerCase().equalsIgnoreCase("Completed")) {
                    matchJson.put("2ndInnings", inningsJson);
                    match.json = matchJson.toString();
                    match.result = "Completed";
                }
                db.insertMatch(match, match.matchId, new CreateMatch() {
                    @Override
                    public void success(String matchId) {

                    }

                    @Override
                    public void failure() {

                    }
                });
                getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void undoRunOut(String deleteStr) {
        try {
            StringBuilder result = new StringBuilder();
            String etData = deleteStr;
            undoBatsmen();
            if (deleteStr.length() > 0) {
                result.append(deleteStr.charAt(0));
                for (int i = 1; i < deleteStr.length(); i++) {
                    result.append("+");
                    result.append(deleteStr.charAt(i));
                }
                etData = result.toString();
            }
            if (etData.contains("+")) {
                String[] str = etData.split("\\+");
                if (etData.toLowerCase().contains("n+b"))
                    removeRunOutNoBall(deleteStr.replace("w", ""));
                else if (etData.contains("w+d"))
                    removeRunOutNoBall(deleteStr.replace("wd", "Wd").replace("w", "").replace("Wd", "wd"));
                else {
                    removeRunoutRun(Integer.parseInt(str[str.length - 1]));
                }
                if (str[str.length - 1].matches(regexStr))
                    removeRunoutBatsmen(1, Integer.parseInt(str[str.length - 1]));
                else
                    removeRunoutBatsmen(1, 0);
            } else {
                removeBallToBowler(1, 0);
                removeBallToOvers();
                removeRunoutBatsmen(1, 0);
            }
            //inningsJson.getJSONArray("batsmen").getJSONArray(position).put(3, "out");
            String[] str = inningsJson.getString("score").split("/");
            inningsJson.put("score", str[0] + "/" + String.valueOf(Integer.parseInt(str[1]) - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeRunoutBatsmen(int ball, int runs) {
        try {
            JSONArray batsmenArr = inningsJson.getJSONArray("batsmen");
            boolean isLastWicketStrike = false;
            int position = 0;
            JSONArray lastwicArr = inningsJson.getJSONArray("lastWicket");
            for (int j = 0; j < batsmenArr.length(); j++) {
                if (lastwicArr.getString(lastwicArr.length() - 1).equals(batsmenArr.getJSONArray(j).getString(0))) {
                    position = j;
                    isLastWicketStrike = batsmenArr.getJSONArray(j).length() == 8;
                    lastwicArr.remove(lastwicArr.length() - 1);
                    break;
                }
            }
            if (isLastWicketStrike)
                for (int j = 0; j < batsmenArr.length(); j++) {
                    if (j != position)
                        batsmenArr.getJSONArray(j).put(4, 0);
                    else {
                        batsmenArr.getJSONArray(j).put(4, 1);
                        batsmenArr.getJSONArray(j).put(3, "notout");
                    }
                }
            else batsmenArr.getJSONArray(position).put(3, "notout");

            for (int i = 0; i < batsmenArr.length(); i++) {
                JSONArray batArr = batsmenArr.getJSONArray(i);
                if (batArr.get(3).equals("notout")) {
                    if (batArr.get(4).equals(Utils.JSON_STRIKING)) {
                        if (runs == 6) {
                            batArr.put(6, batArr.getInt(6) - 1);
                        } else if (runs == 4)
                            batArr.put(5, batArr.getInt(5) - 1);
                        if (batArr.getInt(1) > 0)
                            batArr.put(1, batArr.getInt(1) - runs);
                        if (batArr.getInt(2) > 0)
                            batArr.put(2, batArr.getInt(2) - ball);
                        break;
                    }
                    batsmenArr.put(i, batArr);
                }
            }
            inningsJson.put("batsmen", batsmenArr);
            Log.e(TAG, matchJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void undoBatsmen() {
        try {
            JSONArray arr = inningsJson.getJSONArray("batsmen");
            JSONArray lastArr = inningsJson.getJSONArray("lastWicket");
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONArray(i).getString(1).equalsIgnoreCase(lastArr.getString(lastArr.length() - 1))) {
                    arr.getJSONArray(i).put(3, "notout");
                    if (arr.getJSONArray(i).getString(7).equalsIgnoreCase("strike")) {
                        for (int j = 0; j < arr.length(); j++) {
                            arr.getJSONArray(j).put(4, 0);
                        }
                        arr.getJSONArray(i).put(4, 1);
                    }
                    break;
                }
            }
            inningsJson.put("batsmen", arr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void removeRunoutRun(int run) {
        removeBallToBowler(1, run);
        removeBallToOvers();
        removeRunToScore(run);
    }

    private void removeRun(int run) {
        removeBallToBowler(1, run);
        removeBallToBatmen(1, run);
        removeBallToOvers();
        removeRunToScore(run);
    }

    private void removeRunToScore(int runs) {
        try {
            String[] str = inningsJson.getString("score").split("/");
            int score = Integer.parseInt(str[0]);
            inningsJson.put("score", score - runs + "/" + str[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeNoBall(String deleteStr) {
        StringBuilder result = new StringBuilder();
        deleteStr = deleteStr.replace("nb", "");
        String etData = deleteStr;
        if (deleteStr.length() > 0) {
            result.append(deleteStr.charAt(0));
            for (int i = 1; i < deleteStr.length(); i++) {
                result.append("+");
                result.append(deleteStr.charAt(i));
            }
            etData = result.toString();
        }
        etData = etData.length() == 0 ? "0" : etData;
        etData = etData.contains("l") ? etData.replace("+b", "").replace("l", "lb") : etData;

        if (etData.contains("+")) {
            String[] str = etData.split("\\+");
            int i = Integer.parseInt(str[str.length - 1]);
            if (etData.contains("+b") || (etData.contains("lb"))) {
                if ((i % 2) != 0) {
                    changeStriker();
                }
                removeRunToScore(1 + i);
                removeBallToBatmen(1, 0);
            } else {
                if ((i % 2) != 0) {
                    changeStriker();
                }
                removeBallToBatmen(1, i);
                removeRunToScore(1 + i);
            }
            removeBallToBowler(0, 1 + i);
        } else if (etData.isEmpty()) {
            removeRunToScore(1);
            removeBallToBowler(0, 1);
            removeBallToBatmen(1, 0);
        } else {
            removeRunToScore(1 + Integer.parseInt(etData));
            removeBallToBowler(0, 1 + Integer.parseInt(etData));
            removeBallToBatmen(1, Integer.parseInt(etData));
        }
    }

    private void removeRunOutNoBall(String deleteStr) {
        StringBuilder result = new StringBuilder();
        deleteStr = deleteStr.replace("nb", "");
        String etData = deleteStr;
        if (deleteStr.length() > 0) {
            result.append(deleteStr.charAt(0));
            for (int i = 1; i < deleteStr.length(); i++) {
                result.append("+");
                result.append(deleteStr.charAt(i));
            }
            etData = result.toString();
        }
        etData = etData.length() == 0 ? "0" : etData;
        etData = etData.contains("l") ? etData.replace("+b", "").replace("l", "lb") : etData;

        if (etData.contains("+")) {
            String[] str = etData.split("\\+");
            int i = Integer.parseInt(str[str.length - 1]);
            if (etData.contains("+b") || (etData.contains("lb"))) {
                if ((i % 2) != 0) {
                    changeStriker();
                }
                removeRunToScore(1 + i);
            } else {
                if ((i % 2) != 0) {
                    changeStriker();
                }
                removeRunToScore(1 + i);
            }
            removeBallToBowler(0, 1 + i);
        } else if (etData.isEmpty()) {
            removeRunToScore(1);
            removeBallToBowler(0, 1);
        } else {
            removeRunToScore(1 + Integer.parseInt(etData));
            removeBallToBowler(0, 1 + Integer.parseInt(etData));
        }
    }

    private void undoByes(String deleteStr) {
        StringBuilder result = new StringBuilder();
        String data = deleteStr;
        if (deleteStr.length() > 0) {
            result.append(deleteStr.charAt(0));
            for (int i = 1; i < deleteStr.length(); i++) {
                result.append("+");
                result.append(deleteStr.charAt(i));
            }
            data = result.toString();


            String[] str = data.split("\\+");
            int i = Integer.parseInt(str[str.length - 1]);
            if ((i % 2) != 0) {
                changeStriker();
            }
            removeRunToScore(i);
            removeBallToOvers();
            removeBallToBatmen(1, 0);
            removeBallToBowler(1, i);
        }
    }

    private void undoLegByes(String deleteStr) {
        StringBuilder result = new StringBuilder();
        String data = deleteStr;
        if (deleteStr.length() > 0) {
            result.append(deleteStr.charAt(0));
            for (int i = 1; i < deleteStr.length(); i++) {
                result.append("+");
                result.append(deleteStr.charAt(i));
            }
            data = result.toString();
        }


        String[] str = data.split("\\+");
        int i = Integer.parseInt(str[str.length - 1]);
        if ((i % 2) != 0) {
            changeStriker();
        }
        removeRunToScore(i);
        removeBallToOvers();
        removeBallToBatmen(1, 0);
        removeBallToBowler(1, i);
    }

    private void undoNoBall(String deleteStr) {
        removeNoBall(deleteStr);
    }

    private void undoWides(String deleteStr) {
        StringBuilder result = new StringBuilder();
        String data = deleteStr;
        if (deleteStr.length() > 0 && !data.equalsIgnoreCase("wd")) {
            result.append(deleteStr.charAt(0));
            for (int i = 1; i < deleteStr.length(); i++) {
                result.append("+");
                result.append(deleteStr.charAt(i));
            }
            data = result.toString();
        }


        if (data.contains("+")) {
            String[] str = data.split("\\+");
            if (data.toLowerCase().contains("b")) {
                int i = Integer.parseInt(str[str.length - 1]);
                if ((i % 2) != 0) {
                    changeStriker();
                }
                removeBallToBowler(0, 1 + Integer.parseInt(str[str.length - 1]));
                removeRunToScore(1 + Integer.parseInt(str[str.length - 1]));
            } else {
                int i = Integer.parseInt(str[str.length - 1]);
                if ((i % 2) != 0) {
                    changeStriker();
                }
                removeBallToBowler(0, 1 + Integer.parseInt(str[str.length - 1]));
                removeRunToScore(1 + Integer.parseInt(str[str.length - 1]));
            }

        } else {
            removeRunToScore(1);
        }
    }

    private void undoDotBall() {
        removeBallToBowler(1, 0);
        removeBallToBatmen(1, 0);
        removeBallToOvers();
    }

    private void undoOut() {
        try {
            removeBallToBowler(1, 0);
            removeBallToOvers();
            strikerNotOut();
            removeBallToBatmen(1, 0);
            removeWickerToBowler();
            String[] str = inningsJson.getString("score").split("/");
            inningsJson.put("score", str[0] + "/" + String.valueOf(Integer.parseInt(str[1]) - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeBallToBatmen(int ball, int runs) {
        try {
            JSONArray batsmenArr = inningsJson.getJSONArray("batsmen");
            boolean isEven = false, isStrikerChecked = false;
            for (int i = 0; i < batsmenArr.length(); i++) {
                JSONArray batArr = batsmenArr.getJSONArray(i);
                if (runs == 0) isEven = true;
                else if ((runs % 2) == 0) isEven = true;
                if (!isEven && !isStrikerChecked) {
                    changeStriker();
                    isStrikerChecked = true;
                }
                if (batArr.get(3).equals("notout")) {
                    if (batArr.get(4).equals(Utils.JSON_STRIKING)) {
                        if (runs == 6) {
                            batArr.put(6, batArr.getInt(6) - 1);
                        } else if (runs == 4)
                            batArr.put(5, batArr.getInt(5) - 1);
                        if (batArr.getInt(1) > 0)
                            batArr.put(1, batArr.getInt(1) - runs);
                        if (batArr.getInt(2) > 0)
                            batArr.put(2, batArr.getInt(2) - ball);
                    }
                    batsmenArr.put(i, batArr);
                }
            }
            inningsJson.put("batsmen", batsmenArr);
            Log.e(TAG, matchJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void strikerNotOut() {
        try {
            JSONArray batsmenArr = inningsJson.getJSONArray("batsmen");
            JSONArray lastWicket = inningsJson.getJSONArray("lastWicket");
            for (int i = 0; i < batsmenArr.length(); i++) {
                JSONArray batArr = batsmenArr.getJSONArray(i);
                if (batArr.get(3).equals("out") && batArr.getString(0).equalsIgnoreCase(lastWicket.getString(lastWicket.length() - 1))) {
                    batArr.put(4, 1);
                    batArr.put(3, "notout");
                    lastWicket.remove(lastWicket.length() - 1);
                    break;
                } else {
                    batArr.put(4, 0);
                }
                batsmenArr.put(i, batArr);

            }
            inningsJson.put("batsmen", batsmenArr);
            inningsJson.put("lastWicket", lastWicket);
            Log.e(TAG, matchJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void undoRun(int i) {
        removeRun(i);
    }

    private void removeBallToBowler(int balls, int runs) {
        try {
            JSONArray bowlersArr = inningsJson.getJSONArray("bowler");
            for (int i = 0; i < bowlersArr.length(); i++) {
                JSONArray bowArr = bowlersArr.getJSONArray(i);
                if (bowArr.get(3).equals(Utils.JSON_BOWLING)) {
                    double o = bowArr.getDouble(2);
                    if (balls == 1) {
                        o = o - 0.1;
                    }
                    o = Double.parseDouble(String.format("%.1f", o));
                    if (String.valueOf(o).contains(".6")) {
                        o = o + .4;//it will compete over
                    }
                    bowArr.put(2, o);
                    bowArr.put(1, bowArr.getInt(1) - runs);
                    bowlersArr.put(i, bowArr);
                }
            }
            inningsJson.put("bowler", bowlersArr);
            Log.e(TAG, matchJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void removeWickerToBowler() {
        try {
            JSONArray bowlersArr = inningsJson.getJSONArray("bowler");
            for (int i = 0; i < bowlersArr.length(); i++) {
                JSONArray bowArr = bowlersArr.getJSONArray(i);
                if (bowArr.get(3).equals(Utils.JSON_BOWLING)) {
                    bowArr.put(4, bowArr.getInt(4) - 1);
                    bowlersArr.put(i, bowArr);
                }
            }
            inningsJson.put("bowler", bowlersArr);
            Log.e(TAG, matchJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void removeBallToOvers() {
        try {
            double o = inningsJson.getDouble("overs") - .1;
            o = Double.parseDouble(String.format("%.1f", o));
            if (String.valueOf(o).contains(".6")) {
                o = o + .4;//it will compete over
            }
            inningsJson.put("overs", o);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void changeBatsmenName(final TextView tv) {
        dialog.changePlayerName("Change player name?", tv.getText().toString().replace("*", ""), new SelectPlayerDialog.PlayerName() {
            @Override
            public void changed(String name) {
                try {
                    JSONArray bat = inningsJson.getJSONArray("batsmen");
                    for (int i = 0; i < bat.length(); i++) {
                        JSONArray arr = bat.getJSONArray(i);
                        if (tv.getText().toString().replace("*", "").equals(arr.get(0))) {
                            arr.put(0, name);
                            break;
                        }
                        bat.put(i, arr);
                    }
                    inningsJson.put("batsmen", bat);
                    checkMatchStatus("");
                    getData();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changeBowlerName(final TextView tv) {
        dialog.changePlayerName("Change player name?", tv.getText().toString().replace("*", ""), new SelectPlayerDialog.PlayerName() {
            @Override
            public void changed(String name) {
                try {
                    JSONArray bat = inningsJson.getJSONArray("bowler");
                    for (int i = 0; i < bat.length(); i++) {
                        JSONArray arr = bat.getJSONArray(i);
                        if (tv.getText().toString().replace("*", "").equals(arr.get(0))) {
                            arr.put(0, name);
                            break;
                        }
                        bat.put(i, arr);
                    }
                    inningsJson.put("bowler", bat);
                    checkMatchStatus("");
                    getData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changeBowler() {
        dialog.selectBowler(inningsJson, "Select bowler to bowl", new SelectPlayerDialog.OnSelected() {
            @Override
            public void selected(int pos) {
                try {
                    inningsJson.getJSONArray("bowler").getJSONArray(pos).put(3, 1);
                    inningsJson.put("lastBowled", tvBowler.getText().toString());
                    loadInning(inningsJson);
                    Log.e("", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addStriker() {
        //Some times too many dialogs gets Open
        if (!isDialogOpen) {
            isDialogOpen = true;
            dialog.selectPlayer(inningsJson, "Select Who'se on strike?", new SelectPlayerDialog.OnSelected() {
                @Override
                public void selected(int position) {
                    //inningsJson = innings;
                    try {
                        JSONArray jArr = inningsJson.getJSONArray("batsmen");
                        //removing all from striker
                        for (int i = 0; i < jArr.length(); i++)
                            jArr.getJSONArray(i).put(4, 0);
                        inningsJson.put("batsmen", jArr);
                        inningsJson.getJSONArray("batsmen").getJSONArray(position).put(4, 1);
                        loadInning(inningsJson);
                        isDialogOpen = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void validateEditText() {
        String etData = tvRunsAdded.getText().toString();
        String batmen1 = Utils.getText(tvB1);
        String batmen2 = Utils.getText(tvB2);
        int charCount = 0; //resetting character count
        for (char ch : etData.toCharArray()) {
            if (ch == '+') {
                charCount++;
            }
        }
        if (etData.isEmpty()) {
            Snackbar.make(btnOk, "Select runs to add", Snackbar.LENGTH_SHORT).show();
        } else if (!batmen1.contains("*") && !batmen2.contains("*")) {
            Snackbar.make(btnOk, "Select striker", Snackbar.LENGTH_SHORT).show();
        } else if (!checkBowlerToBowl()) {
            Snackbar.make(btnOk, "Select bowler to bowl", Snackbar.LENGTH_SHORT).show();
        }
        /*else if (charCount > 0) {
            Snackbar.make(btnOk, "Enter correct data", Snackbar.LENGTH_SHORT).show();
        }*/
        else if (etData.equalsIgnoreCase("lb"))
            Snackbar.make(btnOk, "Enter Leg Bye runs", Snackbar.LENGTH_SHORT).show();
        else if (etData.equalsIgnoreCase("b"))
            Snackbar.make(btnOk, "Enter bye runs", Snackbar.LENGTH_SHORT).show();
        else if (etData.equalsIgnoreCase("wd+b"))
            Snackbar.make(btnOk, "Enter bye runs", Snackbar.LENGTH_SHORT).show();
        else if (etData.equalsIgnoreCase("wd+lb"))
            Snackbar.make(btnOk, "Enter Leg Bye runs", Snackbar.LENGTH_SHORT).show();
        else if (etData.equalsIgnoreCase("nb+lb"))
            Snackbar.make(btnOk, "Enter Leg Bye runs", Snackbar.LENGTH_SHORT).show();
        else if (etData.equalsIgnoreCase("nb+b"))
            Snackbar.make(btnOk, "Enter Bye runs", Snackbar.LENGTH_SHORT).show();
        else if (etData.equalsIgnoreCase("nb+b+runout") || etData.equalsIgnoreCase("nb+lb+runout") ||
                etData.equalsIgnoreCase("wd+b+runout") || etData.equalsIgnoreCase("wd+lb+runout") ||
                etData.equalsIgnoreCase("b+runout") || etData.equalsIgnoreCase("lb+runout"))
            Snackbar.make(btnOk, "Enter Bye runs", Snackbar.LENGTH_SHORT).show();
        else {
            if (etData.equalsIgnoreCase("out")) {
                addOut(etData);
            } else if (etData.contains("runout")) {
                addRunOut(etData);
            } else if (etData.equals("0")) {
                addDotBall();
            } else if (etData.equals("1")) {
                addRun(1);
            } else if (etData.equals("2")) {
                addRun(2);
            } else if (etData.equals("3")) {
                addRun(3);
            } else if (etData.equals("4")) {
                addRun(4);
            } else if (etData.equals("5")) {
                addRun(5);
            } else if (etData.equals("6")) {
                addRun(6);
            } else if (etData.toLowerCase().contains("wd")) {
                addWides(etData);
            } else if (etData.toLowerCase().contains("nb")) {
                addNoBall(etData);
            } else if (etData.toLowerCase().contains("lb")) {
                addLegByes(etData);
            } else if (etData.toLowerCase().contains("b")) {
                addByes(etData);
            }
            if (!etData.toLowerCase().contains("out")) {
                addThisOversRuns(etData);
                checkMatchStatus(etData);
                getData();
            }
        }
    }

    private void addThisOversRuns(String etData) {
        try {
            inningsJson.getJSONArray("thisOver").put(etData.replace("+", "").replace("runout", "w")
                    .replace("out", "w"));
            // inningsJson.put("thisOver", str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkBowlerToBowl() {
        tvChangeBowler.setEnabled(true);
        boolean isBool = false;
        try {
            JSONArray arr = inningsJson.getJSONArray("bowler");
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONArray(i).get(3).equals(Utils.JSON_BOWLING)) {
                    isBool = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isBool;
    }


    private void checkMatchStatus(String etData) {
        try {
            if (!etData.contains("wd") || !etData.contains("nb")) {
                //Checking overs completed or not
                if (inningsJson.getDouble("overs") >= matchJson.getDouble("overs")) {
                    if (match.result.toLowerCase().equalsIgnoreCase("SecondInnings")) {
                        if (Integer.parseInt(matchJson.getJSONObject("1stInnings").getString("score").split("/")[0])
                                < Integer.parseInt(inningsJson.getString("score").split("/")[0])) {
                            if (match.teamA.equalsIgnoreCase(matchJson.getString("1stBatting")))
                                matchJson.put("won", match.teamB);
                            else
                                matchJson.put("won", match.teamA);
                        } else if (Integer.parseInt(matchJson.getJSONObject("1stInnings").getString("score").split("/")[0])
                                == Integer.parseInt(inningsJson.getString("score").split("/")[0])) {
                            matchJson.put("won", "tie");
                        } else {
                            matchJson.put("won", matchJson.getString("1stBatting"));
                        }
                        matchJson.put("2ndInnings", inningsJson);
                        matchJson.put("date", Utils.getCurrentDate());
                        match.json = matchJson.toString();
                        match.result = "Completed";
                        //Utils.singleAlertDialog(MatchActivity.this, "Match completed");
                        Utils.congratulations(MatchActivity.this, matchJson.getString("won"));
                    } else {
                        matchJson.put("1stInnings", inningsJson);
                        match.json = matchJson.toString();
                        match.result = "SecondInnings";
                        Utils.singleAlertDialog(MatchActivity.this, "First innings completed");
                    }
                    db.insertMatch(match, match.matchId, new CreateMatch() {
                        @Override
                        public void success(String matchId) {

                        }

                        @Override
                        public void failure() {

                        }
                    });
                    return;
                } else if (match.result.equals("SecondInnings") && Integer.parseInt(matchJson.getJSONObject("1stInnings").getString("score").split("/")[0])
                        < Integer.parseInt(inningsJson.getString("score").split("/")[0])) {
                    matchJson.put("2ndInnings", inningsJson);
                    if (match.teamA.equalsIgnoreCase(matchJson.getString("1stBatting")))
                        matchJson.put("won", match.teamB);
                    else
                        matchJson.put("won", match.teamA);

                    matchJson.put("date", Utils.getCurrentDate());
                    match.json = matchJson.toString();
                    match.result = "Completed";
                    //Utils.singleAlertDialog(MatchActivity.this, "Match completed");
                    Utils.congratulations(MatchActivity.this, matchJson.getString("won"));
                    db.insertMatch(match, match.matchId, new CreateMatch() {
                        @Override
                        public void success(String matchId) {

                        }

                        @Override
                        public void failure() {

                        }
                    });
                    return;

                } else if (Integer.parseInt(inningsJson.getString("score").split("/")[1]) >= 10) {
                    if (match.result.toLowerCase().equalsIgnoreCase("SecondInnings")) {
                        if (Integer.parseInt(matchJson.getJSONObject("1stInnings").getString("score").split("/")[0])
                                < Integer.parseInt(inningsJson.getString("score").split("/")[0])) {
                            if (match.teamA.equalsIgnoreCase(matchJson.getString("1stBatting")))
                                matchJson.put("won", match.teamB);
                            else
                                matchJson.put("won", match.teamA);
                        } else if (Integer.parseInt(matchJson.getJSONObject("1stInnings").getString("score").split("/")[0])
                                == Integer.parseInt(inningsJson.getString("score").split("/")[0])) {
                            matchJson.put("won", "tie");
                        } else {
                            matchJson.put("won", matchJson.getString("1stBatting"));
                        }
                        matchJson.put("2ndInnings", inningsJson);
                        matchJson.put("date", Utils.getCurrentDate());
                        match.json = matchJson.toString();
                        match.result = "Completed";
                        Utils.congratulations(MatchActivity.this, matchJson.getString("won"));
                    } else {
                        inningsJson.put("thisOver", new JSONArray());
                        matchJson.put("1stInnings", inningsJson);
                        match.json = matchJson.toString();
                        match.result = "SecondInnings";
                        Utils.singleAlertDialog(MatchActivity.this, "First innings completed");
                    }
                    db.insertMatch(match, match.matchId, new CreateMatch() {
                        @Override
                        public void success(String matchId) {

                        }

                        @Override
                        public void failure() {

                        }
                    });
                    return;
                }
                if (inningsJson.getString("overs").contains(".0")) {
                    inningsJson.put("lastBowled", tvBowler.getText().toString());
                    changeStriker();
                    changeBowlerStatus();
                    inningsJson.put("thisOver", new JSONArray());
                }
                if (match.result.toLowerCase().equalsIgnoreCase("FirstInnings") || match.result.toLowerCase().equalsIgnoreCase("Created")) {
                    matchJson.put("1stInnings", inningsJson);
                    match.json = matchJson.toString();
                    match.result = "FirstInnings";
                } else if (match.result.toLowerCase().equalsIgnoreCase("SecondInnings")) {
                    matchJson.put("2ndInnings", inningsJson);
                    match.json = matchJson.toString();
                    match.result = "SecondInnings";
                } else if (match.result.toLowerCase().equalsIgnoreCase("Completed")) {
                    matchJson.put("2ndInnings", inningsJson);
                    match.json = matchJson.toString();
                    match.result = "Completed";
                }
                db.insertMatch(match, match.matchId, new CreateMatch() {
                    @Override
                    public void success(String matchId) {

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

    private void changeBowlerStatus() {
        try {
            tvChangeBowler.setEnabled(true);
            JSONArray bowlersArr = inningsJson.getJSONArray("bowler");
            int bowledLast = 0;
            for (int i = 0; i < bowlersArr.length(); i++) {
                JSONArray jArr = bowlersArr.getJSONArray(i);
                if (jArr.get(0).equals(inningsJson.getString("lastBowled"))) {
                    jArr.put(3, 0);
                    bowlersArr.put(i, jArr);
                    bowledLast = i;
                    break;
                }

                /* JSONArray jArr = bowlersArr.getJSONArray(i);
                if (jArr.get(0).equals(inningsJson.getString("lastBowled"))) {
                    jArr.put(3, 0);
                } else if (!addedCurrentBowler) {
                    jArr.put(3, 1);
                    addedCurrentBowler = true;
                }
                bowlersArr.put(i, jArr);*/
            }

            for (int i = 0; i < bowlersArr.length(); i++) {
                bowlersArr.getJSONArray(i).put(3, 0);
            }

            if (bowledLast != 10) {
                bowlersArr.getJSONArray(bowledLast + 1).put(3, 1);
            } else {
                bowlersArr.getJSONArray(0).put(3, 1);
            }
            inningsJson.put("bowler", bowlersArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addOut(final String etData) {
        try {
            addBallToBowler(1, 0);
            addBallToOvers();
            addBallToBatmen(1, 0);
            strikerOut();
            addWickerToBowler();
            String[] str = inningsJson.getString("score").split("/");
            inningsJson.put("score", str[0] + "/" + String.valueOf(Integer.parseInt(str[1]) + 1));
            addThisOversRuns(etData);
            checkMatchStatus(etData);
            getData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void strikerOut() {
        try {
            JSONArray batsmenArr = inningsJson.getJSONArray("batsmen");
            for (int i = 0; i < batsmenArr.length(); i++) {
                JSONArray batArr = batsmenArr.getJSONArray(i);
                if (batArr.get(3).equals("notout")) {
                    if (batArr.get(4).equals(Utils.JSON_STRIKING)) {
                        batArr.put(3, "out");
                        batsmenArr.put(i, batArr);
                        inningsJson.getJSONArray("lastWicket").put(batArr.getString(0));
                        break;
                    }
                }
            }
            inningsJson.put("batsmen", batsmenArr);
            Log.e(TAG, matchJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void addRunOut(final String etData) {

        dialog.selectPlayer(inningsJson, "Select Who'se out?", new SelectPlayerDialog.OnSelected() {
            @Override
            public void selected(int position) {
                //inningsJson = innings;
                try {
                    if (etData.contains("+")) {
                        String[] str = etData.split("\\+");
                        if (etData.toLowerCase().contains("nb"))
                            addNoBall(etData.toLowerCase().replace("+runout", ""));
                        else if (etData.contains("wd"))
                            addNoBall(etData.toLowerCase().replace("+runout", ""));
                        else addRunOutRuns(Integer.parseInt(str[str.length - 1]));
                    } else {
                        addBallToBowler(1, 0);
                        addBallToOvers();
                        addBallToBatmen(1, 0);
                    }
                    if (inningsJson.getJSONArray("batsmen").getJSONArray(position).getInt(4) == Utils.JSON_STRIKING)
                        inningsJson.getJSONArray("batsmen").getJSONArray(position).put(7, "strike");
                    inningsJson.getJSONArray("batsmen").getJSONArray(position).put(3, "out");
                    inningsJson.getJSONArray("lastWicket").put(inningsJson.getJSONArray("batsmen").getJSONArray(position).getString(0));
                    String[] str = inningsJson.getString("score").split("/");
                    inningsJson.put("score", str[0] + "/" + String.valueOf(Integer.parseInt(str[1]) + 1));
                    addThisOversRuns(etData);
                    checkMatchStatus(etData);
                    getData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void addLegByes(String etData) {
        String[] str = etData.split("\\+");
        int i = Integer.parseInt(str[str.length - 1]);
        addRunToScore(i);
        addBallToOvers();
        addBallToBatmen(1, 0);
        addBallToBowler(1, i);
        if ((i % 2) != 0) {
            changeStriker();
        }
    }

    private void addByes(String etData) {
        String[] str = etData.split("\\+");
        int i = Integer.parseInt(str[str.length - 1]);
        addRunToScore(i);
        addBallToOvers();
        addBallToBatmen(1, 0);
        addBallToBowler(1, i);
        if ((i % 2) != 0) {
            changeStriker();
        }
    }


    private void addNoBall(String etData) {
        if (etData.contains("+")) {
            String[] str = etData.split("\\+");
            int i = 0;
            if (String.valueOf(Integer.parseInt(str[str.length - 1])).matches(regexStr))
                i = Integer.parseInt(str[str.length - 1]);
            if (etData.contains("+b") || (etData.contains("lb"))) {
                addRunToScore(1 + i);
                addBallToBatmen(1, i);
                if ((i % 2) != 0) {
                    changeStriker();
                }
            } else {
                addBallToBatmen(1, i);
                addRunToScore(1 + i);
            }
            addBallToBowler(0, 1 + i);
        } else {
            addRunToScore(1);
            addBallToBowler(0, 1);
            addBallToBatmen(1, 0);
        }
    }

    private void addWides(String data) {
        if (data.contains("+")) {
            String[] str = data.split("\\+");
            if (data.toLowerCase().contains("b")) {
                int i = Integer.parseInt(str[str.length - 1]);
                if ((i % 2) != 0) {
                    changeStriker();
                }
                addBallToBowler(0, 1 + Integer.parseInt(str[str.length - 1]));
                addRunToScore(1 + Integer.parseInt(str[str.length - 1]));
            } else {
                int i = Integer.parseInt(str[str.length - 1]);
                if ((i % 2) != 0) {
                    changeStriker();
                }
                addBallToBowler(0, 1 + Integer.parseInt(str[str.length - 1]));
                addRunToScore(1 + Integer.parseInt(str[str.length - 1]));
            }

        } else {
            addRunToScore(1);
        }
    }

    private void changeStriker() {
        try {
            JSONArray batsmenArr = inningsJson.getJSONArray("batsmen");
            boolean isStrikerChecked = false, isNonStrikerChecked = false;
            for (int i = 0; i < batsmenArr.length(); i++) {
                JSONArray batArr = batsmenArr.getJSONArray(i);
                if (batArr.get(3).equals("notout")) {
                    if (batArr.get(4).equals(Utils.JSON_STRIKING)) {
                        batArr.put(4, 0);
                        isStrikerChecked = true;
                    } else if (!isNonStrikerChecked) {
                        batArr.put(4, 1);
                        isNonStrikerChecked = true;
                    }
                    batsmenArr.put(i, batArr);
                    if (isStrikerChecked && isNonStrikerChecked)
                        break;
                }
            }
            inningsJson.put("batsmen", batsmenArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addRun(int run) {
        addBallToBowler(1, run);
        addBallToBatmen(1, run);
        addBallToOvers();
        addRunToScore(run);
    }

    private void addRunOutRuns(int run) {
        addBallToBowler(1, run);
        addRunoutBallToBatmen(1, run);
        addBallToOvers();
        addRunToScore(run);
    }

    private void addDotBall() {
        //Add ball to bowler bowler with 0 runs conceeded
        addBallToBowler(1, 0);
        addBallToBatmen(1, 0);
        addBallToOvers();
    }

    private void addRunToScore(int runs) {
        try {
            String[] str = inningsJson.getString("score").split("/");
            int score = Integer.parseInt(str[0]);
            inningsJson.put("score", score + runs + "/" + str[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addBallToOvers() {
        try {
            double o = inningsJson.getDouble("overs") + .1;
            o = Double.parseDouble(String.format("%.1f", o));
            if (String.valueOf(o).contains(".6")) {
                o = o + .4;//it will compete over
            }
            inningsJson.put("overs", o);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addBallToBatmen(int ball, int runs) {
        try {
            JSONArray batsmenArr = inningsJson.getJSONArray("batsmen");
            boolean isEven = false, isStrikerChecked = false, isNonStrikerChecked = false;
            for (int i = 0; i < batsmenArr.length(); i++) {
                JSONArray batArr = batsmenArr.getJSONArray(i);
                if (batArr.get(3).equals("notout")) {
                    if (runs == 0) isEven = true;
                    else if ((runs % 2) == 0) isEven = true;
                    if (batArr.get(4).equals(Utils.JSON_STRIKING)) {
                        if (runs == 6) {
                            batArr.put(6, batArr.getInt(6) + 1);
                        } else if (runs == 4)
                            batArr.put(5, batArr.getInt(5) + 1);
                        batArr.put(1, batArr.getInt(1) + runs);
                        batArr.put(2, batArr.getInt(2) + ball);
                        isStrikerChecked = true;
                        if (!isEven)
                            batArr.put(4, 0);
                    } else {
                        isNonStrikerChecked = true;
                        if (!isEven)
                            batArr.put(4, 1);
                    }
                    batsmenArr.put(i, batArr);
                    if (isNonStrikerChecked && isStrikerChecked)
                        break;
                }
            }
            inningsJson.put("batsmen", batsmenArr);
            Log.e(TAG, matchJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addRunoutBallToBatmen(int ball, int runs) {
        try {
            JSONArray batsmenArr = inningsJson.getJSONArray("batsmen");
            for (int i = 0; i < batsmenArr.length(); i++) {
                JSONArray batArr = batsmenArr.getJSONArray(i);
                if (batArr.get(3).equals("notout")) {
                    if (batArr.get(4).equals(Utils.JSON_STRIKING)) {
                        if (runs == 6) {
                            batArr.put(6, batArr.getInt(6) + 1);
                        } else if (runs == 4)
                            batArr.put(5, batArr.getInt(5) + 1);
                        batArr.put(1, batArr.getInt(1) + runs);
                        batArr.put(2, batArr.getInt(2) + ball);
                    }
                    batsmenArr.put(i, batArr);
                }
            }
            inningsJson.put("batsmen", batsmenArr);
            Log.e(TAG, matchJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void addBallToBowler(int balls, int runs) {
        try {
            JSONArray bowlersArr = inningsJson.getJSONArray("bowler");
            for (int i = 0; i < bowlersArr.length(); i++) {
                JSONArray bowArr = bowlersArr.getJSONArray(i);
                if (bowArr.get(3).equals(Utils.JSON_BOWLING)) {
                    double o = bowArr.getDouble(2);
                    if (balls == 1) {
                        o = o + 0.1;
                    }
                    o = Double.parseDouble(String.format("%.1f", o));
                    if (String.valueOf(o).contains(".6")) {
                        o = o + .4;//it will compete over
                    }
                    bowArr.put(2, o);
                    bowArr.put(1, bowArr.getInt(1) + runs);
                    bowlersArr.put(i, bowArr);
                }
            }
            inningsJson.put("bowler", bowlersArr);
            Log.e(TAG, matchJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void addWickerToBowler() {
        try {
            JSONArray bowlersArr = inningsJson.getJSONArray("bowler");
            for (int i = 0; i < bowlersArr.length(); i++) {
                JSONArray bowArr = bowlersArr.getJSONArray(i);
                if (bowArr.get(3).equals(Utils.JSON_BOWLING)) {
                    bowArr.put(4, bowArr.getInt(4) + 1);
                    bowlersArr.put(i, bowArr);
                }
            }
            inningsJson.put("bowler", bowlersArr);
            Log.e(TAG, matchJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setDataToEdiText(String str) {
        int charCount = 0;
        for (char ch : tvRunsAdded.getText().toString().toCharArray()) {
            if (ch == '+') {
                charCount++;
            }
        }
        if (tvRunsAdded.getText().toString().isEmpty())
            tvRunsAdded.append(str);
        else if (str.equalsIgnoreCase("out"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().equalsIgnoreCase("out"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().matches(regexStr))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("runout") && !str.matches(regexStr))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("nb") && str.toLowerCase().contains("wd"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("nb") && str.equalsIgnoreCase("nb"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("nb") && str.toLowerCase().equalsIgnoreCase("out"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("nb") && tvRunsAdded.getText().toString().toLowerCase().contains("+b") && str.toLowerCase().contains("lb"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("nb") && tvRunsAdded.getText().toString().toLowerCase().contains("+lb") && str.toLowerCase().contains("b"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("wd") && str.equalsIgnoreCase("nb"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("wd") && str.toLowerCase().contains("wd"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("wd") && str.toLowerCase().contains("lb"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("wd") && str.toLowerCase().equalsIgnoreCase("out"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("wd") && str.toLowerCase().equals("0"))
            tvRunsAdded.setText(str);
        else if ((tvRunsAdded.getText().toString().toLowerCase().contains("nb+b+runout")) || (tvRunsAdded.getText().toString().toLowerCase().contains("nb+lb+runout"))
                || (tvRunsAdded.getText().toString().toLowerCase().contains("wd+b+runout")) || (tvRunsAdded.getText().toString().toLowerCase().contains("wd+lb+runout"))
                || (tvRunsAdded.getText().toString().toLowerCase().contains("b+runout")) || (tvRunsAdded.getText().toString().toLowerCase().contains("lb+runout")))
            if (charCount <= 2 && str.toLowerCase().matches(regexStr))
                tvRunsAdded.append("+" + str);
            else tvRunsAdded.setText(str);

        else if (tvRunsAdded.getText().toString().equals("b") && !str.toLowerCase().matches(regexStr) && !str.equalsIgnoreCase("runout") && !str.equalsIgnoreCase("runout"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("lb") && !str.toLowerCase().matches(regexStr) && !str.equalsIgnoreCase("runout"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("wd"))
            if (charCount == 1 && tvRunsAdded.getText().toString().split("\\+")[1].matches(regexStr)) {
                tvRunsAdded.setText(str);
            } else if (charCount < 2)
                tvRunsAdded.append("+" + str);
            else
                tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("nb")) {
            if (charCount == 1 && tvRunsAdded.getText().toString().split("\\+")[1].matches(regexStr)) {
                tvRunsAdded.setText(str);
            } else if (charCount < 2)
                tvRunsAdded.append("+" + str);
            else
                tvRunsAdded.setText(str);
        } else if (charCount > 0)
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("0"))
            tvRunsAdded.setText(str);
        else if (!tvRunsAdded.getText().toString().isEmpty())
            tvRunsAdded.append("+" + str);
    }

    private void addBallToLayout(JSONArray arr) {

        try {
            RecyclerView ballLayout = (RecyclerView) findViewById(R.id.ll_addBall_match);
            ballLayout.setLayoutManager(new GridLayoutManager(this, 7));
            MatchGridAdapter adapter;
            if (arr.length() == 0) {
                adapter = new MatchGridAdapter(this, new String[]{});
            } else {
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < arr.length(); i++) {
                    list.add(arr.getString(i));
                }
                String[] stringArray = list.toArray(new String[list.size()]);
                adapter = new MatchGridAdapter(this, stringArray);
            }
            //adapter.setClickListener(this);
            ballLayout.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    /* private void addBallToLayout(String str) {
        LinearLayout ballLayout = (LinearLayout) findViewById(R.id.ll_addBall_match);
        if (ballLayout.getChildCount() > 0)
            ballLayout.removeAllViews();
        String[] arr = str.split("/");
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length() != 0) {
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(MatchActivity.this).inflate(R.layout.item_ball, null);
                ((TextView) linearLayout.findViewById(R.id.tv_item_ball)).setText(arr[i]);
                ballLayout.addView(linearLayout);
            }
        }
    }*/
}

