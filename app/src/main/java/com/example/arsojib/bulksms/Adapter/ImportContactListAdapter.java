package com.example.arsojib.bulksms.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arsojib.bulksms.Listener.ContactRemoveListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;

import java.util.ArrayList;

/**
 * Created by AR Sajib on 1/27/2019.
 */

public class ImportContactListAdapter extends RecyclerView.Adapter<ImportContactListAdapter.ViewHolder> {

    Context context;
    private ArrayList<Contact> arrayList;
    private ContactRemoveListener contactRemoveListener;

    public ImportContactListAdapter(Context context, ArrayList<Contact> arrayList, ContactRemoveListener contactRemoveListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.contactRemoveListener = contactRemoveListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final String number = arrayList.get(i).getNumber();
        viewHolder.number.setText(number);
        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactRemoveListener.onContactRemove(number, i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView number;
        ImageView remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            number = itemView.findViewById(R.id.number);
            remove = itemView.findViewById(R.id.remove);

        }
    }

}
