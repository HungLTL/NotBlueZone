package com.example.notbluezone.declare;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.notbluezone.R;
import com.example.notbluezone.account.AccountPersonalInfo;
import com.example.notbluezone.adapters.IdWithNameListItem;
import com.google.android.material.tabs.TabLayout;

public class DeclareFragment extends Fragment {
    public DeclareFragment() { }

    public static DeclareFragment newInstance() {
        return new DeclareFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_declare, container, false);

        TextView txtDeclareOption = view.findViewById(R.id.txtDeclareOption);
        txtDeclareOption.setText(R.string.declare_health);
        TextView txtDeclareOptionDesc = view.findViewById(R.id.txtDeclareOptionDesc);
        txtDeclareOptionDesc.setText(R.string.declare_health_desc);
        TextView txtLastDeclaration = view.findViewById(R.id.txtLastDeclaration);

        ImageView imgDeclareOption = view.findViewById(R.id.imgDeclareOption);
        imgDeclareOption.setImageResource(R.drawable.ic_declare_health_);

        TabLayout declareTabLayout = view.findViewById(R.id.declareTabLayout);
        declareTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:{
                        txtDeclareOption.setText(R.string.declare_health);
                        txtDeclareOptionDesc.setText(R.string.declare_health_desc);
                        imgDeclareOption.setImageResource(R.drawable.ic_declare_health_);
                        break;
                    }
                    case 1:{
                        txtDeclareOption.setText(R.string.declare_vax);
                        txtDeclareOptionDesc.setText(R.string.declare_vax_desc);
                        imgDeclareOption.setImageResource(R.drawable.ic_declare_vax);
                        break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Button btnDeclare = view.findViewById(R.id.btnDeclare);
        btnDeclare.setOnClickListener(v -> {
            Fragment newFragment = new DeclareHealth();
            Bundle args = new Bundle();
            switch (declareTabLayout.getSelectedTabPosition()) {
                case 0:
                    args.putBoolean("DECLARE_HEALTH", true);
                    newFragment.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(((ViewGroup) getView().getParent()).getId(), newFragment, null)
                            .addToBackStack(null)
                            .commit();
                case 1:
                    args.putBoolean("DECLARE_VAX", true);
                    newFragment.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(((ViewGroup) getView().getParent()).getId(), newFragment, null)
                            .addToBackStack(null)
                            .commit();
                default:
                    break;
            }
        });

        return view;
    }
}