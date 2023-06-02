package com.apogee.dummy_spinner.DAO;

import androidx.room.Dao;
import androidx.room.Query;

import com.apogee.dummy_spinner.Entities.ColumnSubtypeEntity;

import java.util.List;

@Dao
public interface ColumnSubtypeDao {
    @Query("SELECT * FROM ColumnSubtype WHERE type_id = :selectedOption")
    List<ColumnSubtypeEntity> getColumnSubtypes(int selectedOption);
}

