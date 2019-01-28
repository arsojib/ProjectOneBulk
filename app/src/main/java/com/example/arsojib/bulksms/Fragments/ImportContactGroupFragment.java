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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arsojib.bulksms.Activites.MainActivity;
import com.example.arsojib.bulksms.Adapter.ImportContactListAdapterFive;
import com.example.arsojib.bulksms.Adapter.ImportContactListAdapterTwo;
import com.example.arsojib.bulksms.Listener.ContactRemoveListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.Model.Group;
import com.example.arsojib.bulksms.R;

import java.util.ArrayList;

public class ImportContactGroupFragment extends Fragment {

    View view;
    ImageView back;
    TextView done, alert;
    EditText search;
    CheckBox selectAll;
    RecyclerView recyclerView;

    ContactRemoveListener contactRemoveListener;
    ImportContactListAdapterFive importContactListAdapter;
    ArrayList<Group> arrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.import_contact_group_fragment_layout, container, false);

        contactRemoveListener = new ContactRemoveListener() {
            @Override
            public void onContactRemove(String number, int position) {
                ImportContactFromGroupFragment importContactFromGroupFragment = new ImportContactFromGroupFragment();
                Bundle bundle = new Bundle();
                bundle.putString("group_id", number);
                importContactFromGroupFragment.setArguments(bundle);
                fragmentTransaction(importContactFromGroupFragment);
            }

            @Override
            public void onContactUnCheck(Contact contact, int position) {

            }
        };

        initialComponent();
        getContactGroup();

//        done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((MainActivity) getContext()).contactImportCompleteListener.onImportComplete(arrayList);
//            }
//        });

        return view;
    }

    private void initialComponent() {
        arrayList = new ArrayList<>();
        importContactListAdapter = new ImportContactListAdapterFive(getActivity(), arrayList, contactRemoveListener);
        back = view.findViewById(R.id.back);
        done = view.findViewById(R.id.done);
        alert = view.findViewById(R.id.alert_text);
        search = view.findViewById(R.id.search);
        selectAll = view.findViewById(R.id.checkbox);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(importContactListAdapter);
    }

    private void getContactGroup() {
        final String[] GROUP_PROJECTION = new String[] {
                ContactsContract.Groups._ID, ContactsContract.Groups.TITLE};
        Cursor gC = null;
        try {
            gC = getActivity().getContentResolver().query(
                    ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION, null, null, null);
            gC.moveToFirst();
            while (!gC.isAfterLast()) {
                int id, title, count;
                id = gC.getColumnIndex(ContactsContract.Groups._ID);
                title = gC.getColumnIndex(ContactsContract.Groups.TITLE);
                count = gC.getColumnIndex(ContactsContract.Groups._COUNT);
                arrayList.add(new Group(gC.getString(id), gC.getString(title), "", false));
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
