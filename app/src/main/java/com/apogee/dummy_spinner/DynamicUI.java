package com.apogee.dummy_spinner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DynamicUI extends AppCompatActivity {
    public String tableName, tableId;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private LinearLayout columnLayout;
    SQLiteDatabase db;
    ArrayList<String> columnNames = new ArrayList<>();
    ArrayList<String> columnNamesValue = new ArrayList<>();
    String[] columns = null;
    String[] columnType = null;
    private static final int REQUEST_PICK_DOCUMENT = 1;
    private Bitmap imageBitmap;
    int idIndex;
    Uri documentUri;
    String strMappingId = "";
    ImageView imageView, imageView1;
    String status = "", strEDT = "";
    EditText edtFile;
    int desiredId;
    String selection, strPicturePathCamera, strPathDoc;

    List<String> editTextValues = new ArrayList<>();
    LinkedHashMap<String, String> createdValues = new LinkedHashMap<String, String>();
    String[] dataTypes = {"TEXT1", "TEXT2", "TEXT3", "TEXT4", "TEXT5", "TEXT6"};

    String[] selectionArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_ui);


        Intent intent = getIntent();
        tableName = intent.getStringExtra("table_name");
        tableId = intent.getStringExtra("form_id");
        desiredId = Integer.parseInt(tableId);

        // Define the selection query
        selection = "form_id = ?";
        selectionArgs = new String[]{String.valueOf(desiredId)};


        columnLayout = findViewById(R.id.columnLayout);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveData(tableName, columns));

        Button showButton = findViewById(R.id.showButton);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getColumns();
            }
        });

        getTableColumns(tableName);
        createUI(tableName);
//        createUI(columns);

