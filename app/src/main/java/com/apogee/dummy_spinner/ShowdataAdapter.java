package com.apogee.dummy_spinner;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShowdataAdapter extends ArrayAdapter<String> {
    SQLiteDatabase db;
    ExternalDatabaseHelper externalDatabaseHelper;
    private Context context;
    private TextView itemListText;
    private Button itemButton;
    private List<String> listValues;
    private List<String> tableids;


    public ShowdataAdapter(Context context, int resource, List<String> listValues, List<String> tableids) {
        super(context, resource, listValues);
        this.context = context;
        this.listValues = listValues;
        this.tableids = tableids;
    }

    /**
     * getView method is called for each item of ListView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String currentValue = listValues.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.dynamic_showdata, null);

        itemListText = (TextView) convertView.findViewById(R.id.itemListText);
        itemListText.setText(currentValue);

        itemListText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        itemButton = (Button) convertView.findViewById(R.id.itemButton);
        //To lazy to implement interface
        itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableid = tableids.get(position);

                List<String> ColumnSubtype = getColumns(tableid);


                Toast.makeText(context, "Form id is : " + tableid, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DetailedData.class);
                intent.putExtra("form_id", tableid);
                intent.putExtra("column_type", (ArrayList) ColumnSubtype);
                context.startActivity(intent);
            }
        });

        return convertView;
    }


    private List<String> getColumns(String tableid) {

        List<String> ColumnSubtype = new ArrayList<>();

        ExternalDatabaseHelper mDatabaseHelper = new ExternalDatabaseHelper(context);
        db = mDatabaseHelper.getReadableDatabase();


        Cursor cursor = db.rawQuery("SELECT subtype_id FROM FormMapping Where form_id = " + tableid + " ", null);

        while (cursor.moveToNext()) {


            int subtype_id = cursor.getInt(cursor.getColumnIndex("subtype_id"));

            String ColumnType = getColumnSubtypeName(db, "ColumnSubtype", subtype_id);

            ColumnSubtype.add(ColumnType);


        }


        cursor.close();

        return ColumnSubtype;
    }


    public String getColumnSubtypeName(SQLiteDatabase database, String tableName, int columnId) {

        String[] columns = {"subtype_name"};
        String selection = "subtype_id" + " = ?";
        String[] selectionArgs = {String.valueOf(columnId)};

        Cursor cursor = database.query(tableName, columns, selection, selectionArgs, null, null, null);
        String columnValue = null;

        if (cursor.moveToFirst()) {
            columnValue = cursor.getString(cursor.getColumnIndex("subtype_name"));
        }

        cursor.close();


        return columnValue;
    }
}