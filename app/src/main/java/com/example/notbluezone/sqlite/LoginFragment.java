package com.example.notbluezone.sqlite;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
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

public class LoginFragment extends Fragment {
    TextView txtLoginInfo, txtLoginPassword;
    RadioGroup loginOptions;
    public LoginFragment() { }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        txtLoginInfo = view.findViewById(R.id.txtLoginInfo);
        txtLoginPassword = view.findViewById(R.id.txtLoginPassword);

        Button btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(onLoginButtonClick);
        Button btnRequestOTP = view.findViewById(R.id.btnRequestOTP);

        loginOptions = view.findViewById(R.id.loginOptions);
        loginOptions.setOnCheckedChangeListener((group, checkedId) -> {
            switch(checkedId) {
                case R.id.rbUsernameLogin: {
                    txtLoginInfo.setText("");
                    txtLoginPassword.setText("");
                    txtLoginInfo.setHint(R.string.login_input_username);
                    txtLoginInfo.setInputType(InputType.TYPE_CLASS_TEXT);
                    txtLoginPassword.setHint(R.string.login_input_password);
                    txtLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnRequestOTP.setVisibility(View.INVISIBLE);
                    break;
                }
                case R.id.rbEmailLogin: {
                    txtLoginInfo.setText("");
                    txtLoginPassword.setText("");
                    txtLoginInfo.setHint(R.string.login_input_email);
                    txtLoginInfo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    txtLoginPassword.setHint(R.string.login_input_password);
                    txtLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnRequestOTP.setVisibility(View.INVISIBLE);
                    break;
                }
                case R.id.rbPhoneLogin: {
                    txtLoginInfo.setText("");
                    txtLoginPassword.setText("");
                    txtLoginInfo.setHint(R.string.login_input_phone);
                    txtLoginInfo.setInputType(InputType.TYPE_CLASS_PHONE);
                    txtLoginPassword.setHint(R.string.login_input_otp);
                    txtLoginPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                    btnRequestOTP.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });

        TextView txtRegisterAccount = view.findViewById(R.id.txtRegisterAccount);
        txtRegisterAccount.setOnClickListener(v -> {
            Fragment newFragment = new RegisterFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup) getView().getParent()).getId(), newFragment, null)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    View.OnClickListener onLoginButtonClick = v -> {
        if (txtLoginInfo.getText().toString().length() == 0 || txtLoginPassword.getText().toString().length() == 0 ) {
            Toast.makeText(getActivity(), "You must input your login information!", Toast.LENGTH_SHORT).show();
            return;
        }

        switch(loginOptions.getCheckedRadioButtonId()){
            case R.id.rbUsernameLogin:
                checkUserLoginUsername(txtLoginInfo.getText().toString(), txtLoginPassword.getText().toString());
                break;
            case R.id.rbEmailLogin:
                checkUserLoginEmail(txtLoginInfo.getText().toString(), txtLoginPassword.getText().toString());
                break;
        }
    };

    public void checkUserLoginUsername(String username, String password) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/checkUserLoginUsername.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    if (dbHelper.getUserById(result.getInt("id")) != -1)
                        dbHelper.updateLoginStatus(result.getInt("id"));
                    else {
                        dbHelper.addAccount(result.getInt("id"), result.getInt("privilege"), getActivity());
                        dbHelper.updateLoginStatus(result.getInt("id"));
                    }
                    FragmentManager fm = getParentFragmentManager();
                    fm.popBackStack(null, 0);
                }
                else {
                    Toast.makeText(getActivity(), "Incorrect login information!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("LOGIN_FRAGMENT", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);
    }

    public void checkUserLoginEmail(String email, String password) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/checkUserLoginEmail.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    if (dbHelper.getUserById(result.getInt("id")) != -1)
                        dbHelper.updateLoginStatus(result.getInt("id"));
                    else {
                        dbHelper.addAccount(result.getInt("id"), result.getInt("privilege"), getActivity());
                        dbHelper.updateLoginStatus(result.getInt("id"));
                    }
                    FragmentManager fm = getParentFragmentManager();
                    fm.popBackStack(null, 0);
                }
                else {
                    Toast.makeText(getActivity(), "Incorrect login information!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("LOGIN_FRAGMENT", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);
    }
}