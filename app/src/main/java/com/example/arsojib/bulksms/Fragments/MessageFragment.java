package com.example.arsojib.bulksms.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arsojib.bulksms.Activites.MainActivity;
import com.example.arsojib.bulksms.Listener.ContactImportCompleteListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Service.SmsManagementService;

import java.util.ArrayList;

import static android.Manifest.permission.SEND_SMS;

public class MessageFragment extends Fragment {

    View view;
    TextView count, messageLength;
    EditText messageText;
    FrameLayout sendMessage;

    String message = "";
    final int REQUEST_SMS = 111;


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
                message = messageText.getText().toString().trim();
                if (((MainActivity) getContext()).arrayList.size() != 0 && !message.equals("")) {
                    checkPermission();
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

    private void checkPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasSMSPermission = ContextCompat.checkSelfPermission(getActivity(), SEND_SMS);
            if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(SEND_SMS)) {
                    showMessageOKCancel("You need to allow access to Send SMS",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{SEND_SMS},
                                                REQUEST_SMS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{SEND_SMS},
                        REQUEST_SMS);
                return;
            }
            sendMySMS();
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void sendMySMS() {
        Intent intent = new Intent(getActivity(), SmsManagementService.class);
        intent.putExtra("contact_list", ((MainActivity) getContext()).arrayList);
        intent.putExtra("message", message);
        getActivity().startService(intent);
    }

    private void showMessageSentProgress() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.sms_sending_progress_layout);
        dialog.setCancelable(false);

        ImageView close = dialog.findViewById(R.id.close);
        TextView sentCount = dialog.findViewById(R.id.sent_count);
        ProgressBar progressBar = dialog.findViewById(R.id.progress_bar);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status;
                String number;

                status = intent.getIntExtra("status", 0);
                number = intent.getStringExtra("number");

            }
        };

        getContext().registerReceiver(broadcastReceiver, new IntentFilter("SMS_MANAGEMENT"));

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SMS:
                if (grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getContext(), "Permission Granted, Now you can access sms", Toast.LENGTH_SHORT).show();
                    sendMySMS();

                }else {
                    Toast.makeText(getContext(), "Permission Denied, You cannot access and sms", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(SEND_SMS)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{SEND_SMS},
                                                        REQUEST_SMS);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
