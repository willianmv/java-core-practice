package com.example.management.core.dto.input;

import java.time.LocalDate;

public record UpdateTaskInput(
        long id,
        String title,
        String description,
        LocalDate dueDate
) {}
