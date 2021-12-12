package com.example.notbluezone.account;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notbluezone.MainActivity;
import com.example.notbluezone.R;
import com.example.notbluezone.adapters.IdWithNameListItem;
import com.example.notbluezone.sqlite.PHPHelper;

import java.util.ArrayList;

public class AccountAddress extends Fragment {
    Spinner spinProfileCity, spinProfileDistrict;
    EditText txtProfileAddress;
    Button btnAddressSave;
    TextView txtGenerateQR;

    ArrayList<IdWithNameListItem> spinCity;

    public AccountAddress() { }

    public static AccountAddress newInstance() {
        return new AccountAddress();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_address, container, false);

        initComponents(view);

        return view;
    }

    public void initComponents(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.account_address));
        toolbar.setNavigationOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack(null, 0);
        });

        spinProfileCity = view.findViewById(R.id.spinProfileCity);
        spinCity = new ArrayList<>();
        ArrayAdapter spinnerAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinCity);
        PHPHelper.fetchCities(spinCity, spinnerAdapter, getActivity());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinProfileCity.setAdapter(spinnerAdapter);
        spinProfileCity.setOnItemSelectedListener(onCitySelect);

        spinProfileDistrict = view.findViewById(R.id.spinProfileDistrict);

        txtProfileAddress = view.findViewById(R.id.txtProfileAddress);

        btnAddressSave = view.findViewById(R.id.btnAddressSave);
        btnAddressSave.setOnClickListener(onSaveButtonClick);

        txtGenerateQR = view.findViewById(R.id.txtGenerateAddressQR);
        txtGenerateQR.setOnClickListener(onQrGenerationClick);

        PHPHelper.setUpAddressFragment(MainActivity.user_id, txtProfileAddress, spinProfileCity, spinProfileDistrict, getActivity());
    }

    AdapterView.OnItemSelectedListener onCitySelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            IdWithNameListItem item = (IdWithNameListItem) parent.getItemAtPosition(position);
            if (!item.getId().isEmpty()) {
                int city_id = Integer.parseInt(item.getId());

                ArrayList<IdWithNameListItem> spinDistrict = new ArrayList<>();
                ArrayAdapter spinnerAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, spinDistrict);
                PHPHelper.fetchDistricts(city_id, spinDistrict, spinnerAdapter, getActivity());
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinProfileDistrict.setAdapter(spinnerAdapter);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    View.OnClickListener onSaveButtonClick = v -> {
        if (spinProfileCity.getSelectedItem().toString().contains("Select city")) {
            Toast.makeText(getActivity(), "You must select a city!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinProfileCity.getSelectedItem().toString().contains("Ho Chi Minh City")
                || spinProfileCity.getSelectedItem().toString().contains("Hai Phong")
                || spinProfileCity.getSelectedItem().toString().contains("Ha Noi")
                || spinProfileCity.getSelectedItem().toString().contains("Da Nang")
                || spinProfileCity.getSelectedItem().toString().contains("Can Tho")) {
            if (spinProfileDistrict.getSelectedItem().toString().contains("Select district")) {
                Toast.makeText(getActivity(), "You must select a district!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (txtProfileAddress.getText().toString().trim().length() <= 0) {
            Toast.makeText(getActivity(), "You must input an address!", Toast.LENGTH_SHORT).show();
            return;
        }

        IdWithNameListItem city = (IdWithNameListItem) spinProfileCity.getSelectedItem();
        int city_id = Integer.parseInt(city.getId());

        int district_id = 0;
        IdWithNameListItem district = (IdWithNameListItem) spinProfileDistrict.getSelectedItem();
        if (!district.getId().equals(""))
            district_id = Integer.parseInt(district.getId());

        String address = txtProfileAddress.getText().toString().trim();

        PHPHelper.addOrUpdateAddress(MainActivity.user_id, city_id, district_id, address, getActivity());
    };

    View.OnClickListener onQrGenerationClick = v -> {
        IdWithNameListItem city = (IdWithNameListItem) spinProfileCity.getSelectedItem();
        String city_name = city.getName();

        String district_name = "";
        IdWithNameListItem district = (IdWithNameListItem) spinProfileDistrict.getSelectedItem();
        if (!district.getName().equals("Select district"))
            district_name = district.getName();

        String address = txtProfileAddress.getText().toString().trim();

        String QRCodeValue = "NotBlueZone QR Address: %" + city_name + "^" + (district_name.length() > 0 ? district_name : "") + "&" + address;

        Fragment newFragment = new AccountAddressQR();
        Bundle args = new Bundle();
        args.putString("QR_CODE", QRCodeValue);
        newFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(((ViewGroup) getView().getParent()).getId(), newFragment, null)
                .addToBackStack(null)
                .commit();
    };
}