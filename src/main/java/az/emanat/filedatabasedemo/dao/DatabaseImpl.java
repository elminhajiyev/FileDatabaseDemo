package az.emanat.filedatabasedemo.dao;

import az.emanat.filedatabasedemo.exception.NotFoundException;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DatabaseImpl implements Database {
    private static final String DB_PATH = "db/";

    @Override
    public int insert(String tableName, List<String> values) {
        List<String> fileContent = getFileContent(tableName, true);

        int rowId = fileContent.size();

        StringBuilder sb = new StringBuilder();

        // add ID as a first column
        sb.append(rowId);

        // add values as following columns
        for (String val : values) {
            if (val != null && val.length() > 0) {
                sb.append(',').append(val);
            }
        }

        fileContent.add(sb.toString());

        try {
            Files.write(getTableFile(tableName).toPath(), fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write to table!");
        }

        return rowId;
    }

    @Override
    public void update(String tableName, int rowId, List<String> values) throws NotFoundException {
        List<String> fileContent = getFileContent(tableName, false);

        int count = fileContent.size();

        if (rowId <= 0 || rowId >= count) throw new NotFoundException("Element with ID " + rowId + " not found!");

        StringBuilder sb = new StringBuilder();

        // add ID as a first column
        sb.append(rowId);

        // add values as following columns
        for (String val : values) {
            if (val != null && val.length() > 0) {
                sb.append(',').append(val);
            }
        }

        fileContent.set(rowId, sb.toString());

        try {
            Files.write(getTableFile(tableName).toPath(), fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write to table!");
        }
    }

    @Override
    public List<String> select(String tableName, int rowId) throws NotFoundException {
        List<String> fileContent = getFileContent(tableName, false);

        int count = fileContent.size();

        if (rowId <= 0 || rowId >= count) throw new NotFoundException("Element with ID " + rowId + " not found!");

        return Arrays.stream(fileContent.get(rowId).split(","))
                .skip(1)
                .collect(Collectors.toList());
    }

    private List<String> getFileContent(String tableName, boolean createIfMissing) {
        File table = getTableFile(tableName);

        if (!table.exists()) {
            if (createIfMissing) {
                try {
                    if (!table.createNewFile()) throw new IOException();
                } catch (IOException e) {
                    throw new RuntimeException("Cannot create table!");
                }
            } else {
                throw new NotFoundException(tableName + " table not found!");
            }
        }

        try {
            return new ArrayList<>(Files.readAllLines(table.toPath(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read table!");
        }
    }

    private File getTableFile(String tableName) {
        return new File(DB_PATH + tableName + ".csv");
    }
}
