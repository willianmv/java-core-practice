package com.example.management.core.dto.input;

import java.time.LocalDate;

public record CreateTaskInput(
        String title,
        String description,
        LocalDate dueDate,
        Long columnId) {}
