package com.example.arsojib.bulksms.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.Util;

import java.util.ArrayList;

public class SmsNotSentAdapter extends RecyclerView.Adapter<SmsNotSentAdapter.ViewHolder> {

    Context context;
    private ArrayList<Contact> arrayList;

    public SmsNotSentAdapter(Context context, ArrayList<Contact> arrayList) {
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        String  number = arrayList.get(i).getNumber();
        int status = arrayList.get(i).getStatus();
        long time = arrayList.get(i).getTime();
        boolean check = arrayList.get(i).isCheck();

        viewHolder.number.setText(number);
        viewHolder.status.setText(Util.getStatus(status));
        viewHolder.time.setText(Util.getDateFromLong(time));

        if (check) {
            viewHolder.select.setVisibility(View.VISIBLE);
        } else {
            viewHolder.select.setVisibility(View.GONE);
        }

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.get(i).setCheck(!arrayList.get(i).isCheck());
                notifyItemChanged(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout;
        TextView number, status, time;
        ImageView select;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            number = itemView.findViewById(R.id.number);
            status = itemView.findViewById(R.id.status);
            time = itemView.findViewById(R.id.time);
            select = itemView.findViewById(R.id.select);

        }
    }

}
