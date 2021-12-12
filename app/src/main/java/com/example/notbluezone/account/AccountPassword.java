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

public class AccountPassword extends Fragment {
    EditText txtCurrentPassword, txtNewPassword, txtConfirmPassword;
    Button btnPasswordSave;
    private String currentPassword = "";

    public AccountPassword() { }

    public static AccountPassword newInstance() {
        return new AccountPassword();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_password, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.account_password));
        toolbar.setNavigationOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack(null, 0);
        });

        txtCurrentPassword = view.findViewById(R.id.txtCurrentPassword);
        txtNewPassword = view.findViewById(R.id.txtNewPassword);
        txtConfirmPassword = view.findViewById(R.id.txtConfirmPassword);

        btnPasswordSave = view.findViewById(R.id.btnPasswordSave);
        btnPasswordSave.setOnClickListener(onSaveButtonClick);

        return view;
    }

    View.OnClickListener onSaveButtonClick = v -> {
        if (txtCurrentPassword.getText().toString().trim().isEmpty()
        ||txtNewPassword.getText().toString().trim().isEmpty()
        ||txtConfirmPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "You have empty fields that needed to be filled out!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!txtNewPassword.getText().toString().trim().equals(txtConfirmPassword.getText().toString().trim())){
            Toast.makeText(getActivity(), "Please reconfirm your password; the fields do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentPassword = txtCurrentPassword.getText().toString().trim();
        String newPassword = txtNewPassword.getText().toString().trim();

        PHPHelper.prepareUpdatePassword(MainActivity.user_id, currentPassword, newPassword, getActivity());
    };
}