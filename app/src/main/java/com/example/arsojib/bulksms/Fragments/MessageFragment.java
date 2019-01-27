package com.example.arsojib.bulksms.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arsojib.bulksms.Activites.MainActivity;
import com.example.arsojib.bulksms.Listener.ContactImportCompleteListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;

import java.util.ArrayList;

public class MessageFragment extends Fragment {

    View view;
    TextView count, messageLength;
    EditText messageText;
    FrameLayout sendMessage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_fragment_layout, container, false);

        initialComponent();

        ((MainActivity) getContext()).contactImportCompleteCountListener = new ContactImportCompleteListener() {
            @Override
            public void onImportComplete(ArrayList<Contact> arrayList) {

            }

            @Override
            public void onImportCompleteCount(int contactCount) {
                count.setText(contactCount);
            }
        };

        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                messageLength.setText("160/" + messageText.length());
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getContext()).arrayList.size() != 0) {

                } else {
                    Toast.makeText(getContext(), "Please add contact first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void initialComponent() {
        count = view.findViewById(R.id.count);
        messageText = view.findViewById(R.id.message_text);
        messageLength = view.findViewById(R.id.message_length);
        sendMessage = view.findViewById(R.id.send_message);
    }

}
