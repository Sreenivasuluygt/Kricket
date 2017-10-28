package obu.ckt.cricket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import obu.ckt.cricket.adapters.HistoryAdapter;
import obu.ckt.cricket.comon.Utils;
import obu.ckt.cricket.database.DatabaseHandler;
import obu.ckt.cricket.model.Match;
import obu.ckt.cricket.model.MatchHistory;

public class MatchHistoryActivity extends AppCompatActivity {
    private Match match;
    private DatabaseHandler db;
    private TextView tvResult, tvHeading;
    private RecyclerView rvHistory;
    private List<MatchHistory> histList = new ArrayList<>();
    private JSONObject jObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_history_activity);
        initControls();
    }

    private void initControls() {
        tvHeading = (TextView) findViewById(R.id.tv_matchhHeading_history);
        tvResult = (TextView) findViewById(R.id.tv_result_history);
        rvHistory = (RecyclerView) findViewById(R.id.recycler_history);
        db = new DatabaseHandler(this);
        match = db.getMatchInfo(getIntent().getStringExtra(Utils.EXTRA_MATCHE_ID));
        getHistLit();
        loadRecycler();
    }

    private void loadRecycler() {
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        HistoryAdapter adapter = new HistoryAdapter(this, histList);
        rvHistory.setAdapter(adapter);
    }

    private void getHistLit() {
        try {
            jObj = new JSONObject(match.json);
            histList.add(new MatchHistory(Utils.VIEWTYPE_HEADING, "1st Innings:" + Utils.getTeamName(match.teamA)));
            histList.add(new MatchHistory(Utils.VIEWTYPE_BATSMEN_HEADING, "[\"Batsmen\", \"Runs\", \"Balls\", \"\", \"\",\" 4's\", \"6's\"]"));
            histList.addAll(getBatsmenList(jObj.getJSONObject("1stInnings")));
            histList.add(new MatchHistory(Utils.VIEWTYPE_SCORES, jObj.getJSONObject("1stInnings").toString()));
            histList.add(new MatchHistory(Utils.VIEWTYPE_BOWLER_HEADING, "[\"Bowler\", \"Runs\",\"Overs\", \"\"]"));
            histList.addAll(getBowlerList(jObj.getJSONObject("1stInnings")));

            histList.add(new MatchHistory(Utils.VIEWTYPE_HEADING, "2nd Innings:" + Utils.getTeamName(match.teamB)));
            histList.add(new MatchHistory(Utils.VIEWTYPE_BATSMEN_HEADING, "[\"Batsmen\", \"Runs\", \"Balls\", \"\", \"\",\" 4's\", \"6's\"]"));
            histList.addAll(getBatsmenList(jObj.getJSONObject("2ndInnings")));
            histList.add(new MatchHistory(Utils.VIEWTYPE_SCORES, jObj.getJSONObject("2ndInnings").toString()));
            histList.add(new MatchHistory(Utils.VIEWTYPE_BOWLER_HEADING, "[\"Bowler\", \"Runs\",\"Overs\", \"\"]"));
            histList.addAll(getBowlerList(jObj.getJSONObject("2ndInnings")));
            Log.e("History List", String.valueOf(histList.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<MatchHistory> getBowlerList(JSONObject jsonObject) {
        List<MatchHistory> list = new ArrayList<>();
        try {
            JSONArray bowAry = jsonObject.getJSONArray("bowler");
            for (int i = 0; i < bowAry.length(); i++) {
                if (bowAry.getJSONArray(i).getDouble(2) > 0)
                    list.add(new MatchHistory(Utils.VIEWTYPE_BOWLER, bowAry.get(i).toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<MatchHistory> getBatsmenList(JSONObject jsonObject) {
        List<MatchHistory> list = new ArrayList<>();
        try {
            JSONArray batmenAry = jsonObject.getJSONArray("batsmen");
            for (int i = 0; i < batmenAry.length(); i++) {
                if (batmenAry.getJSONArray(i).getInt(2) != 0 || batmenAry.getJSONArray(i).getString(3).equalsIgnoreCase("out"))
                    list.add(new MatchHistory(Utils.VIEWTYPE_BATSMEN, batmenAry.get(i).toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
