package com.apogee.dummy_spinner.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ColumnType")
public class ColumnTypeEntity {
    @PrimaryKey
    @ColumnInfo(name = "type_id")
    private int id;

    @ColumnInfo(name = "type_name")
    private String typeName;

    // Generate getters and setters

    public ColumnTypeEntity(int id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}

