package com.ademkok.project.database.csv.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Column {

    public static final String STRING_COLUMN = "STRING";

    private final String name;
    private final String type;

    public static Column stringColumn(String name) {
        return new Column(name, STRING_COLUMN);
    }
}
