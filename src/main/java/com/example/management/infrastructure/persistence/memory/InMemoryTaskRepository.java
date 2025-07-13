package com.example.management.infrastructure.persistence.memory;

import com.example.management.core.domain.Task;
import com.example.management.infrastructure.persistence.TaskRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryTaskRepository implements TaskRepository {

    private final Map<Long, Task> storage = new HashMap<>();
    private long nextId = 1;

    @Override
    public Task save(Task task){
        if(task.getId() == null){
            task.setId(nextId++);
        }
        storage.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(long id) {
        return storage.values().stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    @Override
    public boolean existsById(long id) {
        return storage.values().stream()
                .anyMatch(task -> task.getId().equals(id));
    }

    @Override
    public boolean existsByTitleInBoard(String title, long boardId) {
        return findAllByBoardId(boardId).stream()
                .anyMatch(task -> task.getTitle().equalsIgnoreCase(title));
    }

    @Override
    public boolean existsByTitleInBoardAndIdNot(String title, long boardId, long id) {
        return findAllByBoardId(boardId).stream()
                .anyMatch(task -> task.getTitle().equalsIgnoreCase(title) && !task.getId().equals(id));
    }

    @Override
    public List<Task> findAllByBoardId(long boardId) {
        return storage.values().stream()
                .filter(task -> task.getColumn().getBoard().getId().equals(boardId))
                .toList();
    }

    @Override
    public List<Task> findAllByColumnId(long columnId) {
        return storage.values().stream()
                .filter(task -> task.getColumn().getId().equals(columnId))
                .toList();
    }

    @Override
    public void deleteById(long id) {
        storage.remove(id);
    }
}
