package obu.ckt.cricket.fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import obu.ckt.cricket.HomeActivity;
import obu.ckt.cricket.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements View.OnClickListener {
    private CardView cvQuick, cvTournament;
    private View view;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initControls();
        return view;
    }

    private void initControls() {
        cvQuick = (CardView) view.findViewById(R.id.cv_quick);
        cvTournament = (CardView) view.findViewById(R.id.cv_tournament);
        cvQuick.setOnClickListener(this);
        cvTournament.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == cvQuick) {
            ((HomeActivity) getActivity()).addFragment(QuickFragment.newInstance("Progress"));
        } else if (v == cvTournament) {
            Snackbar.make(cvTournament, "Coming soon..", Snackbar.LENGTH_SHORT).show();
        }
    }
}
