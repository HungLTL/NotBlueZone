package com.example.notbluezone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notbluezone.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View historyView = inflater.inflate(R.layout.address_history_row, parent, false);

        return new ViewHolder(historyView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HistoryAdapter.ViewHolder holder, int position) {
        HistoryModel history = mVisitedAddresses.get(position);

        holder.txtVisitedDistrict.setText(history.getDistrict());
        holder.txtVisitedDate.setText(history.getVisitDate());
        holder.txtVisitedCity.setText(history.getCity());
        holder.txtVisitedAddress.setText(history.getAddress());
    }

    @Override
    public int getItemCount() {
        return mVisitedAddresses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtVisitedCity, txtVisitedDistrict, txtVisitedAddress, txtVisitedDate;

        public ViewHolder(View itemView) {
            super(itemView);

            txtVisitedAddress = itemView.findViewById(R.id.txtVisitedAddress);
            txtVisitedCity = itemView.findViewById(R.id.txtVisitedCity);
            txtVisitedDate = itemView.findViewById(R.id.txtVisitedDate);
            txtVisitedDistrict = itemView.findViewById(R.id.txtVisitedDistrict);
        }
    }

    private List<HistoryModel> mVisitedAddresses;

    public HistoryAdapter(List<HistoryModel> visitedAddresses){
        mVisitedAddresses = visitedAddresses;
    }
}
