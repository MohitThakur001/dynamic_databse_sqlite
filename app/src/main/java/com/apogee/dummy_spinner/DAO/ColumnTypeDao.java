package com.apogee.dummy_spinner.DAO;

import androidx.room.Dao;
import androidx.room.Query;

import com.apogee.dummy_spinner.Entities.ColumnTypeEntity;

import java.util.List;

@Dao
public interface ColumnTypeDao {
    @Query("SELECT * FROM ColumnType")
    List<ColumnTypeEntity> getAllColumnTypes();
}
