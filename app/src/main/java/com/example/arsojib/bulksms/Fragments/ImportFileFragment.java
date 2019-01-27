package com.example.arsojib.bulksms.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arsojib.bulksms.Activites.MainActivity;
import com.example.arsojib.bulksms.Adapter.ImportContactListAdapter;
import com.example.arsojib.bulksms.Listener.ContactRemoveListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.FileUtils;
import com.example.arsojib.bulksms.Utils.Util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static android.content.ContentValues.TAG;

public class ImportFileFragment extends Fragment {

    View view;
    ImageView back;
    TextView done, alert;
    RecyclerView recyclerView;
    FloatingActionButton btnImportFile;

    ContactRemoveListener contactRemoveListener;
    ImportContactListAdapter importContactListAdapter;

    String type;
    ArrayList<Contact> arrayList;
    int requestCodeForExcel = 112;
    int requestCodeForText = 113;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.import_file_fragment_layout, container, false);

        contactRemoveListener = new ContactRemoveListener() {
            @Override
            public void onContactRemove(String number, int position) {
                try {
                    arrayList.remove(position);
                    notifyChange();
                } catch (IndexOutOfBoundsException ignored) {}
            }

            @Override
            public void onContactUnCheck(Contact contact, int position) {

            }
        };

        initialComponent();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        btnImportFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("xls")) {
                    if (new Util().checkPermissionREAD_EXTERNAL_STORAGE(getActivity())) {
                        browseExcel();
                    }
                } else if (type.equals("txt")) {
                    if (new Util().checkPermissionREAD_EXTERNAL_STORAGE(getActivity())) {
                        browseTxt();
                    }
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getContext()).contactImportCompleteListener.onImportComplete(arrayList);
            }
        });

        return view;
    }

    private void initialComponent() {
        type = getArguments().getString("type");
        arrayList = new ArrayList<>();
        importContactListAdapter = new ImportContactListAdapter(getActivity(), arrayList, contactRemoveListener);
        done = view.findViewById(R.id.done);
        alert = view.findViewById(R.id.alert_text);
        back = view.findViewById(R.id.back);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(importContactListAdapter);
        btnImportFile = view.findViewById(R.id.floating_button);
    }

    private void browseExcel() {

//        String[] mimeTypes = {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
//                "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
//                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
//                "application/pdf"};

        String[] mimeTypes = {"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), requestCodeForExcel);

    }

    private void browseTxt() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), requestCodeForText);

    }

    private void onSelectResultExcel(Intent data) {
        Uri uri;
        uri = data.getData();
        String path;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            path = FileUtils.getPath(getActivity(), uri);
        } else {
            path = null;
            if (uri != null) {
                path = uri.getPath();
            }
        }
        File file = new File(path);
        try {
            FileInputStream myInput = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(myInput);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator rows = sheet.rowIterator();
            int rowno =0;
            while (rows.hasNext()) {
                XSSFRow myRow = (XSSFRow) rows.next();
                if(rowno !=0) {
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno =0;
                    String number = "";
                    while (cellIter.hasNext()) {
                        XSSFCell myCell = (XSSFCell) cellIter.next();
                        if (colno==0){
                            number = myCell.toString();
                        }
                        colno++;
                        Log.e(TAG, " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }
                    arrayList.add(new Contact("", number, true));
                }

                rowno++;
            }
            notifyChange();
        } catch (Exception e) {
            Log.e(TAG, "error " + e.toString());
        }
    }

    private void onSelectResultText(Intent data) {
        Uri uri;
        uri = data.getData();
        String path;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            path = FileUtils.getPath(getActivity(), uri);
        } else {
            path = null;
            if (uri != null) {
                path = uri.getPath();
            }
        }
        File file = new File(path);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                arrayList.add(new Contact("", line, true));
            }
            notifyChange();
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestCodeForExcel) {
                onSelectResultExcel(data);
            } else if (requestCode == requestCodeForText) {
                onSelectResultText(data);
            }
        }
    }

}
