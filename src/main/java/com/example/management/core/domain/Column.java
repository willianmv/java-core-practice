package com.example.management.core.domain;

import com.example.management.core.enums.ColumnType;

    public class Column {

        private Long id;
        private Board board;
        private ColumnType type;

    public Column() {}

    public Column(Long id, Board board, ColumnType type) {
        this.id = id;
        this.board = board;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

}
