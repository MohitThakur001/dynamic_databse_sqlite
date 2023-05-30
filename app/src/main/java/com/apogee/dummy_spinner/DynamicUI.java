package com.apogee.dummy_spinner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DynamicUI extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int REQUEST_PERMISSION = 200;
    private static final int REQUEST_CODE_CREATE_FILE = 201;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private Uri audioFileUri;
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
    TextView document_path;
    String status = "", strEDT = "";
    EditText edtFile;
    int desiredId;
    String selection, strPicturePathCamera, strPathDoc;

    List<String> editTextValues = new ArrayList<>();
    LinkedHashMap<String, String> createdValues = new LinkedHashMap<String, String>();
    String[] dataTypes = {"TEXT1", "TEXT2", "TEXT3", "TEXT4", "TEXT5", "TEXT6"};

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    TextView filePathTextView;

    String filePath;
    String[] selectionArgs;
    Button saveButton , showButton;
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

         saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> getColumns());


         showButton = findViewById(R.id.showButton);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DynamicUI.this, ShowData.class);

                startActivity(intent);
            }
        });

        getTableColumns(tableName);
        createUI(tableName);

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

                    }

                    else if ((subType.equalsIgnoreCase("INTEGER") || subType.equalsIgnoreCase("FLOAT") || subType.equalsIgnoreCase("DOUBLE")) && IsSelected.equalsIgnoreCase("Yes")) {
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

                    }

                    else if ((subType.equalsIgnoreCase("STRING")) && IsSelected.equalsIgnoreCase("No")) {

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
                    }

                    else if ((subType.equalsIgnoreCase("STRING")) && IsSelected.equalsIgnoreCase("Yes")) {

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
                    }

                    else if (subType.equalsIgnoreCase("IMAGE") || subType.equalsIgnoreCase("VIDEO") || subType.equalsIgnoreCase("PDF") || subType.equalsIgnoreCase("EXCEL")) {

                        TextView columnNameTextView = new TextView(this);
                        columnNameTextView.setText(columnName);


                        LinearLayout layout = new LinearLayout(this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        edtFile = new EditText(this);
                        edtFile.setText("newfile");
                        edtFile.setVisibility(View.GONE);
                        Button chooseFileBTN = new Button(this);
                        chooseFileBTN.setText("Choose File");
                        chooseFileBTN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dispatchTakePictureIntent();
                            }
                        });


//                        imageView = new ImageView(this);
//                        imageView.setImageResource(R.drawable.image);
//                        imageView.setLayoutParams(new LinearLayout.LayoutParams(212, 212));
//                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                        imageView.setVisibility(View.GONE);

                        document_path = new TextView(this);

