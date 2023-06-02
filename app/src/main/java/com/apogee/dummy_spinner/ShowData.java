package com.apogee.dummy_spinner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ShowData extends AppCompatActivity {

    private ExternalDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase database;
    List<String> data = new ArrayList<>();
    ListView listView;

    String mapping_id;
    ShowdataAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);


        mDatabaseHelper = new ExternalDatabaseHelper(this);

        database = mDatabaseHelper.getReadableDatabase();

        // Retrieve table names and their column count
        List<String> tableList = getTableList();
        List<String> tableids = getTableIds();


        // Display the table names in a ListView
        listView = findViewById(R.id.list);


        customAdapter = new ShowdataAdapter(ShowData.this, R.layout.dynamic_showdata, tableList, tableids);
        listView.setAdapter(customAdapter);

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tableList);
//        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tableName = tableList.get(position);
                int tableId = getTableId(tableName);
                Toast.makeText(ShowData.this, "Table ID: " + tableId, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private List<String> getTableList() {
        List<String> tableList = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM Form", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {


                int formId = cursor.getInt(cursor.getColumnIndex("form_id"));
                String formName = cursor.getString(cursor.getColumnIndex("form_name"));

                int ColumnCount = getTableColumnCount(formId);

                tableList.add("Form Name : " + formName + "\n" + "Total Columns Created : " + ColumnCount);

            }
            cursor.close();
        }
        return tableList;
    }

    private List<String> getTableIds() {

        List<String> tableids = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Form", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {


                int formId = cursor.getInt(cursor.getColumnIndex("form_id"));

                tableids.add(String.valueOf(formId));
//                tableList.add(formId + " (Form Name : " + formName + ")" + "\n" + "(Column Count : " + ColumnCount);
            }
            cursor.close();
        }
        return tableids;
    }

    private int getTableColumnCount(int formId) {
        int tableCount = 0;
        Cursor cursor = database.rawQuery("SELECT * FROM FormMapping Where form_id = " + formId + " ", null);
        if (cursor != null) {
            tableCount = cursor.getCount();
            cursor.close();
        }
        return tableCount;
    }

    private int getTableId(String tableName) {
        int tableId = 0;
        Cursor cursor = database.rawQuery("SELECT rowid FROM " + tableName + " LIMIT 1", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                tableId = cursor.getInt(0);
            }
            cursor.close();
        }
        return tableId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database

    }
}