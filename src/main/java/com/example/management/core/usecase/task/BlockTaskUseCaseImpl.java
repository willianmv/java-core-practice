package com.example.management.core.usecase.task;

import com.example.management.core.domain.Task;
import com.example.management.core.gateway.TaskGateway;

public class BlockTaskUseCaseImpl implements BlockTaskUseCase{

    private final TaskGateway taskGateway;

    public BlockTaskUseCaseImpl(TaskGateway taskGateway) {
        this.taskGateway = taskGateway;
    }

    @Override
    public void execute(long taskId) {
        Task existingTask = taskGateway.findById(taskId);
        if (!existingTask.isBlocked()){
            existingTask.block();
            taskGateway.save(existingTask);
        }
    }
}
