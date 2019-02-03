package com.example.arsojib.bulksms.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.arsojib.bulksms.Activites.MainActivity;
import com.example.arsojib.bulksms.Listener.ContactImportCompleteListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.Util;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements View.OnClickListener {

    View view;
    LinearLayout importExcelLayout, importTextLayout, importContactLayout, importContactGroupLayout, scheduleSmsLayout, historyLayout;

    int smsCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_frgament_layout, container, false);

        initialComponent();

        importExcelLayout.setOnClickListener(this);
        importTextLayout.setOnClickListener(this);
        importContactLayout.setOnClickListener(this);
        importContactGroupLayout.setOnClickListener(this);
        scheduleSmsLayout.setOnClickListener(this);
        historyLayout.setOnClickListener(this);

        ((MainActivity) getContext()).contactNotSentImportCompleteListener = new ContactImportCompleteListener() {
            @Override
            public void onImportComplete(ArrayList<Contact> arrayList) {

            }

            @Override
            public void onImportCompleteCount(int count) {
                showMessageSentProgress(count);
            }
        };

        return view;
    }

    private void initialComponent() {
        importExcelLayout = view.findViewById(R.id.import_excel_layout);
        importTextLayout = view.findViewById(R.id.import_text_layout);
        importContactLayout = view.findViewById(R.id.import_contact_layout);
        importContactGroupLayout = view.findViewById(R.id.import_contact_group_layout);
        scheduleSmsLayout = view.findViewById(R.id.schedule_sms_layout);
        historyLayout = view.findViewById(R.id.history_layout);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.import_excel_layout) {
            ImportFileFragment importFileFragment = new ImportFileFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", "xls");
            importFileFragment.setArguments(bundle);
            fragmentTransaction(importFileFragment);
        } else if (v.getId() == R.id.import_text_layout) {
            ImportFileFragment importFileFragment = new ImportFileFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", "txt");
            importFileFragment.setArguments(bundle);
            fragmentTransaction(importFileFragment);
        } else if (v.getId() == R.id.import_contact_layout) {
            if (new Util().checkPermissionReadContacts(getActivity())) {
                fragmentTransaction(new ImportContactFragment());
            }
        } else if (v.getId() == R.id.import_contact_group_layout) {
            if (new Util().checkPermissionReadContacts(getActivity())) {
                fragmentTransaction(new ImportContactGroupFragment());
            }
        } else if (v.getId() == R.id.schedule_sms_layout) {
            fragmentTransaction(new ScheduleListFragment());
        } else if (v.getId() == R.id.history_layout) {
            fragmentTransaction(new SmsListFragment());
        }
    }

    private void showMessageSentProgress(final int total) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.sms_sending_progress_layout);
        dialog.setCancelable(false);

        smsCount = 0;
        ImageView close = dialog.findViewById(R.id.close);
        final TextView sentCount = dialog.findViewById(R.id.sent_count);
        final ProgressBar progressBar = dialog.findViewById(R.id.progress_bar);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                smsCount++;
                if (smsCount >= total) {
                    sentCount.setText("Sent: " + total + " / " + "Total: " + total);
                    progressBar.setProgress(100);
                } else {
                    sentCount.setText("Sent: " + smsCount + " / " + "Total: " + total);
                    progressBar.setProgress((int) (smsCount / total) * 100);
                }
            }
        };

        sentCount.setText("Sent: " + smsCount + " / " + "Total: " + total);

        getContext().registerReceiver(broadcastReceiver, new IntentFilter("SMS_MANAGEMENT"));

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void fragmentTransaction(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contain_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
