package com.ademkok.project.database.csv;

import com.ademkok.project.database.DatabaseEngine;
import com.ademkok.project.database.csv.models.*;
import com.ademkok.project.database.exception.DatabaseException;
import lombok.AllArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

        List<Row> rows = tableDate.getRows();

        List<Row> foundRows =  rows.stream()
                .filter(row -> applyFilters(row, filters, tableDate.getTableHeader()))
                .map(row -> selectFileds(row, fields, tableDate.getTableHeader()))
                .toList();

        return new SearchResult(tableName, tableDate.getTableHeader().getColumns(), foundRows);
    }

    private Row selectFileds(Row row, List<String> fields, TableHeader tableHeader) {
        if(fields == null || fields.isEmpty()) {
            return row;
        } else {
            List<String> rowFileds = fields.stream().map(
                    field -> row.getFields().get(tableHeader.indexOfColumnWithName(field)))
                    .toList();
            return new Row(row.getId(), rowFileds);
        }
    }

    private boolean applyFilters(Row row, List<Filter> filters, TableHeader header) {
        if(filters == null || filters.isEmpty()) {
            return true;
        }
        return filters.stream().allMatch(filter -> {
           int columnIndex = header.indexOfColumnWithName(filter.getName());
           String valueInRow = row.getFields().get(columnIndex);
           return Objects.equals(valueInRow, filter.getValue());
        });
    }

    private TableData readTableData(String tableName) {
        verifyTableExists(tableName);

        try( BufferedReader br = new BufferedReader(new FileReader(fileNameForTable(tableName)))) {

            TableHeader header = parseHeader(br);
            List<Row> records = new ArrayList<>();

            String line;
            int rowId =1;
            while((line = br.readLine()) != null) {




                Row record = parseLine(rowId++, line);
                records.add(record);
            }

            return new TableData(header, records);
        } catch (IOException ex) {
            throw new DatabaseException("Error when reading file: "+ tableName);
        }
    }

    private Row parseLine(int rowId, String line) {
        List<String> values = new ArrayList<>();
        StringBuilder currentValue = new StringBuilder();
        boolean inQuotes = false;
        boolean escapeNext = false;

        for (char c : line.toCharArray()) {

            if(escapeNext) {
                currentValue.append(c);
                escapeNext = false;
            } else if (c == '\\') {
                escapeNext =true;
            } else if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(currentValue.toString());
                currentValue.setLength(0);
            } else {
                currentValue.append(c);
            }
        }

        values.add(currentValue.toString());

        return new Row(rowId, values);
    }

    private TableHeader parseHeader(BufferedReader br) throws IOException {
        String line = br.readLine();
        if(line == null || line.trim().length() == 0) {
            throw new DatabaseException("Table file is empty");
        }

        List<Column> columns = Arrays.stream(line.split(COMMA))
                .map(Column::stringColumn)
                .toList();
        return new TableHeader(columns);
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
