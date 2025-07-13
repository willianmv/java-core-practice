package com.example.management.core.usecase.task;

import com.example.management.core.domain.Task;
import com.example.management.core.dto.input.CreateTaskInput;

public interface CreateTaskUseCase {

    Task execute(CreateTaskInput createTaskInput);

}
