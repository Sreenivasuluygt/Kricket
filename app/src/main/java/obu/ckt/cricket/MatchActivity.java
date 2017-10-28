package obu.ckt.cricket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

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
            tvChangeStriker, tvBowler, tvBowlerOvers, tvBowlerRuns, tvChangeBowler, tvScore, tvOvers, tvMatchHeading, tvRunsAdded, tvThisOver;
    private Button btnOk, btnClear;
    private String regexStr = "^[0-9]*$";
    private DatabaseHandler db;
    private SharePref prefs;
    private DataLayer dl;
    private Match match;
    private String TAG = "MatchActivity";
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
        tvBowlerOvers = (TextView) findViewById(R.id.tv_bowlerOvers_match);
        tvChangeBowler = (TextView) findViewById(R.id.tv_changeBowler_match);
        tvScore = (TextView) findViewById(R.id.tv_score_match);
        tvOvers = (TextView) findViewById(R.id.tv_overs_match);
        tvMatchHeading = (TextView) findViewById(R.id.tv_matchhHeading_match);
        btnClear = (Button) findViewById(R.id.btn_clear_match);
        btnOk = (Button) findViewById(R.id.btn_ok_match);
        tvRunsAdded = (TextView) findViewById(R.id.et_runsAdded_match);
        tvThisOver = (TextView) findViewById(R.id.tv_thisOver_match);
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
    }

    private void getData() {
        try {
            match = db.getMatchInfo(getIntent().getStringExtra(Utils.EXTRA_MATCHE_ID));
            tvMatchHeading.setText(Utils.getTeamName(match.teamA) + " VS " + Utils.getTeamName(match.teamB));
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
            for (int i = 0; i < dummy.length(); i++) {
                JSONArray jArr = dummy.getJSONArray(i);
                if (jArr.get(3).equals(Utils.JSON_BOWLING)) {
                    tvBowler.setText(jArr.getString(0));
                    tvBowlerOvers.setText(jArr.getString(2));
                    tvBowlerRuns.setText(jArr.getString(1));
                }

            }
            tvScore.setText(inningsJson.getString("score"));
            tvThisOver.setText("This Over : " + inningsJson.getString("thisOver").replace("/", " "));
            tvOvers.setText(inningsJson.getString("overs") + "(" + matchJson.getString("overs") + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dot_match:
                setDataToEdiText(".");
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
                setDataToEdiText("N");
                break;
            case R.id.tv_wide_match:
                setDataToEdiText("W");
                break;
            case R.id.tv_byes_match:
                setDataToEdiText("B");
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
                break;
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
                    loadInning(inningsJson);
                    Log.e("", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addStriker() {
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
            Toast.makeText(getApplicationContext(), "Select runs to add", Toast.LENGTH_LONG).show();
        } else if (!batmen1.contains("*") && !batmen2.contains("*")) {
            Toast.makeText(getApplicationContext(), "Select striker", Toast.LENGTH_LONG).show();
        } else if (!checkBowlerToBowl()) {
            Toast.makeText(getApplicationContext(), "Select bowler to bowl", Toast.LENGTH_LONG).show();
        }
        /*else if (charCount > 0) {
            Toast.makeText(getApplicationContext(), "Enter correct data", Toast.LENGTH_LONG).show();
        }*/
        else if (etData.equalsIgnoreCase("lb"))
            Toast.makeText(getApplicationContext(), "Enter Leg Bye runs", Toast.LENGTH_LONG).show();
        else if (etData.equalsIgnoreCase("b"))
            Toast.makeText(getApplicationContext(), "Enter bye runs", Toast.LENGTH_LONG).show();
        else if (etData.equalsIgnoreCase("w+b"))
            Toast.makeText(getApplicationContext(), "Enter bye runs", Toast.LENGTH_LONG).show();
        else if (etData.equalsIgnoreCase("w+lb"))
            Toast.makeText(getApplicationContext(), "Enter Leg Bye runs", Toast.LENGTH_LONG).show();
        else if (etData.equalsIgnoreCase("n+lb"))
            Toast.makeText(getApplicationContext(), "Enter Leg Bye runs", Toast.LENGTH_LONG).show();
        else if (etData.equalsIgnoreCase("n+b"))
            Toast.makeText(getApplicationContext(), "Enter Bye runs", Toast.LENGTH_LONG).show();
        else if (etData.equalsIgnoreCase("n+b+runout") || etData.equalsIgnoreCase("n+lb+runout") ||
                etData.equalsIgnoreCase("w+b+runout") || etData.equalsIgnoreCase("w+lb+runout") ||
                etData.equalsIgnoreCase("b+runout") || etData.equalsIgnoreCase("lb+runout"))
            Toast.makeText(getApplicationContext(), "Enter Bye runs", Toast.LENGTH_LONG).show();
        else {
            if (etData.equalsIgnoreCase("out")) {
                addOut(etData);
            } else if (etData.contains("runout")) {
                addRunOut(etData);
            } else if (etData.contains(".")) {
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
            } else if (etData.toLowerCase().contains("w")) {
                addWides(etData);
            } else if (etData.toLowerCase().contains("n")) {
                addNoBall(etData);
            } else if (etData.toLowerCase().contains("lb")) {
                addLegByes(etData);
            } else if (etData.toLowerCase().contains("b")) {
                addByes(etData);
            }
            addThisOversRuns(etData);
            if (!etData.toLowerCase().contains("out")) {
                checkMatchStatus(etData);
                getData();
            }
        }
    }

    private void addThisOversRuns(String etData) {
        try {
            String str = inningsJson.getString("thisOver") + "/" + etData.replace("+", "").replace("W", "wd").replace("runout", "w")
                    .replace("out", "w").replace("B", "").replace("N", "nb");
            inningsJson.put("thisOver", str);
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
            if (!etData.contains("w") || !etData.contains("n")) {
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
                        match.json = matchJson.toString();
                        match.result = "Completed";
                        Utils.singleAlertDialog(MatchActivity.this, "Match completed");
                    } else {
                        matchJson.put("1stInnings", inningsJson);
                        match.json = matchJson.toString();
                        match.result = "SecondInnings";
                        Utils.singleAlertDialog(MatchActivity.this, "First innings completed");
                    }
                    db.insertMatch(match, Long.parseLong(match.matchId), new CreateMatch() {
                        @Override
                        public void success(int matchId) {

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


                    match.json = matchJson.toString();
                    match.result = "Completed";
                    Utils.singleAlertDialog(MatchActivity.this, "Match completed");
                    db.insertMatch(match, Long.parseLong(match.matchId), new CreateMatch() {
                        @Override
                        public void success(int matchId) {

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
                        match.json = matchJson.toString();
                        match.result = "Completed";
                        Utils.singleAlertDialog(MatchActivity.this, "Match completed");
                    } else {
                        matchJson.put("1stInnings", inningsJson);
                        match.json = matchJson.toString();
                        match.result = "SecondInnings";
                        Utils.singleAlertDialog(MatchActivity.this, "First innings completed");
                    }
                    db.insertMatch(match, Long.parseLong(match.matchId), new CreateMatch() {
                        @Override
                        public void success(int matchId) {

                        }

                        @Override
                        public void failure() {

                        }
                    });
                    return;
                } else if (inningsJson.getString("overs").contains(".0")) {
                    changeStriker();
                    changeBowlerStatus();
                    inningsJson.put("lastBowled", tvBowler.getText().toString());
                    inningsJson.put("thisOver", "");
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
                db.insertMatch(match, Long.parseLong(match.matchId), new CreateMatch() {
                    @Override
                    public void success(int matchId) {

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
            for (int i = 0; i < bowlersArr.length(); i++) {
                JSONArray jArr = bowlersArr.getJSONArray(i);
                jArr.put(3, 0);
                bowlersArr.put(i, jArr);
            }
            inningsJson.put("bowler", bowlersArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addOut(final String etData) {
        dialog.selectPlayer(inningsJson, "Select Who'se out?", new SelectPlayerDialog.OnSelected() {
            @Override
            public void selected(int position) {
                //inningsJson = innings;
                try {
                    addBallToBowler(1, 0);
                    addBallToOvers();
                    addBallToBatmen(1, 0);
                    inningsJson.getJSONArray("batsmen").getJSONArray(position).put(3, "out");
                    String[] str = inningsJson.getString("score").split("/");
                    inningsJson.put("score", str[0] + "/" + String.valueOf(Integer.parseInt(str[1]) + 1));
                    checkMatchStatus(etData);
                    getData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void addRunOut(final String etData) {

        dialog.selectPlayer(inningsJson, "Select Who'se out?", new SelectPlayerDialog.OnSelected() {
            @Override
            public void selected(int position) {
                //inningsJson = innings;
                try {
                    if (etData.contains("+")) {
                        String[] str = etData.split("\\+");
                        if (etData.toLowerCase().contains("n"))
                            addNoBall(etData.toLowerCase().replace("+runout", ""));
                        else if (etData.contains("w"))
                            addNoBall(etData.toLowerCase().replace("+runout", ""));
                        else addRun(Integer.parseInt(str[str.length - 1]));
                    } else {
                        addBallToBowler(1, 0);
                        addBallToOvers();
                    }
                    addBallToBatmen(1, 0);
                    inningsJson.getJSONArray("batsmen").getJSONArray(position).put(3, "out");
                    String[] str = inningsJson.getString("score").split("/");
                    inningsJson.put("score", str[0] + "/" + String.valueOf(Integer.parseInt(str[1]) + 1));
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
        if ((i % 2) != 0) {
            changeStriker();
        }
        addRunToScore(i);
        addBallToOvers();
        addBallToBatmen(1, 0);
        addBallToBowler(1, i);
    }

    private void addByes(String etData) {
        String[] str = etData.split("\\+");
        int i = Integer.parseInt(str[str.length - 1]);
        if ((i % 2) != 0) {
            changeStriker();
        }
        addRunToScore(i);
        addBallToOvers();
        addBallToBatmen(1, 0);
        addBallToBowler(1, i);
    }


    private void addNoBall(String etData) {
        if (etData.contains("+")) {
            String[] str = etData.split("\\+");
            int i = Integer.parseInt(str[str.length - 1]);
            if (etData.contains("b") || (etData.contains("lb"))) {
                if ((i % 2) != 0) {
                    changeStriker();
                }
                addRunToScore(1 + i);
                addBallToBatmen(1, 0);
            } else {
                addBallToBatmen(1, i);
                addRunToScore(1 + i);
            }
            addBallToBowler(0,1 + i);
        } else {addRunToScore(1);addBallToBowler(0,1);}
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
                    } else {
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
        else if (tvRunsAdded.getText().toString().contains("N") && str.toLowerCase().contains("w"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("N") && str.equalsIgnoreCase("n"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("N") && str.toLowerCase().equalsIgnoreCase("out"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("N") && str.toLowerCase().equals("."))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("N") && tvRunsAdded.getText().toString().toLowerCase().contains("b") && str.toLowerCase().contains("lb"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("N") && tvRunsAdded.getText().toString().toLowerCase().contains("lb") && str.toLowerCase().contains("b"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("W") && str.equalsIgnoreCase("n"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("W") && str.toLowerCase().contains("w"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("W") && str.toLowerCase().contains("lb"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("W") && str.toLowerCase().equalsIgnoreCase("out"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("W") && str.toLowerCase().equals("."))
            tvRunsAdded.setText(str);
        else if ((tvRunsAdded.getText().toString().toLowerCase().contains("n+b+runout")) || (tvRunsAdded.getText().toString().toLowerCase().contains("n+lb+runout"))
                || (tvRunsAdded.getText().toString().toLowerCase().contains("w+b+runout")) || (tvRunsAdded.getText().toString().toLowerCase().contains("w+lb+runout"))
                || (tvRunsAdded.getText().toString().toLowerCase().contains("b+runout")) || (tvRunsAdded.getText().toString().toLowerCase().contains("lb+runout")))
            if (charCount <= 2 && str.toLowerCase().matches(regexStr))
                tvRunsAdded.append("+" + str);
            else tvRunsAdded.setText(str);

        else if (tvRunsAdded.getText().toString().contains("B") && !str.toLowerCase().matches(regexStr) && !str.equalsIgnoreCase("runout"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("lb") && !str.toLowerCase().matches(regexStr) && !str.equalsIgnoreCase("runout"))
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("W"))
            if (charCount == 1 && tvRunsAdded.getText().toString().split("\\+")[1].matches(regexStr)) {
                return;
            } else if (charCount < 2)
                tvRunsAdded.append("+" + str);
            else
                tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("N")) {
            if (charCount == 1 && tvRunsAdded.getText().toString().split("\\+")[1].matches(regexStr)) {
                return;
            } else if (charCount < 2)
                tvRunsAdded.append("+" + str);
            else
                tvRunsAdded.setText(str);
        } else if (charCount > 0)
            tvRunsAdded.setText(str);
        else if (tvRunsAdded.getText().toString().contains("."))
            tvRunsAdded.setText(str);
        else if (!tvRunsAdded.getText().toString().isEmpty())
            tvRunsAdded.append("+" + str);
    }
}

