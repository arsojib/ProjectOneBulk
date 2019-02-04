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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    FrameLayout layout;
    ImageView back;
    RecyclerView recyclerView;
    TextView alert;
    ProgressBar progressBar;

    ArrayList<Message> arrayList;
    SmsListAdapter smsListAdapter;
    DatabaseHelper databaseHelper;
    ClickListener clickListener, deleteListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sms_list_layout, container, false);

        clickListener = new ClickListener() {
            @Override
            public void onItemClick(int position) {
                Util.smsId = arrayList.get(position).getId();
                Util.smsMessage = arrayList.get(position).getMessage();
                fragmentTransaction(new SmsHistoryFragment());
            }
        };

        deleteListener = new ClickListener() {
            @Override
            public void onItemClick(int position) {
                databaseHelper.deleteSingleSms(arrayList.get(position).getId());
                arrayList.remove(position);
                notifyChange();
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
        smsListAdapter = new SmsListAdapter(getActivity(), arrayList, clickListener, deleteListener);
        databaseHelper = new DatabaseHelper(getActivity());
        layout = view.findViewById(R.id.layout);
        back = view.findViewById(R.id.back);
        alert = view.findViewById(R.id.alert_text);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(smsListAdapter);
    }

    private void getData() {
        arrayList.addAll(databaseHelper.getAllMessage());
        notifyChange();
    }

    private void fragmentTransaction(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.layout, fragment);
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
