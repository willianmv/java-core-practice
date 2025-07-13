package com.example.management.core.dto.output;

import com.example.management.core.enums.ColumnType;

import java.util.List;

public record ColumnOutput(
        Long id,
        ColumnType type,
        List<TaskOutput> tasks) {}
