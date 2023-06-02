package com.apogee.dummy_spinner.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.apogee.dummy_spinner.DAO.ColumnSubtypeDao;
import com.apogee.dummy_spinner.DAO.ColumnTypeDao;
import com.apogee.dummy_spinner.Entities.ColumnSubtypeEntity;
import com.apogee.dummy_spinner.Entities.ColumnTypeEntity;

@Database(entities = {ColumnTypeEntity.class, ColumnSubtypeEntity.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract ColumnTypeDao columnTypeDao();
    public abstract ColumnSubtypeDao columnSubtypeDao();

    private static MyDatabase instance;

    public static synchronized MyDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            MyDatabase.class, "updateui.db")
                    .createFromAsset("databases/updateui.db")
                    .build();
        }
        return instance;
    }
}