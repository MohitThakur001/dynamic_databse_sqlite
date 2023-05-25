package com.apogee.dummy_spinner;

public class DataModel {

    private String column_name;
    private String column_value;
    // Add more fields as needed

    public DataModel(String column_name, String column_value) {

        this.column_name = column_name;
        this.column_value = column_value;
    }



    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getColumn_value() {
        return column_value;
    }

    public void setColumn_value(String column_value) {
        this.column_value = column_value;
    }
}
