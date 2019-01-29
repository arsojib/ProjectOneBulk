package com.example.arsojib.bulksms.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.Util;

public class HomeFragment extends Fragment implements View.OnClickListener {

    View view;
    LinearLayout importExcelLayout, importTextLayout, importContactLayout, importContactGroupLayout, scheduleSmsLayout, historyLayout;

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
            fragmentTransaction(new ImportContactGroupFragment());
        } else if (v.getId() == R.id.schedule_sms_layout) {

        } else if (v.getId() == R.id.history_layout) {
            fragmentTransaction(new SmsListFragment());
        }
    }

    private void fragmentTransaction(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contain_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
