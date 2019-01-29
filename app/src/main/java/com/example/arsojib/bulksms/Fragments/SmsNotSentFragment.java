package com.example.arsojib.bulksms.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.arsojib.bulksms.Activites.MainActivity;
import com.example.arsojib.bulksms.Adapter.SmsSentAdapter;
import com.example.arsojib.bulksms.DataFetch.DatabaseHelper;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.Util;

import java.util.ArrayList;

public class SmsNotSentFragment extends Fragment {

    View view;
    CheckBox selectAll;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView alert, done;

    ArrayList<Contact> arrayList;
    SmsSentAdapter smsSentAdapter;
    DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sms_not_sent_layout, container, false);

        initialComponent();
        getData();

        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        arrayList.get(i).setCheck(true);
                    }
                } else {
                    for (int i = 0; i < arrayList.size(); i++) {
                        arrayList.get(i).setCheck(false);
                    }
                }
                notifyChange();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Contact> contacts = new ArrayList<>();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).isCheck()) {
                        contacts.add(new Contact("", arrayList.get(i).getNumber(), false));
                    }
                }
                ((MainActivity) getContext()).contactImportCompleteListener.onImportComplete(contacts);
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void getData() {
        arrayList = databaseHelper.getAllNumberUsingMessageID(Util.smsId, 0);
        notifyChange();
    }

    private void initialComponent() {
        arrayList = new ArrayList<>();
        smsSentAdapter = new SmsSentAdapter(getActivity(), arrayList);
        databaseHelper = new DatabaseHelper(getActivity());
        progressBar = view.findViewById(R.id.progress_bar);
        selectAll = view.findViewById(R.id.select_all);
        alert = view.findViewById(R.id.alert_text);
        done = view.findViewById(R.id.done);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(smsSentAdapter);
    }

    private void notifyChange() {
        smsSentAdapter.notifyDataSetChanged();
        if (arrayList.size() == 0) {
            alert.setVisibility(View.VISIBLE);
        } else {
            alert.setVisibility(View.GONE);
        }
    }

}
