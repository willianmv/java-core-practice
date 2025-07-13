package com.example.management.infrastructure.persistence;

import com.example.management.core.domain.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(long id);

    boolean existsById(long id);

    boolean existsByTitleInBoard(String title, long boardId);

    boolean existsByTitleInBoardAndIdNot(String title, long boardId, long id);

    List<Task> findAllByBoardId(long boardId);

    List<Task> findAllByColumnId(long columnId);

    void deleteById(long id);
}
