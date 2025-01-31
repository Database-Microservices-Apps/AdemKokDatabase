package com.ademkok.project.database.csv;

import com.ademkok.project.database.DatabaseEngine;
import com.ademkok.project.database.csv.models.*;
import com.ademkok.project.database.exception.DatabaseException;
import lombok.AllArgsConstructor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DatabaseEngineImpl implements DatabaseEngine {

    private final String basePath;
    public static final String COMMA = ",";

    @Override
    public void createTable(String tableName, List<Column> columns) {
        String tableFile = basePath + File.separatorChar + tableName + ".csv";

        try {
            FileWriter fileWriter = new FileWriter(tableFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String headerLine = columns.stream().map(column -> column.getName().trim())
                            .collect(Collectors.joining(COMMA));

            bufferedWriter.write(headerLine);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException ex) {
            throw new DatabaseException("Error when creating the file: "+ tableFile);
        }
    }

    @Override
    public int insertIntoTable(String tableName, List<Row> rows) {
        return 0;
    }

    @Override
    public SearchResult selectFromTAble(String tableName, List<String> fields, List<Filter> filters, Order order) {
        return null;
    }

    @Override
    public int deleteFromTable(String tableName, List<Filter> filters) {
        return 0;
    }

    @Override
    public void dropTable(String tableName) {

    }

    @Override
    public int updateTAble() {
        return 0;
    }
}
