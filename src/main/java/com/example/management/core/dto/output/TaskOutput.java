package com.example.management.core.dto.output;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskOutput(
        Long id,
        String title,
        String description,
        LocalDate dueDate,
        boolean blocked,
        LocalDateTime createdAt) {}
