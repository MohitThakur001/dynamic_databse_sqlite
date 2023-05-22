package com.apogee.dummy_spinner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExternalDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "updateui.db";
    private static final int DATABASE_VERSION = 1;

    private final Context mContext;
    private SQLiteDatabase mDatabase;

    public ExternalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();

        if (!dbExist) {
            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (IOException e) {
                Log.e(TAG, "Error copying database", e);
                throw new IOException("Error copying database");
            }
        }
    }

    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            String dbPath = mContext.getDatabasePath(DATABASE_NAME).getPath();
            checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            Log.e(TAG, "Database does not exist yet.");
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null;
    }

    private void copyDatabase() throws IOException {
        InputStream input = mContext.getAssets().open("databases/" + DATABASE_NAME);
        String outputPath = mContext.getDatabasePath(DATABASE_NAME).getPath();
        OutputStream output = new FileOutputStream(outputPath);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        input.close();
    }

    public void openDatabase() {
        String dbPath = mContext.getDatabasePath(DATABASE_NAME).getPath();
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (mDatabase != null)
            mDatabase.close();
        super.close();
    }

    // Implement the necessary methods for your database operations

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This method is called when the database is created for the first time.
        // You can execute your SQL statements to create tables or initialize data here.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This method is called when the database needs to be upgraded.
        // You can handle database schema changes or data migration here.
    }
}