//        saveButton.setOnClickListener(v -> saveData(tableId, columns));


    }


    private void createUI(String tableName) {

        // Replace "your_id_value" with the desired ID value


        ExternalDatabaseHelper mDatabaseHelper = new ExternalDatabaseHelper(DynamicUI.this);
        db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(tableName, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {

            idIndex = cursor.getColumnIndex("mapping_id");

            strMappingId = String.valueOf(idIndex);

            int nameIndex = cursor.getColumnIndex("column_name");
            // Check if the columns exist in the cursor
            if (idIndex != -1 && nameIndex != -1) {
                do {


                    int dataType = (cursor.getColumnIndex("subtype_id"));
                    String dataTypee = cursor.getString(cursor.getColumnIndex("subtype_id"));


                    String columnName = cursor.getString(cursor.getColumnIndex("column_name"));
                    String IsSelected = cursor.getString(cursor.getColumnIndex("is_selected"));

                    columnNames.add(columnName);


                    String subType = getColumnSubtypeName(db, "ColumnSubtype", Integer.parseInt(dataTypee));

                    if ((subType.equalsIgnoreCase("INTEGER") || subType.equalsIgnoreCase("FLOAT") || subType.equalsIgnoreCase("DOUBLE")) && IsSelected.equalsIgnoreCase("No")) {
                        int id = cursor.getInt(idIndex);

                        TextView columnNameTextView = new TextView(this);
                        columnNameTextView.setText(columnName);

                        // Create a new EditText and set the text
                        EditText editText = new EditText(this);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setHint("Enter " + columnName);
                        // Set an identifier for the EditText
                        editText.setId(id);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                // Do nothing
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                // Store the value of the EditText in the list


                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                createdValues.remove(columnName);
                                createdValues.put(columnName, editable.toString());
                                // Do nothing
                            }
                        });
                        // Add the EditText to the parent layout
                        columnLayout.addView(columnNameTextView);
                        columnLayout.addView(editText);

                    } else if ((subType.equalsIgnoreCase("INTEGER") || subType.equalsIgnoreCase("FLOAT") || subType.equalsIgnoreCase("DOUBLE")) && IsSelected.equalsIgnoreCase("Yes")) {
                        int id = cursor.getInt(idIndex);

                        TextView columnNameTextView = new TextView(this);
                        columnNameTextView.setText(columnName);

                        // Create a new Spinner and set the text

                        LinearLayout.LayoutParams layoutParamsDataType = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                100);
                        layoutParamsDataType.setMargins(20, 10, 20, 10);


                        Spinner selectValues = new Spinner(this);

                        selectValues.setLayoutParams(layoutParamsDataType);

                        selectValues.setBackground(getResources().getDrawable(R.drawable.spinner_back));


                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataTypes);
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        selectValues.setAdapter(adapter);

                        selectValues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                String strSelectedVal = (String) adapterView.getItemAtPosition(i);

                                createdValues.put(columnName, strSelectedVal);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });


                        // Add the EditText to the parent layout
                        columnLayout.addView(columnNameTextView);
                        columnLayout.addView(selectValues);

                    } else if ((subType.equalsIgnoreCase("STRING") || subType.equalsIgnoreCase("LOCATION")) && IsSelected.equalsIgnoreCase("No")) {

                        int id = cursor.getInt(idIndex);


                        TextView columnNameTextView = new TextView(this);
                        columnNameTextView.setText(columnName);

                        // Create a new EditText and set the text
                        EditText editText = new EditText(this);
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        editText.setHint("Enter " + columnName);

                        // Set an identifier for the EditText
                        editText.setId(id);
                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                // Do nothing
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                // Store the value of the EditText in the list


                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                // Do nothing
                                createdValues.remove(columnName);
                                createdValues.put(columnName, editable.toString());
                            }
                        });
                        // Add the EditText to the parent layout
                        Log.d(TAG, "strEDT " + strEDT);

                        columnLayout.addView(columnNameTextView);
                        columnLayout.addView(editText);
                    } else if ((subType.equalsIgnoreCase("STRING")) && IsSelected.equalsIgnoreCase("Yes")) {

                        int id = cursor.getInt(idIndex);


                        TextView columnNameTextView = new TextView(this);
                        columnNameTextView.setText(columnName);

                        // Create a new EditText and set the text
                        LinearLayout.LayoutParams layoutParamsDataType = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                100);
                        layoutParamsDataType.setMargins(20, 10, 20, 10);


                        Spinner selectValues = new Spinner(this);

                        selectValues.setLayoutParams(layoutParamsDataType);

                        selectValues.setBackground(getResources().getDrawable(R.drawable.spinner_back));


                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataTypes);
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        selectValues.setAdapter(adapter);
                        selectValues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                String strSelectedVal = (String) adapterView.getItemAtPosition(i);

                                createdValues.put(columnName, strSelectedVal);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });


                        columnLayout.addView(columnNameTextView);
                        columnLayout.addView(selectValues);
                    } else if (subType.equalsIgnoreCase("IMAGE") || subType.equalsIgnoreCase("VIDEO") || subType.equalsIgnoreCase("AUDIO") || subType.equalsIgnoreCase("PDF") || subType.equalsIgnoreCase("EXCEL")) {

                        TextView columnNameTextView = new TextView(this);
                        columnNameTextView.setText(columnName);


                        LinearLayout layout = new LinearLayout(this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        edtFile = new EditText(this);
                        edtFile.setText("newfile");
                        edtFile.setVisibility(View.GONE);
                        Button chooseFileBTN = new Button(this);
                        chooseFileBTN.setText("Choose Image");
                        chooseFileBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dispatchTakePictureIntent();
                            }
                        });


                        imageView = new ImageView(this);
                        imageView.setImageResource(R.drawable.image);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(212, 212));
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        imageView.setVisibility(View.GONE);


                        layout.addView(chooseFileBTN);
                        layout.addView(imageView);


                        columnLayout.addView(columnNameTextView);
                        columnLayout.addView(edtFile);

                        columnLayout.addView(layout);

                    } else if (subType.equalsIgnoreCase("PICTURE")) {

                        TextView columnNameTextView = new TextView(this);
                        columnNameTextView.setText(columnName);


                        LinearLayout layout = new LinearLayout(this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                        Button chooseFileBTN = new Button(this);
                        chooseFileBTN.setText("Open Camera");
                        chooseFileBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dispatchOpenCameraIntent();
                            }
                        });


                        imageView1 = new ImageView(this);
                        imageView1.setImageResource(R.drawable.image);
                        imageView1.setLayoutParams(new LinearLayout.LayoutParams(212, 212));
                        imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
                        imageView1.setVisibility(View.GONE);

                        Button saveFileBTN = new Button(this);
                        saveFileBTN.setText("Save Image");


                        layout.addView(chooseFileBTN);
                        layout.addView(imageView1);


                        columnLayout.addView(columnNameTextView);
                        columnLayout.addView(layout);


                    } else {
                        // Get the values from the cursor
                        int id = cursor.getInt(idIndex);

                        TextView columnNameTextView = new TextView(this);
                        columnNameTextView.setText(columnName);


                        // Create a new EditText and set the text
                        EditText editText = new EditText(this);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setHint("Enter " + columnName);
                        // Set an identifier for the EditText
                        editText.setId(id);

                        // Add the EditText to the parent layout
                        columnLayout.addView(columnNameTextView);
                        columnLayout.addView(editText);
                    }
                } while (cursor.moveToNext());
            } else {
                Log.e(TAG, "Column index not found in cursor");
                Toast.makeText(this, "Error reading data from cursor", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Cursor is empty");
            Toast.makeText(this, "No data found in the table", Toast.LENGTH_SHORT).show();
        }

        // Close the cursor and database
        cursor.close();
        db.close();

    }


    private String[] getTableColumns(String tableName) {
        ExternalDatabaseHelper mDatabaseHelper = new ExternalDatabaseHelper(DynamicUI.this);
        db = mDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);

        if (cursor != null) {
            columns = cursor.getColumnNames();
            columnType = cursor.getColumnNames();
            cursor.close();
        }

        return columns;
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

    private void dispatchOpenCameraIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    private void dispatchTakePictureIntent() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_PICK_DOCUMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_DOCUMENT && resultCode == RESULT_OK) {
            documentUri = data.getData();
            strPathDoc = documentUri.toString();
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(documentUri);
            editTextValues.add(strPathDoc);
            createdValues.put("file", strPathDoc);

            Toast.makeText(this, "" + strPathDoc, Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView1.setVisibility(View.VISIBLE);
            imageView1.setImageBitmap(imageBitmap);

            saveImageBitmap(imageBitmap);
            editTextValues.add(strPicturePathCamera);

            createdValues.put("Camera", strPicturePathCamera);

            Toast.makeText(this, "" + strPicturePathCamera, Toast.LENGTH_SHORT).show();


        }
    }

    public String saveImageBitmap(Bitmap bitmap) {
        // Create a directory to save the image
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyApp");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate a unique filename for the image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";

        // Create the file and save the image
        File file = new File(directory, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the absolute path of the saved image
        String imagePath = file.getAbsolutePath();
        strPicturePathCamera = imagePath;

        return imagePath;
    }


    private void saveData(String tableName, String[] columns) {


    }

    private void getColumns() {

        editTextValues.clear();


        for (Map.Entry<String, String> item : createdValues.entrySet()) {

            editTextValues.add(item.getValue());
        }
        Log.d(TAG, "getColumns: " + editTextValues);

        Toast.makeText(this, "" + columnNames.toString() + editTextValues.toString(), Toast.LENGTH_SHORT).show();


       insertColumnName(columnNames,editTextValues);

        Intent intent = new Intent(DynamicUI.this, ShowData.class);
        intent.putStringArrayListExtra("column_name", columnNames);
        intent.putStringArrayListExtra("column_values", (ArrayList<String>) editTextValues);
        intent.putExtra("table_id", tableId);
        startActivity(intent);


//       insertColumnValues(editTextValues);





    }


    private void insertColumnName(ArrayList<String> columnNames, List<String> editTextValues) {

        for (int i = 0; i < columnNames.size(); i++) {

            String columnName = columnNames.get(i);
            String columnValue = editTextValues.get(i);


            saveNameintoShowData(columnName,columnValue);


        }

    }



    private void saveNameintoShowData(String columnName, String columnValue) {

        ExternalDatabaseHelper mDatabaseHelper = new ExternalDatabaseHelper(DynamicUI.this);

        SQLiteDatabase dbm = mDatabaseHelper.getWritableDatabase();


        String tableName = "ShowData";
        String column1Name = "column_name";
        String column2Name = "column_value";
        String column3Name = "form_id";


        ContentValues valuesMap = new ContentValues();
        valuesMap.put(column1Name, columnName);
        valuesMap.put(column2Name, columnValue);
        valuesMap.put(column3Name, tableId);


        dbm.insert(tableName, null, valuesMap);

        Toast.makeText(this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();



    }


}