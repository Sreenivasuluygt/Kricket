package obu.ckt.cricket.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import obu.ckt.cricket.R;
import obu.ckt.cricket.comon.Utils;
import obu.ckt.cricket.model.MatchHistory;

/**
 * Created by Administrator on 10/27/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {
    private Context mContext;
    private List<MatchHistory> list;

    public HistoryAdapter(Context mContext, List<MatchHistory> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).viewType;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == Utils.VIEWTYPE_HEADING)
            view = LayoutInflater.from(mContext).inflate(R.layout.item_heading_history, parent, false);
        else if (viewType == Utils.VIEWTYPE_BATSMEN)
            view = LayoutInflater.from(mContext).inflate(R.layout.item_batmen_history, parent, false);
        else if (viewType == Utils.VIEWTYPE_SCORES)
            view = LayoutInflater.from(mContext).inflate(R.layout.item_score_history, parent, false);
        else if (viewType == Utils.VIEWTYPE_BOWLER)
            view = LayoutInflater.from(mContext).inflate(R.layout.item_bowler_history, parent, false);
        else if (viewType == Utils.VIEWTYPE_BATSMEN_HEADING)
            view = LayoutInflater.from(mContext).inflate(R.layout.item_batsmen_heading, parent, false);
        else if (viewType == Utils.VIEWTYPE_BOWLER_HEADING)
            view = LayoutInflater.from(mContext).inflate(R.layout.item_bowling_heading, parent, false);
        return new Holder(view, viewType);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        try {
            MatchHistory match = list.get(position);
            if (match.viewType == Utils.VIEWTYPE_HEADING) {
                holder.tvInnName.setText(match.value);
            } else if (match.viewType == Utils.VIEWTYPE_BATSMEN) {
                JSONArray arr = new JSONArray(match.value);
                holder.tvBatsmenName.setText(arr.getString(0));
                holder.tvBatRuns.setText(arr.getString(1));
                holder.tvBatBalls.setText(arr.getString(2));
                holder.tvBat4s.setText(arr.getString(5));
                holder.tvBat6s.setText(arr.getString(6));
               if (arr.getString(3).equals("notout")){
                   holder.tvBatsmenName.append("*");
               }
            } else if (match.viewType == Utils.VIEWTYPE_SCORES) {
                JSONObject obj = new JSONObject(match.value);
                holder.tvInnScore.setText(obj.getString("score"));
                holder.tvInnOvers.setText(obj.getString("overs"));
            } else if (match.viewType == Utils.VIEWTYPE_BOWLER) {
                JSONArray arr = new JSONArray(match.value);
                holder.tvBowlerName.setText(arr.getString(0));
                holder.tvBowOvers.setText(arr.getString(1));
                holder.tvBowRuns.setText(arr.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tvBatsmenName, tvBatRuns, tvBatBalls, tvBat4s, tvBat6s;
        TextView tvBowlerName, tvBowRuns, tvBowOvers;
        TextView tvInnScore, tvInnOvers;
        TextView tvInnName;

        public Holder(View itemView, int viewType) {
            super(itemView);
            if (viewType == Utils.VIEWTYPE_HEADING) {
                tvInnName = (TextView) itemView.findViewById(R.id.tv_innings_item_score);
            } else if (viewType == Utils.VIEWTYPE_BATSMEN) {
                tvBatsmenName = (TextView) itemView.findViewById(R.id.tv_batsmenName_item);
                tvBatRuns = (TextView) itemView.findViewById(R.id.tv_batsmenRuns_item);
                tvBatBalls = (TextView) itemView.findViewById(R.id.tv_batsmenBalls_item);
                tvBat4s = (TextView) itemView.findViewById(R.id.tv_batsmen4s_item);
                tvBat6s = (TextView) itemView.findViewById(R.id.tv_batsmen6s_item);
            } else if (viewType == Utils.VIEWTYPE_SCORES) {
                tvInnScore = (TextView) itemView.findViewById(R.id.tv_score_hist);
                tvInnOvers = (TextView) itemView.findViewById(R.id.tv_overs_history);
            } else if (viewType == Utils.VIEWTYPE_BOWLER) {
                tvBowlerName = (TextView) itemView.findViewById(R.id.tv_bowlerName_item);
                tvBowRuns = (TextView) itemView.findViewById(R.id.tv_bowlerRuns_item);
                tvBowOvers = (TextView) itemView.findViewById(R.id.tv_bowlerOvers_item);
            }
        }
    }
}
