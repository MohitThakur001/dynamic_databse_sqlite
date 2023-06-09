package com.apogee.dummy_spinner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.apogee.dummy_spinner.Database.MyDatabase;
import com.apogee.dummy_spinner.Entities.ColumnSubtypeEntity;
import com.apogee.dummy_spinner.Entities.ColumnTypeEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private List<View> dynamicViews;
    private EditText tableNameEditText;
    private List<ArrayAdapter<String>> spinnerAdapters;
    private List<String> selectedValues;
    private ExternalDatabaseHelper mDatabaseHelper;
    List<Integer> dataListId = new ArrayList<>();
    ImageView showtable;
    List<String> dataListType = new ArrayList<>();
    List<Integer> subDataListId = new ArrayList<>();
    List<String> subDataListType = new ArrayList<>();
    String isSelected = "";
    int count = 0;
    private List<View> columnRows;
    public MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         mDatabaseHelper = new ExternalDatabaseHelper(MainActivity.this);



        myDatabase = MyDatabase.getInstance(this);

        tableNameEditText = findViewById(R.id.table_name_edit_text);
        showtable = findViewById(R.id.showtable);
        showtable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShowData.class);
                startActivity(intent);
            }
        });

        dynamicViews = new ArrayList<>();
        spinnerAdapters = new ArrayList<>();
        selectedValues = new ArrayList<>();

        LinearLayout dynamicLayout = findViewById(R.id.dynamic_layout);

        // Add initial dynamic view
        addDynamicView();

        // Add button click listener to add more dynamic views
        findViewById(R.id.btnAddView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDynamicView();
            }
        });

        // Save button click listener to retrieve selected values
        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveSelectedValues();
            }
        });

    }

    private class DatabaseTaskType extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {

            List<ColumnTypeEntity> columnTypes = myDatabase.columnTypeDao().getAllColumnTypes();

            for (ColumnTypeEntity columnType : columnTypes) {
                int id = columnType.getId();;
                String columnName =columnType.getTypeName();;


                dataListType.add(columnName);
                dataListId.add(id);
            }

            return dataListType;
        }
    }

    private class DatabaseTask extends AsyncTask<Integer, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Integer... integers) {
            int selectedOption = integers[0];

            List<ColumnSubtypeEntity> columnSubtypes = myDatabase.columnSubtypeDao().getColumnSubtypes(selectedOption);

            for (ColumnSubtypeEntity columnSubtype : columnSubtypes) {
                String columnName = columnSubtype.getSubtypeName();
                subDataListType.add(columnName);
            }

            return subDataListType;
        }
    }


    private void addDynamicView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dynamicView = inflater.inflate(R.layout.dynamic_spinner_view, null);
        dynamicViews.add(dynamicView);

        LinearLayout.LayoutParams layoutParams0 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                150);

        layoutParams0.setMargins(20, 20, 20, 10);

        EditText columnNameEditText = dynamicView.findViewById(R.id.column_name_edit_text);


        columnNameEditText.setBackground(getResources().getDrawable(R.drawable.border));
        columnNameEditText.setPadding(10, 0, 0, 0);
        columnNameEditText.setLayoutParams(layoutParams0);


        LinearLayout.LayoutParams layoutParamsDataType = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                100);
        layoutParamsDataType.setMargins(20, 10, 20, 10);


        LinearLayout.LayoutParams layoutParamsSubDataType = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                100);
        layoutParamsSubDataType.setMargins(20, 10, 20, 40);

        Spinner dataTypeSpinner = dynamicView.findViewById(R.id.spinner1);

        dataTypeSpinner.setLayoutParams(layoutParamsDataType);

        dataTypeSpinner.setBackground(getResources().getDrawable(R.drawable.spinner_back));

        Spinner subDataTypeSpinner = dynamicView.findViewById(R.id.spinner2);

        subDataTypeSpinner.setBackground(getResources().getDrawable(R.drawable.spinner_back));
        subDataTypeSpinner.setLayoutParams(layoutParamsSubDataType);

        // Create an adapter for dataTypeSpinner and set its options
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getSpinner1Options());
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataTypeSpinner.setAdapter(typeAdapter);

        // Create an adapter for subDataTypeSpinner
        ArrayAdapter<String> subTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<String>());
        subTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subDataTypeSpinner.setAdapter(subTypeAdapter);

        TextView txtIsSelection = dynamicView.findViewById(R.id.isSelectionTxt);

        RadioGroup radiogroup = dynamicView.findViewById(R.id.groupradio);

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override

            // The flow will come here when
            // any of the radio buttons in the radioGroup
            // has been clicked

            // Check which radio button has been clicked
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                // Get the selected Radio Button


            }
        });


        // Add dataTypeSpinner item selection listener to update subDataTypeSpinner options
        dataTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int selectedOption = dataListId.get(position);

                if (selectedOption != 1) {
                    txtIsSelection.setVisibility(View.GONE);
                    radiogroup.clearCheck();
                    radiogroup.setVisibility(View.GONE);

                } else {
                    txtIsSelection.setVisibility(View.VISIBLE);
                    radiogroup.setVisibility(View.VISIBLE);
                }


                List<String> subTypeOption = null;
                try {
                    subTypeOption = getSubTypeOptions(selectedOption);

                    subTypeAdapter.clear();
                    subTypeAdapter.addAll(subTypeOption);
                    subTypeAdapter.notifyDataSetChanged();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerAdapters.add(subTypeAdapter);

        LinearLayout dynamicLayout = findViewById(R.id.dynamic_layout);
        dynamicLayout.addView(dynamicView);

//        columnRows.add(dynamicView);

    }

    private List<String> getSpinner1Options() {


        dataListType.clear();
        dataListId.clear();
        

        try {
            dataListType = new DatabaseTaskType().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return dataListType;
    }

    private List<String> getSubTypeOptions(int selectedOption) throws ExecutionException, InterruptedException {
        // Dummy data for spinner2 options based on the selectedOption in spinner1
        subDataListType.clear();
        subDataListId.clear();


        subDataListType =  new DatabaseTask().execute(selectedOption).get();


        return subDataListType;
    }

    private void retrieveSelectedValues() {

        selectedValues.clear();


        String tableName = "Form";
        String columnName = "form_name";

        String columnValue = tableNameEditText.getText().toString();

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(columnName, columnValue);

        String rowId = String.valueOf(db.insert(tableName, null, values));


        for (int i = 0; i < dynamicViews.size(); i++) {
            View dynamicView = dynamicViews.get(i);
            Spinner spinner1 = dynamicView.findViewById(R.id.spinner1);
            Spinner spinner2 = dynamicView.findViewById(R.id.spinner2);
            EditText editText = dynamicView.findViewById(R.id.column_name_edit_text);
            RadioGroup radioGroup = dynamicView.findViewById(R.id.groupradio);


            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

            if (selectedRadioButtonId == (-1)) {
                isSelected = "No";

            } else {

                RadioButton radioButton = findViewById(selectedRadioButtonId);


                isSelected = radioButton.getText().toString();

            }


            String selectedValue1 = (String) spinner1.getSelectedItem();

            String selectedValue2 = (String) spinner2.getSelectedItem();


            count++;

            SQLiteDatabase dbg = mDatabaseHelper.getReadableDatabase();
            String[] projection = {"subtype_id"};
            String selection = "subtype_name" + "=?";
            String[] selectionArgs = {selectedValue2};

            Cursor cursor = dbg.query("ColumnSubtype", projection, selection, selectionArgs, null, null, null);

            int columnIndex = -1;

            if (cursor.moveToFirst()) {
                columnIndex = cursor.getInt(cursor.getColumnIndex("subtype_id"));
                Log.d(TAG, "retrieveSelectedValues Sub: " + columnIndex);
            }
            

            String strColumnName = editText.getText().toString();

            String selectedValuesText = "View " + (i + 1) + ": " + strColumnName + ", " + selectedValue1 + ", " + selectedValue2;

            selectedValues.add(selectedValuesText);
            Log.d(TAG, "retrieveSelectedValues: " + selectedValuesText);


            saveDataintoMapping(columnIndex, rowId, strColumnName, strColumnName, count, isSelected);


        }

        // Perform any further processing with the selected values

    }

    private void saveDataintoMapping(int columnIndex, String rowId, String strColumnName, String strColumnName1, int count, String isSelected) {


        SQLiteDatabase dbm = mDatabaseHelper.getWritableDatabase();


        String tableName = "FormMapping";
        String column1Name = "subtype_id";
        String column2Name = "form_id";
        String column3Name = "column_name";
        String column4Name = "display_name";
        String column5Name = "order_no";
        String column6Name = "is_selected";

        ContentValues valuesMap = new ContentValues();
        valuesMap.put(column1Name, columnIndex);
        valuesMap.put(column2Name, rowId);
        valuesMap.put(column3Name, strColumnName);
        valuesMap.put(column4Name, strColumnName1);
        valuesMap.put(column5Name, count);
        valuesMap.put(column6Name, isSelected);

        dbm.insert(tableName, null, valuesMap);

        Toast.makeText(this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(MainActivity.this, DynamicUI.class);
        intent.putExtra("table_name", tableName);
        intent.putExtra("form_id", rowId);
        startActivity(intent);


    }
}
