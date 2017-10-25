package obu.ckt.cricket.data;

import android.app.Dialog;
import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import obu.ckt.cricket.R;

/**
 * Created by Administrator on 10/20/2017.
 */

public class SelectPlayerDialog {
    Context mContext;

    public SelectPlayerDialog(Context context) {
        mContext = context;
    }

    public void selectPlayer(final JSONObject inning, String heading, final OnSelected selected) {
        try {
            final Dialog dialog = createDialog();
            TextView tvHeading = (TextView) dialog.findViewById(R.id.tv_message_dialog_select);
            RecyclerView rv = (RecyclerView) dialog.findViewById(R.id.rv_dialog_select);
            tvHeading.setText(heading);
            rv.setLayoutManager(new LinearLayoutManager(mContext));
            final List<String> list = new ArrayList<>();
            JSONArray baArr = inning.getJSONArray("batsmen");
            for (int i = 0; i < baArr.length(); i++) {
                if (list.size() <= 1) {
                    JSONArray jArr = baArr.getJSONArray(i);
                    if (jArr.get(3).equals("notout")) {
                        list.add(jArr.getString(0));
                    }
                } else {
                    break;
                }
            }
            PlayerAdapter adapter = new PlayerAdapter(list, new Click() {
                @Override
                public void onClick(int pos) {
                    /*try {
                        baArr.getJSONArray(pos).put(3, "out");
                        inning.put("batsmen",baArr);
                        selected.selected(inning);
                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    try {
                        final JSONArray baArr = inning.getJSONArray("batsmen");
                        for (int i = 0; i < baArr.length(); i++) {
                            if (baArr.getJSONArray(i).getString(0).equals(list.get(pos))) {
                                selected.selected(i);
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
            rv.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void selectBowler(final JSONObject inning, String heading, final OnSelected selected) {
        try {
            final Dialog dialog = createDialog();
            TextView tvHeading = (TextView) dialog.findViewById(R.id.tv_message_dialog_select);
            RecyclerView rv = (RecyclerView) dialog.findViewById(R.id.rv_dialog_select);
            tvHeading.setText(heading);
            rv.setLayoutManager(new LinearLayoutManager(mContext));
            final List<String> list = new ArrayList<>();
            JSONArray bowArr = inning.getJSONArray("bowler");
            for (int i = 0; i < bowArr.length(); i++) {
                JSONArray jArr = bowArr.getJSONArray(i);
                if (!jArr.get(0).equals(inning.getString("lastBowled"))) {
                    list.add(jArr.getString(0));
                }
            }
            PlayerAdapter adapter = new PlayerAdapter(list, new Click() {
                @Override
                public void onClick(int pos) {
                    /*try {
                        baArr.getJSONArray(pos).put(3, "out");
                        inning.put("batsmen",baArr);
                        selected.selected(inning);
                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    try {
                        final JSONArray bowArr = inning.getJSONArray("bowler");
                        for (int i = 0; i < bowArr.length(); i++) {
                            if (bowArr.getJSONArray(i).getString(0).equals(list.get(pos))) {
                                selected.selected(i);
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
            rv.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Dialog createDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_player);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        return dialog;
    }

    class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.Holder> {
        List<String> list = new ArrayList<>();
        Click click;

        public PlayerAdapter(List<String> list, Click click) {
            this.list = list;
            this.click = click;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_select_player, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            final String str = list.get(position);
            holder.tvName.setText(list.get(position));
            holder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click.onClick(list.indexOf(str));
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class Holder extends RecyclerView.ViewHolder {
            TextView tvName;

            public Holder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_select_item);
            }
        }
    }

    public void changePlayerName(String heading,String name, final PlayerName playerName) {
        try {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_change_player_name);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //lp.gravity = Gravity.BOTTOM;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
            TextView tvHeading = (TextView) dialog.findViewById(R.id.tv_heading_dialog_change_player_name);
            final EditText etName = (EditText) dialog.findViewById(R.id.et_name_dialog_change_player_name);
            Button btnOk = (Button) dialog.findViewById(R.id.btn_ok_dialog_change_player_name);
            etName.append(name);
            tvHeading.setText(heading);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!etName.getText().toString().isEmpty()){
                        playerName.changed(etName.getText().toString());
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    interface Click {
        void onClick(int pos);
    }

    public interface OnSelected {
        void selected(int pos);
    }
    public interface PlayerName{
        void changed(String name);
    }
}
