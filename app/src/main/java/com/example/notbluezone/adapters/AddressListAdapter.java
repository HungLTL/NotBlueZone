package com.example.notbluezone.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notbluezone.R;
import com.example.notbluezone.sqlite.PHPHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> {
    Context context;

    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View addressView = inflater.inflate(R.layout.address_recycler_row, parent, false);
        return new ViewHolder(addressView);
    }

    @Override
    public void onBindViewHolder(@NotNull AddressListAdapter.ViewHolder holder, int position) {
        holder.spinAdapterCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                IdWithNameListItem item = (IdWithNameListItem) parent.getItemAtPosition(pos);
                if (!item.getId().isEmpty()) {
                    int city_id = Integer.parseInt(item.getId());
                    String city = item.getName();
                    mAddressList.get(position).setCityId(city);

                    ArrayList<IdWithNameListItem> spinDistrict = new ArrayList<>();
                    ArrayAdapter spinnerAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, spinDistrict);
                    PHPHelper.fetchDistricts(city_id, spinDistrict, spinnerAdapter, context);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    holder.spinAdapterDistrict.setAdapter(spinnerAdapter);
                }
                else
                    mAddressList.get(position).setCityId("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.spinAdapterDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                IdWithNameListItem item = (IdWithNameListItem) parent.getItemAtPosition(pos);
                if (!item.getId().isEmpty())
                    mAddressList.get(position).setDistrictId(item.getName());
                else
                    mAddressList.get(position).setDistrictId("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.txtAdapterAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAddressList.get(position).setAddress(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        DatePickerDialog.OnDateSetListener datePickerListener = (view, year, monthOfYear, dayOfMonth) -> {
            String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            holder.txtAdapterDate.setText(date);
            mAddressList.get(position).setDateOfVisit(date);
        };

        holder.txtAdapterDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dateDialog= new DatePickerDialog(context, datePickerListener, mYear, mMonth, mDay);
            dateDialog.show();
        });

        holder.txtAdapterDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAddressList.get(position).setDateOfVisit(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AddressModel address = mAddressList.get(position);
        if (!address.getCityId().isEmpty()) {
            final Handler handler1 = new Handler();
            handler1.postDelayed(() -> {
                for (int j = 0; j < holder.spinAdapterCity.getAdapter().getCount(); j++) {
                    if (holder.spinAdapterCity.getAdapter().getItem(j).toString().contains(address.getCityId())) {
                        holder.spinAdapterCity.setSelection(j);

                        final Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            if (address.getCityId().equals("Ho Chi Minh City") || address.getCityId().equals("Hai Phong")
                            || address.getCityId().equals("Ha Noi") || address.getCityId().equals("Da Nang")
                            || address.getCityId().equals("Can Tho")) {
                                for (int k = 0; k < holder.spinAdapterDistrict.getAdapter().getCount(); k++) {
                                    if (holder.spinAdapterDistrict.getAdapter().getItem(k).toString().contains(address.getDistrictId())) {
                                        holder.spinAdapterDistrict.setSelection(k);
                                        break;
                                    }
                                }
                            }
                        }, 1000);
                        break;
                    }
                }
            }, 1000);
        }

        if (address.getAddress().length() > 0)
            holder.txtAdapterAddress.setText(address.getAddress());

        if (address.getDateOfVisit().length() > 0)
            holder.txtAdapterDate.setText(address.getDateOfVisit());

        holder.btnDeleteItem.setOnClickListener(v -> {
            mAddressList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mAddressList.size());
        });
    }

    @Override
    public int getItemCount() {
        return mAddressList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Spinner spinAdapterCity, spinAdapterDistrict;
        public EditText txtAdapterAddress, txtAdapterDate;
        public ImageButton btnDeleteItem;

        public ViewHolder(View itemView) {
            super(itemView);

            spinAdapterCity = itemView.findViewById(R.id.spinAdapterCity);
            ArrayList<IdWithNameListItem> spinCity = new ArrayList<>();
            ArrayAdapter spinnerAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, spinCity);
            PHPHelper.fetchCities(spinCity, spinnerAdapter, context);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinAdapterCity.setAdapter(spinnerAdapter);

            spinAdapterDistrict = itemView.findViewById(R.id.spinAdapterDistrict);
            txtAdapterAddress = itemView.findViewById(R.id.txtAdapterAddress);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
            txtAdapterDate = itemView.findViewById(R.id.txtAdapterDate);
        }
    }

    private List<AddressModel> mAddressList;

    public AddressListAdapter(List<AddressModel> list) {
        mAddressList = list;
    }

    public void addEmptyItem() {
        AddressModel address = new AddressModel("", "", "");
        mAddressList.add(address);
        notifyItemInserted(mAddressList.size() - 1);
    }
}
