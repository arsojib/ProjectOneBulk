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
import android.widget.TextView;

import com.example.arsojib.bulksms.Activites.MainActivity;
import com.example.arsojib.bulksms.Adapter.ImportContactListAdapterThree;
import com.example.arsojib.bulksms.Listener.ContactImportCompleteListener;
import com.example.arsojib.bulksms.Listener.ContactRemoveListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;

import java.util.ArrayList;

public class ContactListFragment extends Fragment {

    View view;
    TextView count, alert;
    RecyclerView recyclerView;

    ContactRemoveListener contactRemoveListener;
    ImportContactListAdapterThree importContactListAdapterThree;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contact_list_fragment_layout, container, false);

        contactRemoveListener = new ContactRemoveListener() {
            @Override
            public void onContactRemove(String number, int position) {

            }

            @Override
            public void onContactUnCheck(Contact contact, int position) {
                try {
                    ((MainActivity) getContext()).arrayList.remove(position);
                    notifyChange();
                    ((MainActivity) getContext()).contactImportCompleteCountListener.onImportCompleteCount(((MainActivity) getContext()).arrayList.size());
                } catch (IndexOutOfBoundsException ignored) {}
            }
        };

        initialComponent();

        ((MainActivity) getContext()).contactImportCompleteListener = new ContactImportCompleteListener() {
            @Override
            public void onImportComplete(ArrayList<Contact> contacts) {
                ((MainActivity) getContext()).arrayList.addAll(contacts);
                notifyChange();
                ((MainActivity) getContext()).viewPager.setCurrentItem(1);
                ((MainActivity) getContext()).contactImportCompleteCountListener.onImportCompleteCount(((MainActivity) getContext()).arrayList.size());
            }

            @Override
            public void onImportCompleteCount(int count) {

            }
        };

        return view;
    }

    private void initialComponent() {
        ((MainActivity) getContext()).arrayList = new ArrayList<>();
        importContactListAdapterThree = new ImportContactListAdapterThree(getActivity(), ((MainActivity) getContext()).arrayList, contactRemoveListener);
        count = view.findViewById(R.id.count);
        alert = view.findViewById(R.id.alert_text);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(importContactListAdapterThree);
    }

    private void notifyChange() {
        importContactListAdapterThree.notifyDataSetChanged();
        if (((MainActivity) getContext()).arrayList.size() == 0) {
            alert.setVisibility(View.VISIBLE);
        } else {
            alert.setVisibility(View.GONE);
        }
        count.setText(((MainActivity) getContext()).arrayList.size() + "");
    }

}
