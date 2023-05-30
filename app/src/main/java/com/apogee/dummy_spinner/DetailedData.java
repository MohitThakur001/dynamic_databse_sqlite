package com.apogee.dummy_spinner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DetailedData extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<ColumnData> columnDataList;
    private ColumnDataAdapter columnDataAdapter;
    private ExternalDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase database;
    List<String> subTypeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_data);

        String formId = getIntent().getStringExtra("form_id");

        subTypeList = getIntent().getStringArrayListExtra("column_type");

        Log.d(TAG, "onCreate: "+subTypeList);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        columnDataList = new ArrayList<>();
        columnDataAdapter = new ColumnDataAdapter(columnDataList,this,subTypeList);
        recyclerView.setAdapter(columnDataAdapter);

        mDatabaseHelper = new ExternalDatabaseHelper(this);

        database = mDatabaseHelper.getReadableDatabase();

        // Replace "your_table_name" with your actual table name
        String query = "SELECT column_name,column_value FROM ShowData Where form_id = "+ formId+" ";

        Cursor cursor = database.rawQuery(query, null);



        while (cursor.moveToNext()) {


            String column_name = cursor.getString(cursor.getColumnIndex("column_name"));


            String column_value = cursor.getString(cursor.getColumnIndex("column_value"));

            ColumnData columnData = new ColumnData(column_name, column_value);
            columnDataList.add(columnData);


        }


        cursor.close();
//        database.close();
    }

}