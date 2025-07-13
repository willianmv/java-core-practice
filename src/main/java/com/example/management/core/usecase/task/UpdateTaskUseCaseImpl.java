package com.example.management.core.usecase.task;

import com.example.management.core.domain.Column;
import com.example.management.core.domain.Task;
import com.example.management.core.dto.input.UpdateTaskInput;
import com.example.management.core.exception.DuplicateTitleException;
import com.example.management.core.exception.InvalidDueDateException;
import com.example.management.core.gateway.ColumnGateway;
import com.example.management.core.gateway.TaskGateway;

import java.time.LocalDate;

public class UpdateTaskUseCaseImpl implements UpdateTaskUseCase{

    private final TaskGateway taskGateway;

    public UpdateTaskUseCaseImpl(TaskGateway taskGateway) {
        this.taskGateway = taskGateway;
    }

    @Override
    public Task execute(UpdateTaskInput updateTaskInput) {
        Task existingTask = taskGateway.findById(updateTaskInput.id());
        Column existingColumn = existingTask.getColumn();
        validateDueDate(updateTaskInput.dueDate());
        validateDuplicateTitleInBoard(updateTaskInput.title(), existingColumn.getBoard().getId(), updateTaskInput.id());

        existingTask.setTitle(updateTaskInput.title());
        existingTask.setDescription(updateTaskInput.description());
        existingTask.setDueDate(updateTaskInput.dueDate());

        return taskGateway.save(existingTask);
    }

    private void validateDueDate(LocalDate dueDate){
        if(dueDate.isBefore(LocalDate.now()))
            throw new InvalidDueDateException("Due date cannot be in the past.");
    }

    private void validateDuplicateTitleInBoard(String title, long boardId, long id){
        if(taskGateway.existsByTitleInBoardAndIdNot(title, boardId, id))
            throw new DuplicateTitleException("Title: \""+ title +"\" already in use.");
    }
}
