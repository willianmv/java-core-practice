package com.example.management.core.gateway;

import com.example.management.core.domain.Board;

import java.util.List;

public interface BoardGateway {

    Board save(Board board);

    boolean existsById(long id);

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, long id);

    List<Board> getAll();

    Board findById(long id);

    void deleteById(long id);

}
