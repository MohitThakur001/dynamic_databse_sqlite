package com.apogee.dummy_spinner.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.apogee.dummy_spinner.DAO.ColumnTypeDao;
import com.apogee.dummy_spinner.Entities.ColumnTypeEntity;

@Database(entities = {ColumnTypeEntity.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract ColumnTypeDao columnTypeDao();
}
