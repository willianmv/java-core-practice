package com.example.management.infrastructure.dto;

import java.time.LocalDate;

public record TaskResponse(long id, String title, String description, LocalDate dueDate, boolean blocked, long columnId) {}
