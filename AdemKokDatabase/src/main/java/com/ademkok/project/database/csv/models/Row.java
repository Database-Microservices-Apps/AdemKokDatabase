package com.ademkok.project.database.csv.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Data
public class Row {

    private final int id;
    private final List<String> fields;

    public static Row newRow(List<String> fileds) {
        return new Row(0, fileds);
    }

    
}
