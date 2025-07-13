package com.example.management.core.dto.output;

import java.time.LocalDateTime;
import java.util.List;

public record CompleteBoardOutput(
        Long id,
        String title,
        LocalDateTime createdAt,
        List<ColumnOutput> columns
) {}
