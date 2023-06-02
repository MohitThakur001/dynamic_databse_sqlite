package com.apogee.dummy_spinner.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "ColumnSubtype",
        foreignKeys = @ForeignKey(entity = ColumnTypeEntity.class,
                parentColumns = "type_id",
                childColumns = "type_id",
                onDelete = ForeignKey.NO_ACTION))


public class ColumnSubtypeEntity {
    @PrimaryKey
    @ColumnInfo(name = "subtype_id")
    private int subtypeId;

    @ColumnInfo(name = "type_id")
    private int typeId;

    @ColumnInfo(name = "subtype_name")
    private String subtypeName;



    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    // Constructor, getters, and setters

    public int getSubtypeId() {
        return subtypeId;
    }

    public void setSubtypeId(int subtypeId) {
        this.subtypeId = subtypeId;
    }

    public String getSubtypeName() {
        return subtypeName;
    }

    public ColumnSubtypeEntity(int subtypeId,  int typeId,String subtypeName) {
        this.subtypeId = subtypeId;
        this.typeId = typeId;
        this.subtypeName = subtypeName;

    }

    public void setSubtypeName(String subtypeName) {
        this.subtypeName = subtypeName;
    }
}

