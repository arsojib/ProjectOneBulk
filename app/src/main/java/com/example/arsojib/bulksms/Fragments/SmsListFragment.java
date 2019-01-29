package com.example.arsojib.bulksms.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.arsojib.bulksms.Adapter.SmsListAdapter;
import com.example.arsojib.bulksms.DataFetch.DatabaseHelper;
import com.example.arsojib.bulksms.Listener.ClickListener;
import com.example.arsojib.bulksms.Model.Message;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.Util;

import java.util.ArrayList;

public class SmsListFragment extends Fragment {

    View view;
    ImageView back;
    RecyclerView recyclerView;
    TextView alert;
    ProgressBar progressBar;

    ArrayList<Message> arrayList;
    SmsListAdapter smsListAdapter;
    DatabaseHelper databaseHelper;
    ClickListener clickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sms_list_layout, container, false);

        clickListener = new ClickListener() {
            @Override
            public void onItemClick(int position) {
                Util.smsId = arrayList.get(position).getId();
                fragmentTransaction(new SmsHistoryFragment());
            }
        };

        initialComponent();
        getData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void initialComponent() {
        arrayList = new ArrayList<>();
        smsListAdapter = new SmsListAdapter(getActivity(), arrayList, clickListener);
        databaseHelper = new DatabaseHelper(getActivity());
        back = view.findViewById(R.id.back);
        alert = view.findViewById(R.id.alert_text);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(smsListAdapter);
    }

    private void getData() {
        arrayList = databaseHelper.getAllMessage();
        notifyChange();
    }

    private void fragmentTransaction(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contain_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void notifyChange() {
        smsListAdapter.notifyDataSetChanged();
        if (arrayList.size() == 0) {
            alert.setVisibility(View.VISIBLE);
        } else {
            alert.setVisibility(View.GONE);
        }
    }

}
