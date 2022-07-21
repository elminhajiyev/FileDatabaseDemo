package az.emanat.filedatabasedemo.controller;

import az.emanat.filedatabasedemo.dao.Database;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/database/{tableName}")
public class DatabaseController {
    private final Database database;

    public DatabaseController(Database database) {
        this.database = database;
    }

    @PostMapping
    public int create(@PathVariable String tableName, @RequestBody List<String> values) {
        return database.insert(tableName, values);
    }

    @PutMapping("/{rowId}")
    public void update(@PathVariable String tableName, @PathVariable int rowId, @RequestBody List<String> values) {
        database.update(tableName, rowId, values);
    }

    @GetMapping("/{rowId}")
    public List<String> get(@PathVariable String tableName, @PathVariable int rowId) {
        return database.select(tableName, rowId);
    }
}
