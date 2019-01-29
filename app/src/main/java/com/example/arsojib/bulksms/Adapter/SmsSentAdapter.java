package com.example.arsojib.bulksms.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.Util;

import java.util.ArrayList;

public class SmsSentAdapter extends RecyclerView.Adapter<SmsSentAdapter.ViewHolder> {

    Context context;
    private ArrayList<Contact> arrayList;

    public SmsSentAdapter(Context context, ArrayList<Contact> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sms_history_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String  number = arrayList.get(i).getNumber();
        int status = arrayList.get(i).getStatus();
        long time = arrayList.get(i).getTime();

        viewHolder.number.setText(number);
        viewHolder.status.setText(Util.getStatus(status));
        viewHolder.time.setText(Util.getDateFromLong(time));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView number, status, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            number = itemView.findViewById(R.id.number);
            status = itemView.findViewById(R.id.status);
            time = itemView.findViewById(R.id.time);

        }
    }

}
