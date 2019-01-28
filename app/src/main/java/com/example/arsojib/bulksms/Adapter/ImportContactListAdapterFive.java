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

import com.example.arsojib.bulksms.Listener.ContactRemoveListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.Model.Group;
import com.example.arsojib.bulksms.R;

import java.util.ArrayList;

/**
 * Created by AR Sajib on 1/28/2019.
 */

public class ImportContactListAdapterFive extends RecyclerView.Adapter<ImportContactListAdapterFive.ViewHolder> {

    Context context;
    private ArrayList<Group> arrayList;
    private ContactRemoveListener contactRemoveListener;

    public ImportContactListAdapterFive(Context context, ArrayList<Group> arrayList, ContactRemoveListener contactRemoveListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.contactRemoveListener = contactRemoveListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item_three, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final String id, name, count;
        id = arrayList.get(i).getId();
        name = arrayList.get(i).getTitle();
        count = arrayList.get(i).getCount();

        viewHolder.name.setText(name);
        viewHolder.number.setText(count);

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactRemoveListener.onContactRemove(id, i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout;
        TextView name, number;
        ImageView remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
            remove = itemView.findViewById(R.id.remove);

        }
    }

}
