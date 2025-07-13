package com.example.management.infrastructure.persistence.memory;

import com.example.management.core.domain.Column;
import com.example.management.infrastructure.persistence.ColumnRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryColumnRepository implements ColumnRepository {

    private final Map<Long, Column> storage = new HashMap<>();
    private long nextId = 1;

    @Override
    public void save(Column column) {
        if(column.getId() == null){
            column.setId(nextId++);
        }
        storage.put(column.getId(), column);
    }

    @Override
    public Optional<Column> findById(long id) {
        return storage.values().stream()
                .filter(col -> col.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Column> findAllByBoardId(long boardId) {
        return storage.values().stream()
                .filter(col -> col.getBoard().getId().equals(boardId))
                .collect(Collectors.toList());
    }
}
