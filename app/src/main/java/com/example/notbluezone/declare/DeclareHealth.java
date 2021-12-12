package com.example.notbluezone.declare;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.notbluezone.MainActivity;
import com.example.notbluezone.R;
import com.example.notbluezone.adapters.AddressListAdapter;
import com.example.notbluezone.adapters.AddressModel;
import com.example.notbluezone.adapters.IdWithNameListItem;
import com.example.notbluezone.sqlite.ContractClass;
import com.example.notbluezone.sqlite.DatabaseHelper;
import com.example.notbluezone.sqlite.PHPHelper;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DeclareHealth extends Fragment {
    Spinner spinSelectFamilyMember, spinNationality, spinCity, spinDistrict, spinVaxPriority, spinVax;
    EditText txtDeclareName, declareDoB, txtDeclareCardId, txtDeclarePhone, txtDeclareAddress, txtDeclareOccupation,
            txtVaxDate, txtDoseNumber, txtLevel2AllergensList, txtComplicationsVaxList, txtAcuteDiseaseList,
    txtProgressingChronicList, txtOtherAllergens;
    RadioGroup rbGroupGender;
    RadioButton  rbMale, rbFemale;
    CheckBox rbOnBehalf, rbChestPain, rbCough, rbDiarrhea, rbFatigue, rbFever, rbHeadache, rbNoBreath, rbPain, rbSenseLoss,
            rbRedEye, rbSoreThroat, rbNoTalk, rbRash, rbComplication1, rbComplication2, rbComplication3, rbComplication4,
            rbComplication5, rbComplication6, rbComplication7, rbComplication8, rbComplication9, rbComplication11,
            rbComplication12, rbConfirm, rbContactWithSuspect, rbContactWithNational, rbContactWithSymptoms;
    Button btnAddDeclareAddress, btnSubmitForm;
    RelativeLayout layout_vax, layout_health;
    RecyclerView rvAddress;

    ArrayList<IdWithNameListItem> spinFamily, spinNat, spinCty, spinVaxPrio, spinVaxList;
    ArrayList<AddressModel> addresses;
    DatabaseHelper dbHelper;

    private boolean isDeclareHealth;

    public DeclareHealth() { }

    public static DeclareHealth newInstance() {
        DeclareHealth fragment = new DeclareHealth();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            if (getArguments().containsKey("DECLARE_HEALTH"))
                isDeclareHealth = true;
            else {
                if (getArguments().containsKey("DECLARE_VAX"))
                    isDeclareHealth = false;
                else
                    Toast.makeText(getActivity(), "ERROR BUNDLE_NOT_FOUND", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_declare_health, container, false);
        dbHelper = new DatabaseHelper(getActivity());

        initComponents(view);

        return view;
    }

    public void initComponents(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.declare_health));
        toolbar.setNavigationOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack(null, 0);
        });

        spinSelectFamilyMember = view.findViewById(R.id.spinSelectFamilyMember);
        spinFamily = new ArrayList<>();
        spinFamily.add(new IdWithNameListItem("", "Select family member"));
        ArrayAdapter familyAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinFamily);
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
        spinSelectFamilyMember.setAdapter(familyAdapter);
        spinSelectFamilyMember.setOnItemSelectedListener(onFamilyMemberSelected);

        txtDeclareName = view.findViewById(R.id.txtDeclareName);
        rbOnBehalf = view.findViewById(R.id.rbOnBehalf);

        declareDoB = view.findViewById(R.id.declareDoB);
        declareDoB.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dateDialog= new DatePickerDialog(getActivity(), datePickerListener, mYear, mMonth, mDay);
            dateDialog.show();
        });

        rbGroupGender = view.findViewById(R.id.rbGroupGender);
        rbMale = view.findViewById(R.id.rbMale);
        rbFemale = view.findViewById(R.id.rbFemale);

        spinNationality = view.findViewById(R.id.spinNationality);
        spinNat = new ArrayList<>();
        ArrayAdapter spinnerAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinNat);
        PHPHelper.fetchNationality(spinNat, spinnerAdapter, getActivity());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNationality.setAdapter(spinnerAdapter);

        txtDeclareCardId = view.findViewById(R.id.txtDeclareCardId);
        txtDeclarePhone = view.findViewById(R.id.txtDeclarePhone);

        spinCity = view.findViewById(R.id.spinCity);
        spinCty = new ArrayList<>();
        ArrayAdapter cityAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinCty);
        PHPHelper.fetchCities(spinCty, cityAdapter, getActivity());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCity.setAdapter(cityAdapter);
        spinCity.setOnItemSelectedListener(onCitySelect);

        spinDistrict = view.findViewById(R.id.spinDistrict);
        txtDeclareAddress = view.findViewById(R.id.txtDeclareAddress);
        rbConfirm = view.findViewById(R.id.rbConfirm);

        if (isDeclareHealth) {
            layout_health = view.findViewById(R.id.layout_health);
            layout_health.setVisibility(View.VISIBLE);

            rbContactWithNational = view.findViewById(R.id.rbContactWithNational);
            rbContactWithSuspect = view.findViewById(R.id.rbContactWithSuspect);
            rbContactWithSymptoms = view.findViewById(R.id.rbContactWithSymptoms);

            rbChestPain = view.findViewById(R.id.rbChestPain);
            rbCough = view.findViewById(R.id.rbCough);
            rbDiarrhea = view.findViewById(R.id.rbDiarhhea);
            rbFatigue = view.findViewById(R.id.rbFatigue);
            rbFever = view.findViewById(R.id.rbFever);
            rbHeadache = view.findViewById(R.id.rbHeadache);
            rbNoBreath = view.findViewById(R.id.rbNoBreath);
            rbPain = view.findViewById(R.id.rbPain);
            rbSenseLoss = view.findViewById(R.id.rbSenseLoss);
            rbRedEye = view.findViewById(R.id.rbRedEye);
            rbSoreThroat = view.findViewById(R.id.rbSoreThroat);
            rbNoTalk = view.findViewById(R.id.rbNoTalk);
            rbRash = view.findViewById(R.id.rbRash);

            btnAddDeclareAddress = view.findViewById(R.id.btnAddDeclareAddress);
            rvAddress = view.findViewById(R.id.rvAddress);
            AddressListAdapter adapter = new AddressListAdapter(new ArrayList<>());
            rvAddress.setAdapter(adapter);
            rvAddress.setLayoutManager(new LinearLayoutManager(getActivity()));
            btnAddDeclareAddress.setOnClickListener(v -> {
                AddressListAdapter adapter1 = (AddressListAdapter) rvAddress.getAdapter();
                assert adapter1 != null;
                adapter1.addEmptyItem();
            });
        } else {
            layout_vax = view.findViewById(R.id.layout_vax);
            layout_vax.setVisibility(View.VISIBLE);

            txtDeclareOccupation = view.findViewById(R.id.txtDeclareOccupation);
            txtVaxDate = view.findViewById(R.id.txtVaxDate);
            txtVaxDate.setOnClickListener(v -> {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog= new DatePickerDialog(getActivity(), datePickerListener1, mYear, mMonth, mDay);
                dateDialog.show();
            });

            spinVaxPriority = view.findViewById(R.id.spinVaxPriority);
            spinVaxPrio = new ArrayList<>();
            ArrayAdapter spinnerAdapterVax = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinVaxPrio);
            PHPHelper.fetchVaxPriority(spinVaxPrio, spinnerAdapterVax, getActivity());
            spinnerAdapterVax.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinVaxPriority.setAdapter(spinnerAdapterVax);

            spinVax = view.findViewById(R.id.spinVax);
            spinVaxList = new ArrayList<>();
            ArrayAdapter spinnerAdapterVaxList = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinVaxList);
            PHPHelper.fetchVaxList(spinVaxList, spinnerAdapterVaxList, getActivity());
            spinnerAdapterVaxList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinVax.setAdapter(spinnerAdapterVaxList);

            txtDoseNumber = view.findViewById(R.id.txtDoseNumber);

            rbComplication1 = view.findViewById(R.id.rbComplication1);
            txtLevel2AllergensList = view.findViewById(R.id.txtLevel2AllergensList);
            rbComplication1.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked)
                    txtLevel2AllergensList.setVisibility(View.VISIBLE);
                else {
                    txtLevel2AllergensList.setText("");
                    txtLevel2AllergensList.setVisibility(View.GONE);
                }
            });

            rbComplication2 = view.findViewById(R.id.rbComplication2);
            rbComplication3 = view.findViewById(R.id.rbComplication3);
            txtComplicationsVaxList = view.findViewById(R.id.txtComplicationsVaxList);
            rbComplication3.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked)
                    txtComplicationsVaxList.setVisibility(View.VISIBLE);
                else {
                    txtComplicationsVaxList.setText("");
                    txtComplicationsVaxList.setVisibility(View.GONE);
                }
            });

            rbComplication4 = view.findViewById(R.id.rbComplication4);
            rbComplication5 = view.findViewById(R.id.rbComplication5);
            rbComplication6 = view.findViewById(R.id.rbComplication6);
            txtAcuteDiseaseList = view.findViewById(R.id.txtAcuteDiseaseList);
            rbComplication6.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked)
                    txtAcuteDiseaseList.setVisibility(View.VISIBLE);
                else {
                    txtAcuteDiseaseList.setText("");
                    txtAcuteDiseaseList.setVisibility(View.GONE);
                }
            });

            rbComplication7 = view.findViewById(R.id.rbComplication7);
            txtProgressingChronicList = view.findViewById(R.id.txtProgressingChronicList);
            rbComplication7.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked)
                    txtProgressingChronicList.setVisibility(View.VISIBLE);
                else {
                    txtProgressingChronicList.setText("");
                    txtProgressingChronicList.setVisibility(View.GONE);
                }
            });

            rbComplication8 = view.findViewById(R.id.rbComplication8);
            rbComplication9 = view.findViewById(R.id.rbComplication9);
            rbComplication11 = view.findViewById(R.id.rbComplication11);
            rbComplication12 = view.findViewById(R.id.rbComplication12);
            txtOtherAllergens = view.findViewById(R.id.txtOtherAllergens);
            rbComplication12.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked)
                    txtOtherAllergens.setVisibility(View.VISIBLE);
                else {
                    txtOtherAllergens.setText("");
                    txtOtherAllergens.setVisibility(View.GONE);
                }
            });
        }
        btnSubmitForm = view.findViewById(R.id.btnSubmitForm);
        btnSubmitForm.setOnClickListener(onSendFormButtonClick);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            declareDoB.setText(date);
        }
    };

    private DatePickerDialog.OnDateSetListener datePickerListener1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            txtVaxDate.setText(date);
        }
    };

    AdapterView.OnItemSelectedListener onCitySelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            IdWithNameListItem item = (IdWithNameListItem) parent.getItemAtPosition(position);
            if (!item.getId().isEmpty()) {
                int city_id = Integer.parseInt(item.getId());

                ArrayList<IdWithNameListItem> array = new ArrayList<>();
                ArrayAdapter spinnerAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, array);
                PHPHelper.fetchDistricts(city_id, array, spinnerAdapter, getActivity());
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinDistrict.setAdapter(spinnerAdapter);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener onFamilyMemberSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            IdWithNameListItem item = (IdWithNameListItem) parent.getItemAtPosition(position);
            if (!item.getName().contains("Select family member")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage("Autofill data for this family member?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        (dialog, id12) -> {
                            int fam_id = Integer.parseInt(item.getId());

                            Cursor cursor = dbHelper.getFamilyMember(fam_id);
                            if (cursor != null) {
                                while (cursor.moveToNext()) {
                                    txtDeclareName.setText(cursor.getString(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_NAME)));

                                    if (fam_id != dbHelper.getFamilyMemberIdOfUser(MainActivity.user_id))
                                        rbOnBehalf.setChecked(true);

                                    declareDoB.setText(cursor.getString(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_DOB)));

                                    switch(cursor.getInt(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_GENDER))) {
                                        case 1:
                                            rbMale.setChecked(true);
                                            break;
                                        case 2:
                                            rbFemale.setChecked(true);
                                            break;
                                        default:
                                            break;
                                    }

                                    String nationality = cursor.getString(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_NAT));
                                    for (int i = 0; i < spinNationality.getAdapter().getCount(); i++) {
                                        if (spinNationality.getAdapter().getItem(i).toString().contains(nationality)) {
                                            spinNationality.setSelection(i);
                                            break;
                                        }
                                    }

                                    txtDeclareCardId.setText(cursor.getString(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_CARD)));
                                    txtDeclarePhone.setText(cursor.getString(cursor.getColumnIndex(ContractClass.CFamilyMember.KEY_PHONE)));

                                    PHPHelper.setUpAddressFragment(MainActivity.user_id, txtDeclareAddress, spinCity, spinDistrict, getActivity());

                                    if (isDeclareHealth) {
                                        ArrayList<String> symptoms = dbHelper.getSymptomHistoryDetailPrev14Days(fam_id);
                                        if (symptoms != null) {
                                            for (int i = 0; i < symptoms.size(); i++) {
                                                switch (symptoms.get(i)) {
                                                    case "CHESTPAIN":
                                                        rbChestPain.setChecked(true);
                                                        break;
                                                    case "COUGH":
                                                        rbCough.setChecked(true);
                                                        break;
                                                    case "DIARRHEA":
                                                        rbDiarrhea.setChecked(true);
                                                        break;
                                                    case "FATIGUE":
                                                        rbFatigue.setChecked(true);
                                                        break;
                                                    case "FEVER":
                                                        rbFever.setChecked(true);
                                                        break;
                                                    case "HEADACHE":
                                                        rbHeadache.setChecked(true);
                                                        break;
                                                    case "NOBREATH":
                                                        rbNoBreath.setChecked(true);
                                                        break;
                                                    case "NOTALK":
                                                        rbNoTalk.setChecked(true);
                                                        break;
                                                    case "PAIN":
                                                        rbPain.setChecked(true);
                                                        break;
                                                    case "RASH":
                                                        rbRash.setChecked(true);
                                                        break;
                                                    case "REDEYE":
                                                        rbRedEye.setChecked(true);
                                                        break;
                                                    case "SENSELOSS":
                                                        rbSenseLoss.setChecked(true);
                                                        break;
                                                    case "SORETHROAT":
                                                        rbSoreThroat.setChecked(true);
                                                        break;
                                                }
                                            }
                                        }

                                        Cursor travelHistoryCursor = dbHelper.getTravelHistoryPast14Days();
                                        if (travelHistoryCursor != null) {
                                            travelHistoryCursor.moveToFirst();
                                            ArrayList<AddressModel> history = new ArrayList<>();
                                            do {
                                                String city = travelHistoryCursor.getString(travelHistoryCursor.getColumnIndex(ContractClass.CTravelHistory.KEY_CITY));
                                                String district = travelHistoryCursor.getString(travelHistoryCursor.getColumnIndex(ContractClass.CTravelHistory.KEY_DISTRICT));
                                                String dateOfVisit = travelHistoryCursor.getString(travelHistoryCursor.getColumnIndex(ContractClass.CTravelHistory.KEY_DATE));
                                                String address = travelHistoryCursor.getString(travelHistoryCursor.getColumnIndex(ContractClass.CTravelHistory.KEY_ADDRESS));

                                                if (district.length() > 0)
                                                    history.add(new AddressModel(city, address, dateOfVisit));
                                                else
                                                    history.add(new AddressModel(city, district, address, dateOfVisit));
                                            } while (travelHistoryCursor.moveToNext());
                                            AddressListAdapter adapter = new AddressListAdapter(history);
                                            rvAddress.setAdapter(adapter);
                                        }
                                    }
                                    else {
                                        PHPHelper.getLastVaxForm(MainActivity.user_id, spinVax, txtDoseNumber, getActivity());
                                    }
                                }
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        (dialog, id1) -> dialog.cancel());

                AlertDialog alert11 = builder1.create();
                alert11.show();
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage("This will reset all your fields. Continue?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        (dialog, id14) -> {
                            txtDeclareName.setText("");
                            rbOnBehalf.setChecked(false);
                            declareDoB.setText("");
                            rbMale.setChecked(false);
                            rbFemale.setChecked(false);
                            spinNationality.setSelection(0);
                            txtDeclareCardId.setText("");
                            txtDeclarePhone.setText("");
                            spinCity.setSelection(0);
                            txtDeclareAddress.setText("");
                            rbConfirm.setChecked(false);

                            if (isDeclareHealth) {
                                rbContactWithNational.setChecked(false);
                                rbContactWithSuspect.setChecked(false);
                                rbContactWithSymptoms.setChecked(false);
                                rbChestPain.setChecked(false);
                                rbCough.setChecked(false);
                                rbDiarrhea.setChecked(false);
                                rbFatigue.setChecked(false);
                                rbFever.setChecked(false);
                                rbHeadache.setChecked(false);
                                rbNoBreath.setChecked(false);
                                rbPain.setChecked(false);
                                rbSenseLoss.setChecked(false);
                                rbRedEye.setChecked(false);
                                rbSoreThroat.setChecked(false);
                                rbNoTalk.setChecked(false);
                                rbRash.setChecked(false);
                                AddressListAdapter adapter = new AddressListAdapter(new ArrayList<>());
                                rvAddress.setAdapter(adapter);
                            } else {
                                txtDeclareOccupation.setText("");
                                txtVaxDate.setText("");
                                spinVaxPriority.setSelection(0);
                                spinVax.setSelection(0);
                                txtDoseNumber.setText("");
                                rbComplication1.setChecked(false);
                                rbComplication2.setChecked(false);
                                rbComplication3.setChecked(false);
                                rbComplication4.setChecked(false);
                                rbComplication5.setChecked(false);
                                rbComplication6.setChecked(false);
                                rbComplication7.setChecked(false);
                                rbComplication8.setChecked(false);
                                rbComplication9.setChecked(false);
                                rbComplication11.setChecked(false);
                                rbComplication12.setChecked(false);
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        (dialog, id13) -> dialog.cancel());

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    View.OnClickListener onSendFormButtonClick = v -> {
        if (rbConfirm.isChecked()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            String date = format.format(currentDate);

            String name;
            if (txtDeclareName.getText().toString().trim().length() > 0)
                name = txtDeclareName.getText().toString().trim();
            else {
                Toast.makeText(getActivity(), "You must input a name!", Toast.LENGTH_SHORT).show();
                return;
            }

            int onBehalf = 0;
            if (rbOnBehalf.isChecked())
                onBehalf = 1;

            String DoB;
            if (declareDoB.getText().toString().trim().length() > 0)
                DoB = declareDoB.getText().toString().trim();
            else {
                Toast.makeText(getActivity(), "You must input a date of birth!", Toast.LENGTH_SHORT).show();
                return;
            }

            int gender = 0;
            if (rbMale.isChecked())
                gender = 1;
            if (rbFemale.isChecked())
                gender = 2;

            IdWithNameListItem nationality = (IdWithNameListItem) spinNationality.getSelectedItem();
            String nat_id = nationality.getId();

            String card_id;
            if (txtDeclareCardId.getText().toString().trim().length() > 0)
                card_id = txtDeclareCardId.getText().toString().trim();
            else {
                Toast.makeText(getActivity(), "You must input your national ID!", Toast.LENGTH_SHORT).show();
                return;
            }

            String phone;
            if (txtDeclarePhone.getText().toString().trim().length() > 0)
                phone = txtDeclarePhone.getText().toString().trim();
            else {
                Toast.makeText(getActivity(), "You must input a phone number!", Toast.LENGTH_SHORT).show();
                return;
            }

            int city_id;
            IdWithNameListItem city = (IdWithNameListItem) spinCity.getSelectedItem();
            if (city.toString().contains("Select city")) {
                Toast.makeText(getActivity(), "You must select a city!", Toast.LENGTH_SHORT).show();
                return;
            }
            else
                city_id = Integer.parseInt(city.getId());

            int district_id;
            IdWithNameListItem district = (IdWithNameListItem) spinDistrict.getSelectedItem();
            if (district.toString().contains("Select district"))
                district_id = 0;
            else
                district_id = Integer.parseInt(city.getId());

            String address;
            if (txtDeclareAddress.getText().toString().trim().length() > 0)
                address = txtDeclareAddress.getText().toString().trim();
            else {
                Toast.makeText(getActivity(), "You must input an address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isDeclareHealth) {
                int ContactWithSuspect = 0, ContactWithNational = 0, ContactWithSymptoms = 0, ChestPain = 0, Cough = 0,
                        Diarrhea = 0, Fatigue = 0, Fever = 0, Headache = 0, NoBreath = 0, Pain = 0, SenseLoss = 0, RedEye = 0,
                        SoreThroat = 0, NoTalk = 0, Rash = 0;

                if (rbContactWithSuspect.isChecked())
                    ContactWithSuspect = 1;

                if (rbContactWithNational.isChecked())
                    ContactWithNational = 1;

                if (rbContactWithSymptoms.isChecked())
                    ContactWithSymptoms = 1;

                if (rbChestPain.isChecked())
                    ChestPain = 1;

                if (rbCough.isChecked())
                    Cough = 1;

                if (rbDiarrhea.isChecked())
                    Diarrhea = 1;

                if (rbFatigue.isChecked())
                    Fatigue = 1;

                if (rbFever.isChecked())
                    Fever = 1;

                if (rbHeadache.isChecked())
                    Headache = 1;

                if (rbNoBreath.isChecked())
                    NoBreath = 1;

                if (rbPain.isChecked())
                    Pain = 1;

                if (rbSenseLoss.isChecked())
                    SenseLoss = 1;

                if (rbRedEye.isChecked())
                    RedEye = 1;

                if (rbSoreThroat.isChecked())
                    SoreThroat = 1;

                if (rbRash.isChecked())
                    Rash = 1;

                if (rbNoTalk.isChecked())
                    NoTalk = 1;

                PHPHelper.addHealthForm(MainActivity.user_id, onBehalf, date, name, DoB, card_id, nat_id, gender, phone,
                        city_id, district_id, address, ContactWithSuspect, ContactWithNational, ContactWithSymptoms,
                        ChestPain, Cough, Diarrhea, Fatigue, Fever, Headache, NoBreath, Pain, SenseLoss, RedEye, SoreThroat,
                        NoTalk, Rash, rvAddress, getActivity());
            } else {
                String occupation = "";
                if (txtDeclareOccupation.getText().toString().trim().length() > 0)
                    occupation = txtDeclareOccupation.getText().toString().trim();

                String vaxDate = "";
                if (txtVaxDate.getText().toString().trim().length() > 0)
                    vaxDate = txtVaxDate.getText().toString().trim();

                IdWithNameListItem priority = (IdWithNameListItem) spinVaxPriority.getSelectedItem();
                int priority_id = Integer.parseInt(priority.getId());

                IdWithNameListItem vax = (IdWithNameListItem) spinVax.getSelectedItem();
                String vax_id = vax.getId();

                int dose = 1;
                if (txtDoseNumber.getText().toString().trim().length() > 0)
                    dose = Integer.parseInt(txtDoseNumber.getText().toString());

                int com1 = 0, com2 = 0, com3 = 0, com4 = 0, com5 = 0, com6 = 0, com7 = 0, com8 = 0, com9 = 0, com11 = 0,
                        com12 = 0;
                String allergens = "", vaxList = "", acute = "", chronic = "", otherAllergens = "";

                if (rbComplication1.isChecked()) {
                    com1 = 1;
                    if (!txtLevel2AllergensList.getText().toString().isEmpty())
                        allergens = txtLevel2AllergensList.getText().toString();
                }

                if (rbComplication2.isChecked())
                    com2 = 1;

                if (rbComplication3.isChecked()) {
                    com3 = 1;
                    if (!txtComplicationsVaxList.getText().toString().isEmpty())
                        vaxList = txtComplicationsVaxList.getText().toString();
                }

                if (rbComplication4.isChecked())
                    com4 = 1;

                if (rbComplication5.isChecked())
                    com5 = 1;

                if (rbComplication6.isChecked()) {
                    com6 = 1;
                    if (!txtAcuteDiseaseList.getText().toString().isEmpty())
                        acute = txtAcuteDiseaseList.getText().toString();
                }

                if (rbComplication7.isChecked()) {
                    com7 = 1;
                    if (!txtProgressingChronicList.getText().toString().isEmpty())
                        chronic = txtProgressingChronicList.getText().toString();
                }

                if (rbComplication8.isChecked())
                    com8 = 1;

                if (rbComplication9.isChecked())
                    com9 = 1;

                if (rbComplication11.isChecked())
                    com11 = 1;

                if (rbComplication12.isChecked()) {
                    com12 = 1;
                    if (!txtOtherAllergens.getText().toString().isEmpty())
                        otherAllergens = txtOtherAllergens.getText().toString();
                }

                PHPHelper.addVaxForm(MainActivity.user_id, onBehalf, date, name, DoB, card_id, nat_id, gender, phone, city_id,
                        district_id, address, occupation, vaxDate, priority_id, vax_id, dose, com1, allergens, com2, com3,
                        vaxList, com4, com5, com6, acute, com7, chronic, com8, com9, com11, com12, otherAllergens, getActivity());
            }
        } else
            Toast.makeText(getActivity(), "Please read and agree to the ToS before sending.", Toast.LENGTH_SHORT).show();
    };
}