package com.example.management.infrastructure.persistence;

import com.example.management.core.domain.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    Board save(Board board);

    boolean existsById(long id);

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, long id);

    List<Board> getAll();

    Optional<Board> findById(long id);

    void deleteById(long id);
}
