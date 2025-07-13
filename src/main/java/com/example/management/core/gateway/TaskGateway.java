package com.example.management.core.gateway;

import com.example.management.core.domain.Task;

import java.util.List;

public interface TaskGateway {

    Task save(Task task);

    Task findById(long id);

    boolean existsById(long id);

    boolean existsByTitleInBoard(String title, long boardId);

    boolean existsByTitleInBoardAndIdNot(String title, long boardId, long id);

    List<Task> getAllByBoardId(long boardId);

    List<Task> getAllByColumnId(long columnId);

    void deleteById(long id);
}
