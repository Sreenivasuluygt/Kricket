package obu.ckt.cricket.adapters;

/**
 * Created by Administrator on 10/13/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import obu.ckt.cricket.R;
import obu.ckt.cricket.comon.Utils;
import obu.ckt.cricket.model.Match;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.Holder> {
    Context context;
    List<Match> list = new ArrayList<>();
    ItemClick itemClick;

    public HomeAdapter(Context context, ItemClick itemClick) {
        this.context = context;
        this.itemClick = itemClick;
    }

    public void setMatchList(List<Match> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_home, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        try {
            final Match match = list.get(position);
            holder.tvName.setText((Utils.getTeamName(match.teamA) + " V/S " + Utils.getTeamName(match.teamB)));
            JSONObject jObj = new JSONObject(match.json);
            String text = "";
            switch (match.result.toLowerCase()) {
                case "created":
                    holder.tvScores.setText(Utils.getTeamName(jObj.getString("1stBatting")) + ":" + "0/0");
                    holder.tvStatus.setText(match.result);
                    break;
                case "firstinnings":
                    text = Utils.getTeamName(jObj.getString("1stBatting")) + ":" + jObj.getJSONObject("1stInnings").getString("score");
                    holder.tvScores.setText(text);
                    holder.tvStatus.setText("Progress");
                    break;
                case "secondinnings":
                    if (match.teamA.equals(jObj.getString("1stBatting"))) {
                        text = Utils.getTeamName(match.teamB) + ":" + jObj.getJSONObject("2ndInnings").getString("score");
                    } else
                        text = Utils.getTeamName(match.teamA) + ":" + jObj.getJSONObject("2ndInnings").getString("score");
                    text = Utils.getTeamName(jObj.getString("1stBatting")) + ":" + jObj.getJSONObject("1stInnings").getString("score")
                            + " " + text;
                    holder.tvScores.setText(text);
                    holder.tvStatus.setText("Progress");
                    break;
                case "completed":
                    if (match.teamA.equals(jObj.getString("1stBatting"))) {
                        text = Utils.getTeamName(match.teamB) + ":" + jObj.getJSONObject("2ndInnings").getString("score");
                    } else
                        text = Utils.getTeamName(match.teamA) + ":" + jObj.getJSONObject("2ndInnings").getString("score");
                    text = Utils.getTeamName(jObj.getString("1stBatting")) + ":" + jObj.getJSONObject("1stInnings").getString("score")
                            + " " + text;
                    holder.tvScores.setText(text);
                    holder.tvStatus.setText("Won : " + Utils.getTeamName(jObj.getString("won")));
                    break;
            }
            holder.llClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick.onClick(list.get(list.indexOf(match)).matchId, list.get(list.indexOf(match)).result);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, tvScores;
        RelativeLayout llClick;

        public Holder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name_item_home);
            tvScores = (TextView) itemView.findViewById(R.id.tv_scrore_item_home);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_status_item_home);
            llClick = (RelativeLayout) itemView.findViewById(R.id.rl_click_home);
        }
    }

    public interface ItemClick {
        void onClick(String matchId, String status);
    }
}