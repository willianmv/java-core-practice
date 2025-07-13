package com.example.management.core.usecase.task;

import com.example.management.core.domain.Column;
import com.example.management.core.domain.Task;
import com.example.management.core.dto.input.CreateTaskInput;
import com.example.management.core.exception.DuplicateTitleException;
import com.example.management.core.exception.InvalidDueDateException;
import com.example.management.core.gateway.ColumnGateway;
import com.example.management.core.gateway.TaskGateway;

import java.time.LocalDate;

public class CreateTaskUseCaseImpl implements CreateTaskUseCase{

    private final TaskGateway taskGateway;
    private final ColumnGateway columnGateway;

    public CreateTaskUseCaseImpl(TaskGateway taskGateway, ColumnGateway columnGateway) {
        this.taskGateway = taskGateway;
        this.columnGateway = columnGateway;
    }

    @Override
    public Task execute(CreateTaskInput createTaskInput) {
        Column column = columnGateway.findById(createTaskInput.columnId());
        validateDueDate(createTaskInput.dueDate());
        validateDuplicateTitleInBoard(createTaskInput.title(), column.getBoard().getId());

        Task task = new Task(
                null,
                createTaskInput.title(),
                createTaskInput.description(),
                createTaskInput.dueDate(),
                column);

        return taskGateway.save(task);
    }

    private void validateDueDate(LocalDate dueDate){
        if(dueDate.isBefore(LocalDate.now()))
            throw new InvalidDueDateException("Due date cannot be in the past.");
    }

    private void validateDuplicateTitleInBoard(String title, long boardId){
        if(taskGateway.existsByTitleInBoard(title, boardId))
            throw new DuplicateTitleException("Title: \""+ title +"\" already in use.");
    }
}
