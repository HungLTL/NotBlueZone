package com.example.notbluezone.sqlite;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.notbluezone.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    EditText txtRegisterUsername, txtRegisterEmail, txtRegisterPhone, txtRegisterPassword;
    Button btnRegister;
    public RegisterFragment() {}

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        initComponent(view);

        return view;
    }

    public void initComponent(View view){
        txtRegisterEmail = view.findViewById(R.id.txtRegisterEmail);
        txtRegisterPassword = view.findViewById(R.id.txtRegisterPassword);
        txtRegisterPhone = view.findViewById(R.id.txtRegisterPhone);
        txtRegisterUsername = view.findViewById(R.id.txtRegisterUsername);

        btnRegister = view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(onRegisterButtonClick);
    }

    View.OnClickListener onRegisterButtonClick = v -> {
        if (txtRegisterEmail.getText().toString().trim().length() == 0
        || txtRegisterPassword.getText().toString().trim().length() == 0
        || txtRegisterUsername.getText().toString().trim().length() == 0)
            Toast.makeText(getActivity(), "You need a username, password and email!", Toast.LENGTH_SHORT).show();
        else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(txtRegisterEmail.getText()).matches()) {
                Toast.makeText(getActivity(), "You must input a valid email!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (txtRegisterPhone.getText().toString().length() > 0) {
                if (!android.util.Patterns.PHONE.matcher(txtRegisterPhone.getText()).matches()) {
                    Toast.makeText(getActivity(), "You must input a valid phone number!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (txtRegisterPassword.getText().toString().length() < 8) {
                Toast.makeText(getActivity(), "A password should have at least 8 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            RegisterUser(txtRegisterUsername.getText().toString(),
                    0,
                    txtRegisterPassword.getText().toString(),
                    txtRegisterEmail.getText().toString(),
                    txtRegisterPhone.getText().toString());
        }
    };

    public void RegisterUser(String username, int account_type, String password, String email, String phone) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/registerUser.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                if (result.getBoolean("success")) {
                    Toast.makeText(getActivity(), "Account successfully registered!", Toast.LENGTH_SHORT).show();
                    FragmentManager fm = getParentFragmentManager();
                    fm.popBackStack(null, 0);
                }
                else
                    Toast.makeText(getActivity(), "Account not registered.", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("REGISTER_FRAGMENT", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("account_type", Integer.toString(account_type));
                params.put("password", password);
                params.put("email", email);
                params.put("phone", phone);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);
    }
}