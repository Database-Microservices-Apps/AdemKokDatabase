package com.ademkok.project.database.csv;

import com.ademkok.project.database.csv.models.*;

import java.util.List;

public interface DatabaseEngine {
    void createTable(String tableName, List<Column> columns);
    int insertIntoTable(String tableName, List<Row> rows);
    SearchResult selectFromTAble(String tableName, List<String> fields, List<Filter> filters, Order order);
    int deleteFromTable(String tableName, List<Filter> filters);
    void dropTable(String tableName);
    int updateTAble();
}
