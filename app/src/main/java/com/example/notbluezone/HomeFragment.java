package com.example.notbluezone;

import android.app.AlertDialog;
import android.app.slice.SliceItem;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notbluezone.adapters.IdWithNameListItem;
import com.example.notbluezone.adapters.SlideItem;
import com.example.notbluezone.adapters.SliderAdapter;
import com.example.notbluezone.sqlite.ContractClass;
import com.example.notbluezone.sqlite.DatabaseHelper;
import com.example.notbluezone.sqlite.PHPHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    TextView qrCodeTitle, txtLastDeclarationHome, txtLastDeclarationHomeWarning, txtVaxStatusHome;
    ViewPager2 vpQRCodes;
    Spinner spinHomeFamilyMember;
    ArrayList<IdWithNameListItem> spinFamily;
    ImageButton btnScan;

    public HomeFragment() { }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initComponents(view);
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());

        if (MainActivity.user_id > 0) {
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
            spinHomeFamilyMember.setAdapter(familyAdapter);
            spinHomeFamilyMember.setOnItemSelectedListener(onFamilyMemberSelected);

            PHPHelper.setUpHomeFragmentHealth(MainActivity.user_id, txtLastDeclarationHome, txtLastDeclarationHomeWarning, getActivity());
            PHPHelper.setUpHomeFragmentVax(MainActivity.user_id, txtVaxStatusHome, getActivity());
        }
        else {
            txtLastDeclarationHome.setText(R.string.home_not_logged_in_declare);
            txtLastDeclarationHome.setTextColor(ContextCompat.getColor(requireActivity(), R.color.crimson));
            txtVaxStatusHome.setText(R.string.home_not_logged_in_vax);
            txtVaxStatusHome.setTextColor(ContextCompat.getColor(requireActivity(), R.color.crimson));
        }

        return view;
    }

    void initComponents(View view) {
        qrCodeTitle = view.findViewById(R.id.qrCodeTitle);
        spinHomeFamilyMember = view.findViewById(R.id.spinHomeFamilyMember);
        txtLastDeclarationHome = view.findViewById(R.id.txtLastDeclarationHome);
        txtLastDeclarationHomeWarning = view.findViewById(R.id.txtLastDeclarationHomeWarning);
        txtVaxStatusHome = view.findViewById(R.id.txtVaxStatusHome);
        vpQRCodes = view.findViewById(R.id.vpQRCodes);
        btnScan = view.findViewById(R.id.btnScan);
        btnScan.setOnClickListener(onScanButtonClickListener);
    }

    AdapterView.OnItemSelectedListener onFamilyMemberSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            IdWithNameListItem item = (IdWithNameListItem) spinHomeFamilyMember.getSelectedItem();
            if (!item.toString().contains("Select family member")) {
                ArrayList<SlideItem> qrCodes = new ArrayList<>();
                SliderAdapter adapter = new SliderAdapter(qrCodes, vpQRCodes);
                PHPHelper.buildVaxQRCode(MainActivity.user_id, item.getName(), qrCodes, adapter, getActivity());
                PHPHelper.buildHealthQRCode(MainActivity.user_id, item.getName(), qrCodes, adapter, getActivity());
                vpQRCodes.setAdapter(adapter);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    View.OnClickListener onScanButtonClickListener = v -> {
        ScanOptions options = new ScanOptions();
        Intent intent = options.createScanIntent(getActivity());
        startActivity(intent);
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,data);
        if (intentResult.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            if (intentResult.getContents().contains("QR Address")) {
                DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                String city = "", district = "", address = "";
                int phase = 0;
                for (int i = 0; i < intentResult.getContents().length();i++) {
                    char c = intentResult.getContents().charAt(i);
                    switch (c) {
                        case '%':
                            phase = 1;
                            break;
                        case '^':
                            phase = 2;
                            break;
                        case '&':
                            phase = 3;
                            break;
                        default:
                        {
                            switch (phase) {
                                case 1:
                                    city = city.concat(String.valueOf(c));
                                    break;
                                case 2:
                                    district = district.concat(String.valueOf(c));
                                    break;
                                case 3:
                                    address = address.concat(String.valueOf(c));
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    }
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                String dateString = sdf.format(date);

                dbHelper.addTravelHistory(address,city, district, dateString);
                builder.setMessage("Travel history updated!");
            } else
                builder.setMessage(intentResult.getContents());

            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        } else
            Toast.makeText(getActivity(), "Scanner picked up nothing.", Toast.LENGTH_SHORT).show();
    }
}