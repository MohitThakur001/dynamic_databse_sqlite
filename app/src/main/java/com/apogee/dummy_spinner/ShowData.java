package com.apogee.dummy_spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShowData extends AppCompatActivity {

    private ExternalDatabaseHelper mDatabaseHelper;

    List<String> data = new ArrayList<>();
    ListView list;


    String mapping_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        list = findViewById(R.id.list);



        mDatabaseHelper = new ExternalDatabaseHelper(this);


        mapping_id = getIntent().getStringExtra("table_id");


        displayAllData();


        // Retrieve and display all data from the table

    }

    private void displayAllData() {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query("ShowData", null, "form_id=?", new String[]{String.valueOf(mapping_id)}, null, null, null);

        if (cursor.moveToFirst()) {

            do {

                String column_name = String.valueOf(cursor.getColumnIndex("column_name"));
                String column_value = String.valueOf(cursor.getColumnIndex("column_value"));
                String strcolumn_name = cursor.getString(Integer.parseInt(column_name));
                String strcolumn_value = cursor.getString(Integer.parseInt(column_value));
                data.add(strcolumn_name + ", "+strcolumn_value);

            } while (cursor.moveToNext());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);

        list.setAdapter(adapter);

        cursor.close();
    }

}