package com.ademkok.project.database.csv.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class SearchResult {

    private final String tableName;
    private final List<Column> columns;
    private final List<Row> rows;

}
