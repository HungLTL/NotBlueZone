package com.example.notbluezone.account;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notbluezone.MainActivity;
import com.example.notbluezone.R;
import com.example.notbluezone.sqlite.PHPHelper;

public class AccountUsernameEmail extends Fragment {
    EditText txtEditUsername, txtEditEmail;
    Button btnUsernameEmailSave;

    public AccountUsernameEmail() { }

    public static AccountUsernameEmail newInstance() {
        return new AccountUsernameEmail();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_username_email, container, false);

        initComponents(view);

        return view;
    }

    public void initComponents(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.account_username_email));
        toolbar.setNavigationOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack(null, 0);
        });

        txtEditUsername = view.findViewById(R.id.txtEditUsername);
        txtEditEmail = view.findViewById(R.id.txtEditEmail);
        PHPHelper.fetchUsernameAndEmail(MainActivity.user_id, txtEditUsername, txtEditEmail, getActivity());

        btnUsernameEmailSave = view.findViewById(R.id.btnUsernameEmailSave);
        btnUsernameEmailSave.setOnClickListener(onSaveButtonClickListener);
    }

    View.OnClickListener onSaveButtonClickListener = v -> {
        if (txtEditUsername.getText().toString().length() == 0 || txtEditEmail.getText().toString().length() == 0)
            Toast.makeText(getActivity(), "You have empty fields that needed to be filled out!", Toast.LENGTH_SHORT).show();

        String username = txtEditUsername.getText().toString();
        String email = txtEditEmail.getText().toString();

        PHPHelper.updateUsernameAndEmail(MainActivity.user_id, username, email, getActivity());
    };
}