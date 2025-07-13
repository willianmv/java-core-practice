package com.example.management.core.gateway;

import com.example.management.core.domain.Column;

import java.util.List;

public interface ColumnGateway {

    void save(Column column);

    Column findById(long id);

    List<Column> getAllByBoardId(long boardId);

}
