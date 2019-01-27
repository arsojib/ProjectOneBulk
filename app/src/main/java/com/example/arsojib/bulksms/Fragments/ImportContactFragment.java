package com.example.arsojib.bulksms.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arsojib.bulksms.Adapter.ImportContactListAdapter;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.Util;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ImportContactFragment extends Fragment {

    View view;
    RecyclerView recyclerView;

    ImportContactListAdapter importContactListAdapter;
    ArrayList<String> arrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.import_contact_fragment_layout, container, false);

        initialComponent();
//        getContactList();
        getContactGroup();
        return view;
    }

    private void initialComponent() {
        arrayList = new ArrayList<>();
        importContactListAdapter = new ImportContactListAdapter(getActivity(), arrayList);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(importContactListAdapter);
    }

    private void getContactList() {
        Cursor phones = null;
        try {
            phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                arrayList.add(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }
            importContactListAdapter.notifyDataSetChanged();
        } catch (NullPointerException ignored) {

        } finally {
            if (phones != null) {
                phones.close();
            }
        }
    }

    private void getContactGroup() {
        final String[] GROUP_PROJECTION = new String[] {
                ContactsContract.Groups._ID, ContactsContract.Groups.TITLE };
        Cursor gC = null;
        try {
            gC = getActivity().getContentResolver().query(
                    ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION, null, null, null);
            gC.moveToFirst();
            while (!gC.isAfterLast()) {
                int idcolumn = gC.getColumnIndex(ContactsContract.Groups.TITLE);
                String Id = gC.getString(idcolumn);
                arrayList.add(Id);
                gC.moveToNext();
            }
            importContactListAdapter.notifyDataSetChanged();
        } catch (NullPointerException ignored) {

        } finally {
            if (gC != null) {
                gC.close();
            }
        }
    }

}
