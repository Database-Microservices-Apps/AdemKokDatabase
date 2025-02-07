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
public class CsvDatabaseEngineImpl implements DatabaseEngine {

    private final String basePath;
    public static final String COMMA = ",";

    @Override
    public void createTable(String tableName, List<Column> columns) {
        verifyTableDoesNotExist(tableName);
        String tableFile = fileNameForTable(tableName);

        try( BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tableFile, true))) {

            String headerLine = columns.stream().map(column -> column.getName().trim())
                            .collect(Collectors.joining(COMMA));

            bufferedWriter.write(headerLine);
            bufferedWriter.newLine();
        } catch (IOException ex) {
            throw new DatabaseException("Error when creating the file: "+ tableFile);
        }
    }

//    private void performActionInTable(String tableName, boolean append) {
//        try( BufferedWriter bw = new BufferedWriter(new FileWriter(fileNameForTable(tableName), append))) {
//
//            String headerLine = columns.stream().map(column -> column.getName().trim())
//                    .collect(Collectors.joining(COMMA));
//
//            bufferedWriter.write(headerLine);
//            bufferedWriter.newLine();
//        } catch (IOException ex) {
//            throw new DatabaseException("Error when creating the file: "+ tableFile);
//        }
//    }



    @Override
    public int insertIntoTable(String tableName, List<Row> rows) {
        verifyTableExists(tableName);
        
        rows.stream()
                .map(Row::getFields)
                .map(this::escapeCharacters);

        return 0;
    }

    private String escapeCharacters(List<String> values) {
        return values.stream()
                .map(CsvDatabaseEngineImpl::escapeDubleQuotes)
                .collect(Collectors.joining(COMMA));
    }

    private static String escapeDubleQuotes(String s) {
        String returnValues;
        if(s.contains("\"")) {
            returnValues = s.replaceAll("\"", "\\\\\"");
        }else {
            returnValues = s;
        }
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

    private String fileNameForTable(String tableName) {
        return basePath + File.separatorChar + tableName + ".csv";
    }

    private void verifyTableDoesNotExist(String tableName) {
        String tableFile = fileNameForTable(tableName);

        if(tableExists(tableName)) {
            throw new DatabaseException("Table: " + tableName + " already exists");
        }
    }

    private void verifyTableExists(String tableName) {
        String tableFile = fileNameForTable(tableName);

        if(!tableExists(tableName)) {
            throw new DatabaseException("Table: " + tableName + " does not exist");
        }
    }

    private boolean tableExists(String tableName) {
        String tableFile = fileNameForTable(tableName);
        return  new File(tableFile).exists();
    }
}
