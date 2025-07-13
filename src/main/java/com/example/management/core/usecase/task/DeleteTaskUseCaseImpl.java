package com.example.management.core.usecase.task;

import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.core.gateway.TaskGateway;

public class DeleteTaskUseCaseImpl implements DeleteTaskUseCase{

    private final TaskGateway taskGateway;

    public DeleteTaskUseCaseImpl(TaskGateway taskGateway) {
        this.taskGateway = taskGateway;
    }

    @Override
    public void execute(long id) {
        boolean exists = taskGateway.existsById(id);
        if(!exists) throw new EntityNotFoundException("Entity not found with ID: "+id);
        taskGateway.deleteById(id);
    }
}
