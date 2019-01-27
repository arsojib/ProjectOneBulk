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
import com.example.arsojib.bulksms.R;

import java.util.ArrayList;

public class ImportContactListAdapterTwo extends RecyclerView.Adapter<ImportContactListAdapterTwo.ViewHolder> {

    Context context;
    private ArrayList<Contact> arrayList;
    private ContactRemoveListener contactRemoveListener;

    public ImportContactListAdapterTwo(Context context, ArrayList<Contact> arrayList, ContactRemoveListener contactRemoveListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.contactRemoveListener = contactRemoveListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item_two, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        String name, number;
        boolean check;
        name = arrayList.get(i).getName();
        number = arrayList.get(i).getNumber();
        check = arrayList.get(i).isCheck();

        viewHolder.name.setText(name);
        viewHolder.number.setText(number);

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
        TextView name, number;
        ImageView select;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
            select = itemView.findViewById(R.id.select);

        }
    }

}
