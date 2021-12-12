package com.example.notbluezone.manage;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.ConditionVariable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notbluezone.R;
import com.example.notbluezone.adapters.HistoryAdapter;
import com.example.notbluezone.adapters.HistoryModel;
import com.example.notbluezone.sqlite.ContractClass;
import com.example.notbluezone.sqlite.DatabaseHelper;

import java.util.ArrayList;

public class ManageVisitedPlaces extends Fragment {
    public ManageVisitedPlaces() { }

    public static ManageVisitedPlaces newInstance() {
        return new ManageVisitedPlaces();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_visited_places, container, false);

        RecyclerView rvAddressHistory = view.findViewById(R.id.rvAddressHistory);
        ArrayList<HistoryModel> adapterList = new ArrayList<>();

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        Cursor cursor = dbHelper.getTravelHistory();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                HistoryModel address;
                if (cursor.getString(cursor.getColumnIndex(ContractClass.CTravelHistory.KEY_DISTRICT)).length() > 0)
                    address = new HistoryModel(cursor.getString(cursor.getColumnIndex(ContractClass.CTravelHistory.KEY_CITY)),
                            cursor.getString(cursor.getColumnIndex(ContractClass.CTravelHistory.KEY_DISTRICT)),
                            cursor.getString(cursor.getColumnIndex(ContractClass.CTravelHistory.KEY_ADDRESS)),
                            cursor.getString(cursor.getColumnIndex(ContractClass.CTravelHistory.KEY_DATE)));
                else
                    address = new HistoryModel(cursor.getString(cursor.getColumnIndex(ContractClass.CTravelHistory.KEY_CITY)),
                            cursor.getString(cursor.getColumnIndex(ContractClass.CTravelHistory.KEY_ADDRESS)),
                            cursor.getString(cursor.getColumnIndex(ContractClass.CTravelHistory.KEY_DATE)));

                adapterList.add(address);
            }
        }

        HistoryAdapter adapter = new HistoryAdapter(adapterList);
        rvAddressHistory.setAdapter(adapter);
        rvAddressHistory.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}