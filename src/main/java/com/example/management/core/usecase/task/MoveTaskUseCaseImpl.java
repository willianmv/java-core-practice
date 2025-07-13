package com.example.management.core.usecase.task;

import com.example.management.core.domain.Column;
import com.example.management.core.domain.Task;
import com.example.management.core.dto.input.MoveTaskInput;
import com.example.management.core.gateway.ColumnGateway;
import com.example.management.core.gateway.TaskGateway;

public class MoveTaskUseCaseImpl implements MoveTaskUseCase {

    private final TaskGateway taskGateway;
    private final ColumnGateway columnGateway;

    public MoveTaskUseCaseImpl(TaskGateway taskGateway, ColumnGateway columnGateway) {
        this.taskGateway = taskGateway;
        this.columnGateway = columnGateway;
    }

    @Override
    public void execute(MoveTaskInput moveTaskInput) {
        Task existingTask = taskGateway.findById(moveTaskInput.taskId());
        Column destinationColumn = columnGateway.findById(moveTaskInput.columnId());

        if(existingTask.getColumn().equals(destinationColumn)) {
            throw new IllegalArgumentException("Task is already in the destination column");
        }

        existingTask.move(destinationColumn);
        taskGateway.save(existingTask);
    }
}
