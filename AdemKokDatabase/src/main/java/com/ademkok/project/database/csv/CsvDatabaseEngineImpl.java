package com.ademkok.project.database.csv;

import com.ademkok.project.database.DatabaseEngine;
import com.ademkok.project.database.csv.models.*;
import com.ademkok.project.database.exception.DatabaseException;
import lombok.AllArgsConstructor;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CsvDatabaseEngineImpl implements DatabaseEngine {

    private final String basePath;
    public static final String COMMA = ",";

    @Override
    public void createTable(String tableName, List<Column> columns) {
        verifyTableDoesNotExist(tableName);
        String tableFile = fileNameForTable(tableName);

        performActionInTable(tableName, true, bw ->{

            String headerLine = columns.stream()
                    .map(column -> column.getName().trim())
                    .collect(Collectors.joining(COMMA));

            writeContent(bw, headerLine, tableName);
        });
    }


    @Override
    public int insertIntoTable(String tableName, List<Row> rows) {
        verifyTableExists(tableName);
        return performActionInTable(tableName, true, bw -> {
            rows.stream()
                .map(Row::getFields)
                .map(this::escapeCharacters)
                .forEach(line -> writeContent(bw, line, tableName));
            return rows.size();
        });
    }



    @Override
    public SearchResult selectFromTable(String tableName, List<String> fields, List<Filter> filters, Order order) {

        TableData tableDate = readTableData(tableName);

        return null;
    }

    private TableData readTableData(String tableName) {
        verifyTableExists(tableName);

        try( BufferedReader br = new BufferedReader(new FileReader(fileNameForTable(tableName)))) {

            TableHeader header = parseHeader(br);
        } catch (IOException ex) {
            throw new DatabaseException("Error when reading file: "+ tableName);
        }
        return null;
    }

    private TableHeader parseHeader(BufferedReader br) throws IOException {
        String line = br.readLine();
        if(line == null || line.trim().length() == 0) {
            throw new DatabaseException("Table file is empty");
        }

        List<Column> columns = Arrays.stream(line.split(COMMA))
                .map(Column::stringColumn)
                .toList();

        // mit rows weiter
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
    public int updateTable() {
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

    private void writeContent(BufferedWriter bw, String text, String tableName) {
        try{
            bw.write(text);
            bw.newLine();
        } catch (IOException ex) {
            throw new DatabaseException("Unexpected error when writing content to file: "+ tableName);
        }
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
        return "\"" + returnValues + "\"";
    }

    private void performActionInTable(String tableName, boolean append, Consumer<BufferedWriter> consumer) {
        performActionInTable(tableName, append, bufferedWriter -> {
            consumer.accept(bufferedWriter);
            return 0;
        });
    }

    private <T> T performActionInTable(String tableName, boolean append, Function<BufferedWriter, T> function) {
        try( BufferedWriter bw = new BufferedWriter(new FileWriter(fileNameForTable(tableName), append))) {
            return function.apply(bw);
        } catch (IOException ex) {
            throw new DatabaseException("Error when creating the file: "+ tableName);
        }
    }
}
