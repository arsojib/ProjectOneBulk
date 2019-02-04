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

import com.example.arsojib.bulksms.Listener.ClickListener;
import com.example.arsojib.bulksms.Model.Message;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.Util;

import java.util.ArrayList;

/**
 * Created by AR Sajib on 2/3/2019.
 */

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {

    Context context;
    private ArrayList<Message> arrayList;
    ClickListener clickListener;

    public ScheduleListAdapter(Context context, ArrayList<Message> arrayList, ClickListener clickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.schedule_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        String  message = arrayList.get(i).getMessage();
        long time = arrayList.get(i).getTime();
        int count = arrayList.get(i).getCount();

        viewHolder.message.setText(message);
        viewHolder.time.setText(Util.getDateFromLong(time));
        viewHolder.numberCount.setText("Total Number: " + count);

        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout;
        TextView message, numberCount, time;
        ImageView remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            message = itemView.findViewById(R.id.message);
            numberCount = itemView.findViewById(R.id.number_count);
            time = itemView.findViewById(R.id.time);
            remove = itemView.findViewById(R.id.remove);

        }
    }

}
