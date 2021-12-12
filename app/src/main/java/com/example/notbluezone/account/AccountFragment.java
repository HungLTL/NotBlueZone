package com.example.notbluezone.account;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.notbluezone.MainActivity;
import com.example.notbluezone.R;
import com.example.notbluezone.sqlite.DatabaseHelper;
import com.example.notbluezone.sqlite.LoginFragment;
import com.example.notbluezone.sqlite.PHPHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment {
    Button btnAccountProfile, btnAccountFamily, btnAccountAccount, btnAccountToS, btnAccountFeedback, btnAccountAbout, btnLogout;
    TextView txtLoginIntroText, txtLoginName;
    DatabaseHelper dbHelper;
    Fragment newFragment;

    public AccountFragment() { }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);
        dbHelper = new DatabaseHelper(getActivity());
        initComponents(view);

        if (MainActivity.user_id > 0) {
            txtLoginIntroText.setText(R.string.account_login_intro);
            PHPHelper.getUsernameForAccountScreen(MainActivity.user_id, txtLoginName, getActivity());
            btnLogout.setText(R.string.account_logout);
        }
        else {
            txtLoginIntroText.setText(R.string.account_not_login_intro);
            txtLoginName.setVisibility(View.GONE);
            btnLogout.setText(R.string.account_login);
        }

        return view;
    }

    void initComponents(View view) {
        btnAccountProfile = view.findViewById(R.id.btnAccountProfile);
        btnAccountProfile.setOnClickListener(runProfileFragment);

        btnAccountFamily = view.findViewById(R.id.btnAccountFamily);
        btnAccountFamily.setOnClickListener(runFamilyFragment);

        btnAccountAccount = view.findViewById(R.id.btnAccountAccount);
        btnAccountAccount.setOnClickListener(runAccountFragment);

        btnAccountToS = view.findViewById(R.id.btnAccountToS);
        btnAccountFeedback = view.findViewById(R.id.btnAccountFeedback);
        btnAccountAbout = view.findViewById(R.id.btnAccountAbout);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(onLogoutButtonClick);

        txtLoginIntroText = view.findViewById(R.id.txtLoginIntroText);
        txtLoginName = view.findViewById(R.id.txtLoginName);
    }

    View.OnClickListener runProfileFragment = v -> {
        if (MainActivity.user_id > 0) {
            newFragment = new AccountProfile();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup) getView().getParent()).getId(), newFragment, null)
                    .addToBackStack(null)
                    .commit();
        }
        else
            Toast.makeText(getContext(), "You must be logged in to access this option!", Toast.LENGTH_LONG).show();
    };

    View.OnClickListener runFamilyFragment = v -> {
        if (MainActivity.user_id > 0) {
            newFragment = new AccountPersonalInfo();
            Bundle args = new Bundle();
            args.putBoolean("IS_EDITING_FAMILY", true);
            newFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup) getView().getParent()).getId(), newFragment, null)
                    .addToBackStack(null)
                    .commit();
        }
        else
            Toast.makeText(getContext(), "You must be logged in to access this option!", Toast.LENGTH_LONG).show();
    };

    View.OnClickListener runAccountFragment = v -> {
        if (MainActivity.user_id > 0) {
            newFragment = new AccountInformation();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup) getView().getParent()).getId(), newFragment, null)
                    .addToBackStack(null)
                    .commit();
        }
        else
            Toast.makeText(getContext(), "You must be logged in to access this option!", Toast.LENGTH_LONG).show();
    };

    View.OnClickListener onLogoutButtonClick = v -> {
        if (MainActivity.user_id <= 0) {
            newFragment = new LoginFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup) getView().getParent()).getId(), newFragment, null)
                    .addToBackStack(null)
                    .commit();
        }
        else {
            dbHelper.logOutAllAccounts();
            newFragment = new AccountFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup) getView().getParent()).getId(), newFragment, null)
                    .addToBackStack(null)
                    .commit();
        }
    };
}