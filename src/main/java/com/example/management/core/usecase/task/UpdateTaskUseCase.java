package com.example.management.core.usecase.task;

import com.example.management.core.domain.Task;
import com.example.management.core.dto.input.UpdateTaskInput;

public interface UpdateTaskUseCase {

    Task execute(UpdateTaskInput updateTaskInput);

}
