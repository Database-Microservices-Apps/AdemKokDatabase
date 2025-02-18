package com.ademkok.project.database.csv.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TableData {
    private final TableHeader tableHeader;
    private final List<Row> rows;

}
