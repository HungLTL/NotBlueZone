package com.example.notbluezone.account;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.notbluezone.MainActivity;
import com.example.notbluezone.R;
import com.example.notbluezone.adapters.IdWithNameListItem;
import com.example.notbluezone.sqlite.ContractClass;
import com.example.notbluezone.sqlite.DatabaseHelper;
import com.example.notbluezone.sqlite.PHPHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AccountPersonalInfo extends Fragment {
    Spinner spinProfileFamilyMember, spinProfileNationality;
    EditText txtProfilePersonalName, txtProfileDoB, txtProfileCardId, txtProfilePhone;
    RadioGroup rbGroupProfileGender;
    Button btnProfileSave;

    DatabaseHelper dbHelper;

    boolean isEditingFamily;

    ArrayList<IdWithNameListItem> spinNationality, spinFamily;

    public AccountPersonalInfo() { }

    public static AccountPersonalInfo newInstance() {
        return new AccountPersonalInfo();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            isEditingFamily = getArguments().getBoolean("IS_EDITING_FAMILY");
        else
            isEditingFamily = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_personal_info, container, false);

        dbHelper = new DatabaseHelper(getActivity());
        initComponents(view);

        if (!isEditingFamily)
            setUpUserPersonalInfo(MainActivity.user_id, view);

        return view;
    }

    public void initComponents(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (isEditingFamily)
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.account_family));
        else
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.account_edit_info));
        toolbar.setNavigationOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack(null, 0);
        });

        spinProfileFamilyMember = view.findViewById(R.id.spinProfileFamilyMember);
        if (isEditingFamily)
            spinProfileFamilyMember.setVisibility(View.VISIBLE);
        spinFamily = new ArrayList<>();
        spinFamily.add(new IdWithNameListItem("", "New family member"));
        ArrayAdapter familyAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinFamily);
        Cursor cursor = dbHelper.getFamilyMembers(MainActivity.user_id);
        if (cursor != null) {
            int userFamId = dbHelper.getFamilyMemberIdOfUser(MainActivity.user_id);
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex(ContractClass.CFamilyMember._ID)) != userFamId) {
                    IdWithNameListItem item = new IdWithNameListItem(Integer.toString(cursor.getInt(cursor.getColumnIndex(ContractClass.CFamilyMember._ID)))
                    , cursor.getString(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_NAME)));
                    spinFamily.add(item);
                }
                familyAdapter.notifyDataSetChanged();
            }
            cursor.close();
        }
        familyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinProfileFamilyMember.setAdapter(familyAdapter);
        spinProfileFamilyMember.setOnItemSelectedListener(onFamilyMemberSelected);

        spinProfileNationality = view.findViewById(R.id.spinProfileNationality);
        spinNationality = new ArrayList<>();
        ArrayAdapter spinnerAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinNationality);
        PHPHelper.fetchNationality(spinNationality, spinnerAdapter, getActivity());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinProfileNationality.setAdapter(spinnerAdapter);

        txtProfilePersonalName = view.findViewById(R.id.txtProfilePersonalName);
        txtProfileCardId = view.findViewById(R.id.txtProfileCardId);

        txtProfileDoB = view.findViewById(R.id.txtProfileDoB);
        txtProfileDoB.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dateDialog= new DatePickerDialog(getActivity(), datePickerListener, mYear, mMonth, mDay);
            dateDialog.show();
        });

        txtProfilePhone = view.findViewById(R.id.txtProfilePhone);

        rbGroupProfileGender = view.findViewById(R.id.rbGroupProfileGender);

        btnProfileSave = view.findViewById(R.id.btnProfileSave);
        btnProfileSave.setOnClickListener(onSaveButtonClick);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            txtProfileDoB.setText(date);
        }
    };

    public void setUpUserPersonalInfo(int id, View view) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getUserById.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    if (!result.getString("name").equals("null"))
                        txtProfilePersonalName.setText(result.getString("name"));

                    if (!result.getString("DoB").equals("null"))
                        txtProfileDoB.setText(result.getString("DoB"));

                    switch (result.getInt("gender")) {
                        case 1:
                            RadioButton b = view.findViewById(R.id.rbProfileMale);
                            b.setChecked(true);
                            break;
                        case 2:
                            RadioButton b1 = view.findViewById(R.id.rbProfileFemale);
                            b1.setChecked(true);
                            break;
                        default:
                            break;
                    }

                    for (int i = 0; i < spinProfileNationality.getAdapter().getCount(); i++) {
                        if (spinProfileNationality.getAdapter().getItem(i).toString().contains(result.getString("nat_id"))){
                            spinProfileNationality.setSelection(i);
                            break;
                        }
                    }

                    if (!result.getString("card_id").equals("null"))
                        txtProfileCardId.setText(result.getString("card_id"));

                    if (!result.getString("phone").equals("null"))
                        txtProfilePhone.setText(result.getString("phone"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("ACCOUNT_FRAGMENT", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);
    }

    AdapterView.OnItemSelectedListener onFamilyMemberSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            IdWithNameListItem item = (IdWithNameListItem) parent.getItemAtPosition(position);
            if (!item.getId().isEmpty()) {
                Cursor cursor = dbHelper.getFamilyMemberFromId(Integer.parseInt(item.getId()));
                if (cursor != null) {
                    txtProfilePersonalName.setText(cursor.getString(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_NAME)));
                    txtProfileDoB.setText(cursor.getString(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_DOB)));

                    switch (cursor.getInt(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_GENDER))) {
                        case 1:
                            RadioButton b = view.findViewById(R.id.rbProfileMale);
                            b.setChecked(true);
                            break;
                        case 2:
                            RadioButton b1 = view.findViewById(R.id.rbProfileFemale);
                            b1.setChecked(true);
                            break;
                        default:
                            break;
                    }

                    for (int i = 0; i < spinProfileNationality.getAdapter().getCount(); i++) {
                        if (spinProfileNationality.getAdapter().getItem(i).toString().contains(cursor.getString(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_NAT)))){
                            spinProfileNationality.setSelection(i);
                            break;
                        }
                    }

                    txtProfileCardId.setText(cursor.getString(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_CARD)));
                    txtProfilePhone.setText(cursor.getString(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_PHONE)));
                }
            } else {
                if (item.getName().contains("New family member")) {
                    txtProfilePhone.setText("");
                    txtProfileCardId.setText("");
                    txtProfileDoB.setText("");
                    txtProfilePersonalName.setText("");

                    rbGroupProfileGender.clearCheck();
                    spinProfileNationality.setSelection(0);
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    View.OnClickListener onSaveButtonClick = v -> {
        if (!isEditingFamily) {
            if (txtProfilePersonalName.getText().toString().trim().length() > 0) {
                String name = txtProfilePersonalName.getText().toString().trim();
                PHPHelper.updateUserName(MainActivity.user_id, name, getActivity());
            }

            if (txtProfileDoB.getText().toString().trim().length() > 0) {
                String DoB = txtProfileDoB.getText().toString().trim();
                PHPHelper.updateUserDoB(MainActivity.user_id, DoB, getActivity());
            }

            int gender = 0;
            switch (rbGroupProfileGender.getCheckedRadioButtonId()) {
                case R.id.rbProfileMale:
                    gender = 1;
                    break;
                case R.id.rbProfileFemale:
                    gender = 2;
                    break;
                default:
                    break;
            }
            if (gender != 0)
                PHPHelper.updateUserGender(MainActivity.user_id, gender, getActivity());

            IdWithNameListItem item = (IdWithNameListItem) spinProfileNationality.getSelectedItem();
            String nationality = item.getId();
            PHPHelper.updateUserNationality(MainActivity.user_id, nationality, getActivity());

            if (txtProfileCardId.getText().toString().trim().length() > 0) {
                String card_id = txtProfileCardId.getText().toString().trim();
                PHPHelper.updateUserCard(MainActivity.user_id, card_id, getActivity());
            }

            if (txtProfilePhone.getText().toString().trim().length() > 0) {
                String phone = txtProfilePhone.getText().toString().trim();
                PHPHelper.updateUserPhone(MainActivity.user_id, phone, getActivity());
            }
        } else {
            String name = txtProfilePersonalName.getText().toString();
            String DoB = txtProfileDoB.getText().toString();
            String card = txtProfileCardId.getText().toString();
            String phone = txtProfilePhone.getText().toString();

            int gender = 0;
            switch (rbGroupProfileGender.getCheckedRadioButtonId()) {
                case R.id.rbProfileMale:
                    gender = 1;
                    break;
                case R.id.rbProfileFemale:
                    gender = 2;
                    break;
                default:
                    break;
            }
            IdWithNameListItem nat = (IdWithNameListItem) spinProfileNationality.getSelectedItem();
            String nationality = nat.getId();

            IdWithNameListItem item = (IdWithNameListItem) spinProfileFamilyMember.getSelectedItem();
            if (!item.getId().isEmpty())
                dbHelper.updateFamilyMember(Integer.parseInt(item.getId()), name, DoB, gender, nationality, card, phone);
            else {
                if (item.getName().contains("New family member"))
                    dbHelper.addFamilyMember(MainActivity.user_id, name, DoB, gender, nationality, card, phone);
            }
        }
    };
}