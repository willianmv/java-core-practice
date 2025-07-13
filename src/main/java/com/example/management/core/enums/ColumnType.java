package com.example.management.core.enums;

public enum ColumnType {

    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    DONE("Done"),
    PAUSED("Paused");

    private final String title;

    ColumnType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
