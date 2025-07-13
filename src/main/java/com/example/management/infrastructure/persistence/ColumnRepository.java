package com.example.management.infrastructure.persistence;

import com.example.management.core.domain.Column;

import java.util.List;
import java.util.Optional;

public interface ColumnRepository {
    void save(Column column);

    Optional<Column> findById(long id);

    List<Column> findAllByBoardId(long boardId);
}
