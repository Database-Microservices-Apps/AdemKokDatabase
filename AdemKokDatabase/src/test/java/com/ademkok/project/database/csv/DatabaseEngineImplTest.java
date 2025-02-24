package com.ademkok.project.database.csv;

import com.ademkok.project.database.DatabaseEngine;
import com.ademkok.project.database.csv.models.Filter;
import com.ademkok.project.database.csv.models.Row;
import com.ademkok.project.database.csv.models.SearchResult;
import org.assertj.core.api.AbstractFileAssert;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static com.ademkok.project.database.csv.models.Column.stringColumn;
import static com.ademkok.project.database.csv.models.Row.newRow;
import static org.assertj.core.api.Assertions.assertThat;
class DatabaseEngineImplTest {

    private String tempDir;
    private DatabaseEngine databaseEngine;
    public static final String TEST_TABLE = "MY_TABLE";

    @BeforeEach
    void setUp() {
        tempDir = Files.newTemporaryFolder().getAbsolutePath();
        System.out.println("TempDir: "+tempDir);
        databaseEngine = new CsvDatabaseEngineImpl(tempDir);
    }

    @AfterEach
    void tearDown() {
        //Files.delete(new File(tempDir));
    }

    @Test
    void createTable() {
        createTestTable();
        assertThatFile()
                .exists()
                .hasContent("id,firstName,lastName");
    }

    @Test
    void insertTable() {
        createTestTable();

        int rows = databaseEngine.insertIntoTable(TEST_TABLE, List.of(
                newRow(List.of("1", "Ayten", "Kok")),
                newRow(List.of("65", "Ad\"em", "Ko,,k"))
        ));
        
        assertThat(rows).isEqualTo(2);

        assertThatFile()
                .exists()
                .hasContent("""
                        id,firstName,lastName
                        "1","Ayten","Kok"
                        "65","Ad\\"em","Ko,,k"
                        """);
    }

    @Test
    void selectTable() {
        createTestTable();
        databaseEngine.insertIntoTable(TEST_TABLE, List.of(
                Row.newRow(List.of("1", "Ayten", "Kok")),
                Row.newRow(List.of("3", "Metin", "Kara")),
                Row.newRow(List.of("1", "Sema", "Kara")),
                Row.newRow(List.of("65", "Ad\"em", "Ko,,k"))
        ));

        SearchResult searchResult = databaseEngine.selectFromTable(
                TEST_TABLE,
                List.of("firstName", "lastName"),
                List.of(new Filter("id", "1")),
                null);
        assertThat(searchResult.getColumns()).containsExactly(
                stringColumn("id"),
                stringColumn("firstName"),
                stringColumn("lastName")
        );
        assertThat(searchResult.getRows()).containsExactly(
                new Row(1, List.of("Ayten","Kok")),
                new Row(3, List.of("Sema", "Kara"))
        );

    }

    private AbstractFileAssert<?> assertThatFile() {
        return assertThat(new File(tempDir + File.separatorChar + TEST_TABLE + ".csv"));
    }

    private void createTestTable() {
        databaseEngine.createTable(TEST_TABLE,
                List.of(
                        stringColumn("id"),
                        stringColumn("firstName"),
                        stringColumn("lastName")
                ));
    }
}