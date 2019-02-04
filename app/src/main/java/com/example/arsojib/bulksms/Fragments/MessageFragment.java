package com.example.arsojib.bulksms.Fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.arsojib.bulksms.Activites.MainActivity;
import com.example.arsojib.bulksms.DataFetch.DatabaseHelper;
import com.example.arsojib.bulksms.Listener.ContactImportCompleteListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.Model.Message;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Service.ScheduleExactJobService;
import com.example.arsojib.bulksms.Service.SmsManagementService;
import com.example.arsojib.bulksms.Utils.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;

public class MessageFragment extends Fragment {

    View view;
    LinearLayout simSelectionLayout, simOneLayout, simTwoLayout;
    RadioButton simOneButton, simTwoButton;
    TextView count, messageLength;
    EditText messageText;
    FrameLayout sendMessage, addToSchedule;

    String message = "";
    final int REQUEST_SMS = 111, REQUEST_PHONE_STATE = 112;
    int smsCount = 0, sim = 5;
    boolean hasSim = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_fragment_layout, container, false);

        initialComponent();
        checkPermissionPhoneState();

        ((MainActivity) getContext()).contactImportCompleteCountListener = new ContactImportCompleteListener() {
            @Override
            public void onImportComplete(ArrayList<Contact> arrayList) {

            }

            @Override
            public void onImportCompleteCount(int contactCount) {
                count.setText(contactCount + "");
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
                messageLength.setText("∞ / " + messageText.length());
            }
        });

        simOneButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeSim(1);
                }
            }
        });

        simTwoButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeSim(2);
                }
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasSim) {
                    if (simOneButton.isChecked() || simTwoButton.isChecked()) {
                        if (simOneButton.isChecked()) {
                            sim = 0;
                        } else if (simTwoButton.isChecked()) {
                            sim = 1;
                        }
                    }
                    if (sim == 5) {
                        Toast.makeText(getContext(), "Please Select a Sim.", Toast.LENGTH_SHORT).show();
                    } else {
                        send();
                    }
                } else {
                    sim = 5;
                    send();
                }
            }
        });

        addToSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSchedule();
            }
        });

        return view;
    }

    private void send() {
        message = messageText.getText().toString().trim();
        if (((MainActivity) getContext()).arrayList.size() != 0 && !message.equals("")) {
            checkPermission();
        } else {
            Toast.makeText(getContext(), "Please add contact & message first.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initialComponent() {
        simSelectionLayout = view.findViewById(R.id.sim_selection_layout);
        simOneLayout = view.findViewById(R.id.sim_one_layout);
        simTwoLayout = view.findViewById(R.id.sim_two_layout);
        simOneButton = view.findViewById(R.id.sim_one_button);
        simTwoButton = view.findViewById(R.id.sim_two_button);
        count = view.findViewById(R.id.count);
        messageText = view.findViewById(R.id.message_text);
        messageLength = view.findViewById(R.id.message_length);
        sendMessage = view.findViewById(R.id.send_message);
        addToSchedule = view.findViewById(R.id.add_to_schedule);
    }

    private void simCheck() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            simSelectionLayout.setVisibility(View.VISIBLE);
            try {
                SubscriptionManager subscriptionManager = (SubscriptionManager) getActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                if (subscriptionInfoList == null || subscriptionInfoList.size() == 0) {
                    hasSim = false;
                    simSelectionLayout.setVisibility(View.GONE);
                } else if (subscriptionInfoList.size() == 1) {
                    hasSim = true;
                    simSelectionLayout.setVisibility(View.VISIBLE);
                    if (subscriptionInfoList.get(0).getSubscriptionId() == 1) {
                        simOneLayout.setVisibility(View.VISIBLE);
                        simTwoLayout.setVisibility(View.GONE);
                    } else if (subscriptionInfoList.get(0).getSubscriptionId() == 2) {
                        simOneLayout.setVisibility(View.GONE);
                        simTwoLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    hasSim = true;
                    simSelectionLayout.setVisibility(View.VISIBLE);
                }
            } catch (NullPointerException ignored) {
            }

        } else {
            hasSim = false;
            simSelectionLayout.setVisibility(View.GONE);
        }
    }

    private void changeSim(int s) {
        if (s == 1) {
            simTwoButton.setChecked(false);
        } else {
            simOneButton.setChecked(false);
        }
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
        } else {
            sendMySMS();
        }

    }

    private void checkPermissionPhoneState() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasSMSPermission = ContextCompat.checkSelfPermission(getActivity(), READ_PHONE_STATE);
            if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
                    showMessageOKCancel("You need to allow access to Send SMS",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{READ_PHONE_STATE},
                                                REQUEST_PHONE_STATE);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{READ_PHONE_STATE},
                        REQUEST_PHONE_STATE);
                return;
            }
            simCheck();
        } else {
            simCheck();
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
        ArrayList<Contact> contacts = new ArrayList<>();
        contacts.addAll(((MainActivity) getContext()).arrayList);
        Intent intent = new Intent(getActivity(), SmsManagementService.class);
        intent.putExtra("contact_list", contacts);
        intent.putExtra("message", message);
        intent.putExtra("sim", sim);
        intent.putExtra("old", false);
        getActivity().startService(intent);
        showMessageSentProgress(((MainActivity) getContext()).arrayList.size());
        clearHistory();
    }

    private void clearHistory() {
        ((MainActivity) getContext()).arrayList.clear();
        messageText.setText("");
        ((MainActivity) getContext()).contactImportCompleteListener.onImportCompleteCount(0);
        count.setText(0 + "");
    }

    private void showMessageSentProgress(final int total) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.sms_sending_progress_layout);
        dialog.setCancelable(true);

        smsCount = 0;
        count.setText(0 + "");
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
                    progressBar.setProgress((int) ((smsCount / total) * 100));
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

    int mYear = 0, mMonth = 0, mDay = 0, mHour = 0, mMinute = 0;

    private void setSchedule() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.add_to_schedule_layout);
        dialog.setCancelable(true);

        final DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        FrameLayout addDate, addTime, addToSchedule;
        final TextView date, time;
        addDate = dialog.findViewById(R.id.add_date);
        addTime = dialog.findViewById(R.id.add_time);
        addToSchedule = dialog.findViewById(R.id.add_to_schedule);
        date = dialog.findViewById(R.id.date);
        time = dialog.findViewById(R.id.time);

        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mYear = year;
                                mMonth = monthOfYear + 1;
                                mDay = dayOfMonth;
                                date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                mHour = hourOfDay;
                                mMinute = minute;
                                time.setText(hourOfDay + ":" + minute);
                            }
                        }, c.get(Calendar.HOUR_OF_DAY) + 1, c.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        addToSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mYear != 0 && mMonth != 0 && mDay != 0) {
                    String dateTime = mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":" + mMinute + ":00";
                    long time = Util.getLongFromDate(dateTime);
                    message = messageText.getText().toString().trim();
                    if (time != 0) {
                        if ((System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)) < time) {
                            if (((MainActivity) getContext()).arrayList.size() != 0 && !message.equals("")) {
                                databaseHelper.addSchedule(time, messageText.getText().toString(), time, ((MainActivity) getContext()).arrayList);
                                ScheduleExactJobService.scheduleExactJob(new Message(time, messageText.getText().toString(), time));
                                Toast.makeText(getContext(), "Schedule added successfully.", Toast.LENGTH_SHORT).show();
                                clearHistory();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "Please add contact & message first.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Please set time at least 30 min from now", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Please select schedule date and time.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission Granted, Now you can access sms", Toast.LENGTH_SHORT).show();
                    sendMySMS();

                } else {
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
            case REQUEST_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    simCheck();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{READ_PHONE_STATE},
                                                        REQUEST_PHONE_STATE);
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
