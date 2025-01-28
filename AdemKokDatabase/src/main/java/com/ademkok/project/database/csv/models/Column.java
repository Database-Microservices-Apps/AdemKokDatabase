package com.ademkok.project.database.csv.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Column {

    public static final String STRING = "STRING";

    private final String name;
    private final String type;
}
