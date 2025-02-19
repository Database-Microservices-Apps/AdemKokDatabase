package com.ademkok.project.database.csv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Data
public class Column {

    public static final String STRING_COLUMN = "STRING";

    private final String name;
    private final String type;

    public static Column stringColumn(String name) {
        return new Column(name, STRING_COLUMN);
    }
}
