package com.example.management.core.domain;

import com.example.management.core.exception.BlockedTaskException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {

    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Boolean blocked;
    private LocalDateTime createdAt;
    private Column column;

    public Task() {
        this.createdAt = LocalDateTime.now();
        this.blocked = false;
    }

    public Task(Long id, String title, String description, LocalDate dueDate, Column column) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.blocked = false;
        this.createdAt = LocalDateTime.now();
        this.column = column;
    }

    public void block() {
        if(!blocked){
            this.blocked = true;
        }
    }

    public void unblock() {
        if(blocked){
            this.blocked = false;
        }
    }

    public void move(Column toColumn){
        if(blocked) throw new BlockedTaskException("Task blocked! Unlock it to be able to move.");
        this.column = toColumn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }
}
