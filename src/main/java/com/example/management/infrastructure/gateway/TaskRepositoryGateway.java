package com.example.management.infrastructure.gateway;

import com.example.management.core.domain.Task;
import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.core.gateway.TaskGateway;
import com.example.management.infrastructure.persistence.TaskRepository;
import com.example.management.infrastructure.persistence.file.InFileTaskRepository;

import java.util.List;

public class TaskRepositoryGateway implements TaskGateway {

    private final TaskRepository taskRepository;

    public TaskRepositoryGateway(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task findById(long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with ID: "+id));
    }

    @Override
    public boolean existsById(long id) {
        return taskRepository.existsById(id);
    }

    @Override
    public boolean existsByTitleInBoard(String title, long boardId) {
        return taskRepository.existsByTitleInBoard(title, boardId);
    }

    @Override
    public boolean existsByTitleInBoardAndIdNot(String title, long boardId, long id) {
        return taskRepository.existsByTitleInBoardAndIdNot(title, boardId, id);
    }

    @Override
    public List<Task> getAllByBoardId(long boardId) {
        return taskRepository.findAllByBoardId(boardId);
    }

    @Override
    public List<Task> getAllByColumnId(long columnId) {
        return taskRepository.findAllByColumnId(columnId);
    }

    @Override
    public void deleteById(long id) {
        taskRepository.deleteById(id);
    }
}