//                        imageView.setVisibility(View.GONE);


                        layout.addView(chooseFileBTN);
                        layout.addView(document_path);


                        columnLayout.addView(columnNameTextView);
                        columnLayout.addView(edtFile);

                        columnLayout.addView(layout);

                    }

                    else if (subType.equalsIgnoreCase("PICTURE")) {

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


                    }

                    else if (subType.equals("Audio") || subType.equals("AUDIO")) {

                        LayoutInflater inflater = LayoutInflater.from(this);
                        View dynamicView = inflater.inflate(R.layout.dynamic_audio_view, null);

                        TextView columnNameTextView = new TextView(this);
                        columnNameTextView.setText(columnName);


                        // Set up UI components
                        Button recordButton = dynamicView.findViewById(R.id.recordButton);
                        Button stopButton = dynamicView.findViewById(R.id.stopButton);
                        Button saveButton = dynamicView.findViewById(R.id.saveButton);

                        // Disable the stop and save buttons initially
                        stopButton.setEnabled(false);
                        saveButton.setEnabled(false);

                        // Set click listeners for buttons
                        recordButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isRecording) {
                                    stopRecording();
                                } else {
                                    checkPermissions();
                                    startRecording();
                                }
                            }
                        });

                        stopButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                stopRecording();
                            }
                        });

                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestSavePermission();
                            }
                        });


                        columnLayout.addView(columnNameTextView);
                        columnLayout.addView(dynamicView);


                    }

                    else if (subType.equals("LOCATION")) {

                        LayoutInflater inflater = LayoutInflater.from(this);
                        View dynamicView = inflater.inflate(R.layout.dynamic_location, null);

                        TextView columnNameTextView = new TextView(this);
                        columnNameTextView.setText(columnName);


                        // Set up UI components
                        Button getLocationButton = dynamicView.findViewById(R.id.getLocationButton);
                        TextView locationTextView = dynamicView.findViewById(R.id.locationTextView);

                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                        getLocationButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ContextCompat.checkSelfPermission(DynamicUI.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    startLocationUpdates();
                                } else {
                                    requestLocationPermission();
                                }
                            }
                        });

                        locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                if (locationResult == null) {
                                    return;
                                }
                                Location location = locationResult.getLastLocation();
                                if (location != null) {
                                    String latitude = String.valueOf(location.getLatitude());
                                    String longitude = String.valueOf(location.getLongitude());
                                    String locationText = "Latitude: " + latitude + "\nLongitude: " + longitude;
                                    locationTextView.setText(locationText);
                                    createdValues.put(columnName, locationText);
                                }
                            }
                        };


                        columnLayout.addView(columnNameTextView);
                        columnLayout.addView(dynamicView);

                    }

                    else {
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


    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    private void startRecording() {

        mediaRecorder = new MediaRecorder();
        audioFileUri = createAudioFileUri();

        if (audioFileUri != null) {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(audioFileUri.getPath());

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;

                Button recordButton = findViewById(R.id.recordButton);
                Button stopButton = findViewById(R.id.stopButton);
                Button saveButton = findViewById(R.id.saveButton);
                recordButton.setEnabled(false);
                stopButton.setEnabled(true);
                saveButton.setEnabled(false);

                Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Failed to create audio file", Toast.LENGTH_SHORT).show();
        }

    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;

            Button stopButton = findViewById(R.id.stopButton);
            Button saveButton = findViewById(R.id.saveButton);
            stopButton.setEnabled(false);
            saveButton.setEnabled(true);

            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestSavePermission() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/3gp");
        intent.putExtra(Intent.EXTRA_TITLE, "recording.3gp");
        startActivityForResult(intent, REQUEST_CODE_CREATE_FILE);
    }


    private void saveRecording() {
        if (audioFileUri != null) {
            ContentResolver contentResolver = getContentResolver();
            try {
                InputStream inputStream = contentResolver.openInputStream(audioFileUri);
                if (inputStream != null) {
                    // Implement your logic to save the recording from the input stream
                    Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Unable to open input stream", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving recording", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No audio file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri createAudioFileUri() {
        ContentResolver contentResolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "recording.3gp");
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");

        Uri audioUri = null;
        try {
            audioUri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return audioUri;
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
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


    private boolean isRecording() {
        return isRecording;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_DOCUMENT && resultCode == RESULT_OK) {




            documentUri = data.getData();
            strPathDoc = documentUri.toString();

            document_path.setVisibility(View.VISIBLE);
            document_path.setText(documentUri.toString());
//            imageView.setVisibility(View.VISIBLE);
//            imageView.setImageURI(documentUri);

            try {

              if(documentUri.toString().contains("image") || documentUri.toString().contains("jpg") || documentUri.toString().contains("png")) {
                  byte[] byteArray = compressImageUri(getApplicationContext(), documentUri, 800, 600, 80);
                  editTextValues.add(Arrays.toString(byteArray));
                  createdValues.put("file", Arrays.toString(byteArray));

              }else {
                  try {
                      byte[] byteArray = convertUriToByteArray(this, documentUri);
                      Log.d(TAG, "onActivityResult: "+byteArray.toString());
                      editTextValues.add(Arrays.toString(byteArray));
                      createdValues.put("file", Arrays.toString(byteArray));
                      // Handle the byte array as needed (e.g., upload it, process it, etc.)
                  } catch (IOException e) {
                      e.printStackTrace();
                      // Handle the error appropriately
                  }


              }
                // Use the byte array as needed
            } catch (IOException e) {
                e.printStackTrace();
            }



        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView1.setVisibility(View.VISIBLE);
            imageView1.setImageBitmap(imageBitmap);

            saveImageBitmap(imageBitmap);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            editTextValues.add(Arrays.toString(byteArray));
            createdValues.put("Camera", Arrays.toString(byteArray));


        } else if (requestCode == REQUEST_CODE_CREATE_FILE && resultCode == RESULT_OK) {
            audioFileUri = data.getData();
            saveRecording();
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


    private void getColumns() {

      showButton.setVisibility(View.VISIBLE);


        editTextValues.clear();


        for (Map.Entry<String, String> item : createdValues.entrySet()) {

            editTextValues.add(item.getValue());
        }
        Log.d(TAG, "getColumns: " + editTextValues);


        insertColumnName(columnNames, editTextValues);


//       insertColumnValues(editTextValues);


    }


    private void insertColumnName(ArrayList<String> columnNames, List<String> editTextValues) {

        for (int i = 0; i < columnNames.size(); i++) {

            String columnName = columnNames.get(i);
            String columnValue = editTextValues.get(i);


            saveNameintoShowData(columnName, columnValue);


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


    public byte[] compressImageUri(Context context, Uri imageUri, int maxWidth, int maxHeight, int quality) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        // Calculate the new dimensions while maintaining the aspect ratio
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleRatio = Math.min((float) maxWidth / width, (float) maxHeight / height);
        int newWidth = (int) (width * scaleRatio);
        int newHeight = (int) (height * scaleRatio);

        // Create the scaled bitmap
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        // Compress the scaled bitmap to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

        // Release resources
        inputStream.close();
        bitmap.recycle();
        scaledBitmap.recycle();

        return byteArrayOutputStream.toByteArray();
    }

    private byte[] convertUriToByteArray(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        byteArrayOutputStream.close();
        inputStream.close();

        return byteArrayOutputStream.toByteArray();
    }

}