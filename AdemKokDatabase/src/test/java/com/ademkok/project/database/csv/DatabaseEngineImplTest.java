package com.ademkok.project.database.csv;

import com.ademkok.project.database.DatabaseEngine;
import com.ademkok.project.database.csv.models.Column;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static com.ademkok.project.database.csv.models.Column.stringColumn;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseEngineImplTest {

    private String tempDir;
    private DatabaseEngine databaseEngine;

    @BeforeEach
    void setUp() {
        tempDir = Files.newTemporaryFolder().getAbsolutePath();
        System.out.println("TempDir: "+tempDir);
        databaseEngine = new DatabaseEngineImpl(tempDir);
    }

    @AfterEach
    void tearDown() {
        //Files.delete(new File(tempDir));
    }

    @Test
    void createTable() {
        databaseEngine.createTable("Baris",
                List.of(
                        stringColumn("id"),
                        stringColumn("firstName"),
                        stringColumn("lastName")
                ));
    }
}