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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arsojib.bulksms.Activites.MainActivity;
import com.example.arsojib.bulksms.Adapter.ImportContactListAdapterFive;
import com.example.arsojib.bulksms.Adapter.ImportContactListAdapterTwo;
import com.example.arsojib.bulksms.Listener.ClickListener;
import com.example.arsojib.bulksms.Listener.ContactRemoveListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.Model.Group;
import com.example.arsojib.bulksms.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ImportContactGroupFragment extends Fragment {

    View view;
    ImageView back;
    TextView done, alert;
    CheckBox selectAll;
    RecyclerView recyclerView;

    ClickListener clickListener;
    ImportContactListAdapterFive importContactListAdapter;
    ArrayList<Group> arrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.import_contact_group_fragment_layout, container, false);

        clickListener = new ClickListener() {
            @Override
            public void onItemClick(int position) {
                ImportContactFromGroupFragment importContactFromGroupFragment = new ImportContactFromGroupFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("contacts", arrayList.get(position).getArrayList());
                importContactFromGroupFragment.setArguments(bundle);
                fragmentTransaction(importContactFromGroupFragment);
            }
        };

        initialComponent();
        getContactGroup();

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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Contact> contacts = new ArrayList<>();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).isCheck()) {
                        contacts.addAll(arrayList.get(i).getArrayList());
                    }
                }
                ((MainActivity) getContext()).contactImportCompleteListener.onImportComplete(contacts);
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void initialComponent() {
        arrayList = new ArrayList<>();
        importContactListAdapter = new ImportContactListAdapterFive(getActivity(), arrayList, clickListener);
        back = view.findViewById(R.id.back);
        done = view.findViewById(R.id.done);
        alert = view.findViewById(R.id.alert_text);
        selectAll = view.findViewById(R.id.checkbox);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(importContactListAdapter);
    }

    private void getContactGroup() {
        final String[] GROUP_PROJECTION = new String[] {
                ContactsContract.Groups._ID, ContactsContract.Groups.TITLE};
        Cursor gC = null;
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            gC = getActivity().getContentResolver().query(
                    ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION, null, null, null);
            gC.moveToFirst();
            while (!gC.isAfterLast()) {
                int id, title;
                id = gC.getColumnIndex(ContactsContract.Groups._ID);
                title = gC.getColumnIndex(ContactsContract.Groups.TITLE);
                arrayList.add(new Group(gC.getString(id), gC.getString(title), "", false, contacts));
                getContactList(arrayList.size() - 1, gC.getString(id));
                gC.moveToNext();
            }
            importContactListAdapter.notifyDataSetChanged();
            notifyChange();
        } catch (NullPointerException ignored) {

        } finally {
            if (gC != null) {
                gC.close();
            }
        }
    }

    private void getContactList(int position, String groupId) {
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
            ArrayList<Contact> contacts = new ArrayList<>();
            while (phones.moveToNext()) {
                String name, number;
                name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(new Contact(name, number, false));
            }
            arrayList.get(position).setArrayList(contacts);
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

    private void fragmentTransaction(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contain_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
