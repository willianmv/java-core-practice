package com.example.management.core.usecase.task;

import com.example.management.core.domain.Task;
import com.example.management.core.gateway.TaskGateway;

public class UnblockTaskUseCaseImpl implements UnblockTaskUseCase{

    private final TaskGateway taskGateway;

    public UnblockTaskUseCaseImpl(TaskGateway taskGateway) {
        this.taskGateway = taskGateway;
    }

    @Override
    public void execute(long taskId) {
        Task existingTask = taskGateway.findById(taskId);
        if(existingTask.isBlocked()){
            existingTask.unblock();
            taskGateway.save(existingTask);
        }
    }
}
