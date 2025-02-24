package com.ademkok.project.database.csv.models;

import com.ademkok.project.database.exception.DatabaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TableHeader {
    private final List<Column> columns;

    public int indexOfColumnWithName(String columnName) {
        for (int i=0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if (columnName.equalsIgnoreCase(column.getName())) {
                return i;
            }
        }
        throw new DatabaseException("No column found with name: " + columnName);
    }

}
