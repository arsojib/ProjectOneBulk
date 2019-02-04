package com.example.arsojib.bulksms.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arsojib.bulksms.Activites.MainActivity;
import com.example.arsojib.bulksms.Adapter.ImportContactListAdapterTwo;
import com.example.arsojib.bulksms.Listener.ContactRemoveListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;

import java.util.ArrayList;


public class ImportContactFragment extends Fragment {

    View view;
    ImageView back;
    TextView done, alert;
    CheckBox selectAll;
    RecyclerView recyclerView;

    ContactRemoveListener contactRemoveListener;
    ImportContactListAdapterTwo importContactListAdapter;
    ArrayList<Contact> arrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.import_contact_fragment_layout, container, false);

        contactRemoveListener = new ContactRemoveListener() {
            @Override
            public void onContactRemove(String number, int position) {

            }

            @Override
            public void onContactUnCheck(Contact contact, int position) {
                arrayList.remove(position);
                notifyChange();;
            }
        };

        initialComponent();
        getContactList();

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
                        contacts.add(arrayList.get(i));
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
        importContactListAdapter = new ImportContactListAdapterTwo(getActivity(), arrayList, contactRemoveListener);
        back = view.findViewById(R.id.back);
        done = view.findViewById(R.id.done);
        alert = view.findViewById(R.id.alert_text);
        selectAll = view.findViewById(R.id.checkbox);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(importContactListAdapter);
    }

    private void getContactList() {
        Cursor phones = null;
        try {
            phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                String name, number;
                name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll(" ", "");
                arrayList.add(new Contact(name, number, false));
            }
            notifyChange();
        } catch (NullPointerException ignored) {

        } finally {
            if (phones != null) {
                phones.close();
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
