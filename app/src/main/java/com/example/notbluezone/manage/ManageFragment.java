package com.example.notbluezone.manage;

import android.database.Cursor;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notbluezone.MainActivity;
import com.example.notbluezone.R;
import com.example.notbluezone.account.AccountAddress;
import com.example.notbluezone.adapters.IdWithNameListItem;
import com.example.notbluezone.sqlite.ContractClass;
import com.example.notbluezone.sqlite.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ManageFragment extends Fragment {
    Spinner spinManageFamily;
    CheckBox rbChestPainManage, rbCoughManage, rbDiarrheaManage, rbFatigueManage, rbFeverManage, rbHeadacheManage, rbNoBreathManage,
    rbPainManage, rbSenseLossManage, rbRedEyesManage, rbSoreThroatManage, rbNoTalkManage, rbRashManage;
    Button btnTravelHistory;
    ArrayList<IdWithNameListItem> spinFamily;

    DatabaseHelper dbHelper;

    public ManageFragment() {
    }

    public static ManageFragment newInstance() {
        return new ManageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);
        dbHelper = new DatabaseHelper(getActivity());

        initComponents(view);

        return view;
    }

    public void initComponents(View view) {
        rbChestPainManage = view.findViewById(R.id.rbChestPainManage);
        rbChestPainManage.setOnCheckedChangeListener(onCheckboxCheck);
        rbCoughManage = view.findViewById(R.id.rbCoughManage);
        rbCoughManage.setOnCheckedChangeListener(onCheckboxCheck);
        rbDiarrheaManage = view.findViewById(R.id.rbDiarhheaManage);
        rbDiarrheaManage.setOnCheckedChangeListener(onCheckboxCheck);
        rbFatigueManage = view.findViewById(R.id.rbFatigueManage);
        rbFatigueManage.setOnCheckedChangeListener(onCheckboxCheck);
        rbFeverManage = view.findViewById(R.id.rbFeverManage);
        rbFeverManage.setOnCheckedChangeListener(onCheckboxCheck);
        rbHeadacheManage = view.findViewById(R.id.rbHeadacheManage);
        rbHeadacheManage.setOnCheckedChangeListener(onCheckboxCheck);
        rbNoBreathManage = view.findViewById(R.id.rbNoBreathManage);
        rbNoBreathManage.setOnCheckedChangeListener(onCheckboxCheck);
        rbPainManage = view.findViewById(R.id.rbPainManage);
        rbPainManage.setOnCheckedChangeListener(onCheckboxCheck);
        rbSenseLossManage = view.findViewById(R.id.rbSenseLossManage);
        rbSenseLossManage.setOnCheckedChangeListener(onCheckboxCheck);
        rbRedEyesManage = view.findViewById(R.id.rbRedEyeManage);
        rbRedEyesManage.setOnCheckedChangeListener(onCheckboxCheck);
        rbSoreThroatManage = view.findViewById(R.id.rbSoreThroatManage);
        rbSoreThroatManage.setOnCheckedChangeListener(onCheckboxCheck);
        rbNoTalkManage = view.findViewById(R.id.rbNoTalkManage);
        rbNoTalkManage.setOnCheckedChangeListener(onCheckboxCheck);
        rbRashManage = view.findViewById(R.id.rbRashManage);
        rbRashManage.setOnCheckedChangeListener(onCheckboxCheck);

        spinManageFamily = view.findViewById(R.id.spinManageFamily);
        spinFamily = new ArrayList<>();
        spinFamily.add(new IdWithNameListItem("", "Select family member"));
        ArrayAdapter familyAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, spinFamily);
        Cursor cursor = dbHelper.getFamilyMembers(MainActivity.user_id);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                IdWithNameListItem item = new IdWithNameListItem(Integer.toString(cursor.getInt(cursor.getColumnIndex(ContractClass.CFamilyMember._ID)))
                        , cursor.getString(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_NAME)));
                spinFamily.add(item);
                familyAdapter.notifyDataSetChanged();
            }
            cursor.close();
        }
        familyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinManageFamily.setAdapter(familyAdapter);
        spinManageFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rbChestPainManage.setChecked(false);
                rbCoughManage.setChecked(false);
                rbDiarrheaManage.setChecked(false);
                rbFatigueManage.setChecked(false);
                rbFeverManage.setChecked(false);
                rbHeadacheManage.setChecked(false);
                rbNoBreathManage.setChecked(false);
                rbNoTalkManage.setChecked(false);
                rbPainManage.setChecked(false);
                rbRashManage.setChecked(false);
                rbRedEyesManage.setChecked(false);
                rbSenseLossManage.setChecked(false);
                rbSoreThroatManage.setChecked(false);

                IdWithNameListItem item = (IdWithNameListItem) parent.getAdapter().getItem(position);
                if (!item.getName().contains("Select family member")) {
                    int item_id = Integer.parseInt(item.getId());

                    Date d = Calendar.getInstance().getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String date = sdf.format(d);

                    Cursor cursor = dbHelper.getSymptomHistoryDetail(item_id, date);
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            String symptom = cursor.getString(cursor.getColumnIndex(ContractClass.CSymptomDetails.KEY_SYMPTOM));
                            switch (symptom) {
                                case "CHESTPAIN":
                                    rbChestPainManage.setChecked(true);
                                    break;
                                case "COUGH":
                                    rbCoughManage.setChecked(true);
                                    break;
                                case "DIARRHEA":
                                    rbDiarrheaManage.setChecked(true);
                                    break;
                                case "FATIGUE":
                                    rbFatigueManage.setChecked(true);
                                    break;
                                case "FEVER":
                                    rbFeverManage.setChecked(true);
                                    break;
                                case "HEADACHE":
                                    rbHeadacheManage.setChecked(true);
                                    break;
                                case "NOBREATH":
                                    rbNoBreathManage.setChecked(true);
                                    break;
                                case "NOTALK":
                                    rbNoTalkManage.setChecked(true);
                                    break;
                                case "PAIN":
                                    rbPainManage.setChecked(true);
                                    break;
                                case "RASH":
                                    rbRashManage.setChecked(true);
                                    break;
                                case "REDEYE":
                                    rbRedEyesManage.setChecked(true);
                                    break;
                                case "SENSELOSS":
                                    rbSenseLossManage.setChecked(true);
                                    break;
                                case "SORETHROAT":
                                    rbSoreThroatManage.setChecked(true);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnTravelHistory = view.findViewById(R.id.btnTravelHistory);
        btnTravelHistory.setOnClickListener(v -> {
            Fragment newFragment = new ManageVisitedPlaces();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(((ViewGroup) getView().getParent()).getId(), newFragment, null)
                    .addToBackStack(null)
                    .commit();
        });
    }

    CompoundButton.OnCheckedChangeListener onCheckboxCheck = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Date d = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String date = sdf.format(d);

            int fam_id = 0;
            IdWithNameListItem family = (IdWithNameListItem) spinManageFamily.getSelectedItem();
            if (!family.toString().contains("Select family member")) {
                fam_id = Integer.parseInt(family.getId());

                int symptom_history_id = 0;
                if (dbHelper.getSymptomHistory(fam_id, date) != -1)
                    symptom_history_id = dbHelper.getSymptomHistory(fam_id, date);
                else
                    symptom_history_id = (int) dbHelper.addSymptomHistory(fam_id, date);

                switch (buttonView.getId()) {
                    case R.id.rbChestPainManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "CHESTPAIN");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "CHESTPAIN");
                        break;
                    case R.id.rbCoughManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "COUGH");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "COUGH");
                        break;
                    case R.id.rbDiarhheaManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "DIARRHEA");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "DIARRHEA");
                        break;
                    case R.id.rbFatigueManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "FATIGUE");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "FATIGUE");
                        break;
                    case R.id.rbFeverManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "FEVER");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "FEVER");
                        break;
                    case R.id.rbHeadacheManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "HEADACHE");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "HEADACHE");
                        break;
                    case R.id.rbNoBreathManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "NOBREATH");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "NOBREATH");
                        break;
                    case R.id.rbNoTalkManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "NOTALK");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "NOTALK");
                        break;
                    case R.id.rbPainManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "PAIN");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "PAIN");
                        break;
                    case R.id.rbRashManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "RASH");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "RASH");
                        break;
                    case R.id.rbRedEyeManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "REDEYE");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "REDEYE");
                        break;
                    case R.id.rbSenseLossManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "SENSELOSS");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "SENSELOSS");
                        break;
                    case R.id.rbSoreThroatManage:
                        if (isChecked)
                            dbHelper.addSymptom(symptom_history_id, "SORETHROAT");
                        else
                            dbHelper.deleteSymptom(symptom_history_id, "SORETHROAT");
                        break;
                    default:
                        Toast.makeText(getActivity(), "Unrecognized view/symptom!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}