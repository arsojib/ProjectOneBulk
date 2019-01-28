package com.example.arsojib.bulksms.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.TextView;

import com.example.arsojib.bulksms.Adapter.ImportContactListAdapterFour;
import com.example.arsojib.bulksms.Adapter.ImportContactListAdapterTwo;
import com.example.arsojib.bulksms.Listener.ContactRemoveListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;

import java.util.ArrayList;

/**
 * Created by AR Sajib on 1/28/2019.
 */

public class ImportContactFromGroupFragment extends Fragment {

    View view;
    ImageView back;
    TextView alert;
    RecyclerView recyclerView;

    String groupId;
    ImportContactListAdapterFour importContactListAdapter;
    ArrayList<Contact> arrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.import_contact_from_group_fragment_layout, container, false);

        initialComponent();
        getData();
        getContactList();

        return view;
    }

    private void initialComponent() {
        arrayList = new ArrayList<>();
        importContactListAdapter = new ImportContactListAdapterFour(getActivity(), arrayList, null);
        back = view.findViewById(R.id.back);
        alert = view.findViewById(R.id.alert_text);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(importContactListAdapter);
    }

    private void getData() {
        groupId = getArguments().getString("group_id");
    }

    private void getContactList() {
        Cursor phones = null;
        Cursor query = null;
        try {
            String where = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "="
                    + groupId + " AND "
                    + ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE + "='"
                    + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'";

            query = getActivity().getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID
                    }, where, null, ContactsContract.Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

            String ids = "";
            for (query.moveToFirst(); !query.isAfterLast(); query.moveToNext()) {
                ids += "," + query.getString(0);
            }
            if (ids.length() > 0) {
                ids = ids.substring(1);
            }
            String[] projection = new String[]{
                    "_id",
                    "contact_id",
                    "lookup",
                    "display_name",
                    "data1",
                    "photo_id",
                    "data2", // number type: 1:home, 2:mobile, 3: work, else : other
            };
            String selection = "mimetype ='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'"
                    + " AND contact_id in (" + ids + ")";
            phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, null, null);
            while (phones.moveToNext()) {
                String name, number;
                name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                arrayList.add(new Contact(name, number, false));
            }
            notifyChange();
        } catch (NullPointerException ignored) {

        } finally {
            if (phones != null) {
                phones.close();
            }
            if (query != null) {
                query.close();
            }
        }
    }

    private void notifyChange() {
        importContactListAdapter.notifyDataSetChanged();
        if (arrayList.size() == 0) {
            alert.setVisibility(View.VISIBLE);
        } else {
            alert.setVisibility(View.GONE);
        }
    }

}
