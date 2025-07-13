package com.example.management.infrastructure.dto;

public record BoardResponse(long id, String title, int taskCount, int progress) {}
