package com.example.management.infrastructure.gateway;

import com.example.management.core.domain.Column;
import com.example.management.core.gateway.ColumnGateway;
import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.infrastructure.persistence.ColumnRepository;

import java.util.List;

public class ColumnRepositoryGateway implements ColumnGateway {

    private final ColumnRepository columnRepository;

    public ColumnRepositoryGateway(ColumnRepository columnRepository) {
        this.columnRepository = columnRepository;
    }

    @Override
    public void save(Column column) {
         this.columnRepository.save(column);
    }

    @Override
    public Column findById(long id) {
        return columnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found by ID: \""+id+"\""));
    }

    @Override
    public List<Column> getAllByBoardId(long boardId) {
        return columnRepository.findAllByBoardId(boardId);
    }
}
