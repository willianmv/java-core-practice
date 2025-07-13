package com.example.management.core.usecase.board;

import com.example.management.core.domain.Board;
import com.example.management.core.domain.Column;
import com.example.management.core.enums.ColumnType;
import com.example.management.core.exception.DuplicateTitleException;
import com.example.management.core.gateway.BoardGateway;
import com.example.management.core.gateway.ColumnGateway;

public class CreateBoardUseCaseImpl implements CreateBoardUseCase{

    private final BoardGateway boardGateway;
    private final ColumnGateway columnGateway;

    public CreateBoardUseCaseImpl(BoardGateway boardGateway, ColumnGateway columnGateway) {
        this.boardGateway = boardGateway;
        this.columnGateway = columnGateway;
    }

    @Override
    public Board execute(String title) {
        validateDuplicateTitle(title);
        Board board = new Board(null, title);
        Board createdBoard = boardGateway.save(board);
        createColumnsForBoard(createdBoard);
        return createdBoard;
    }

    private void validateDuplicateTitle(String title){
        if(boardGateway.existsByTitle(title))
            throw new DuplicateTitleException("Title: \""+ title +"\" already in use.");
    }

    private void createColumnsForBoard(Board board){
        for(ColumnType type : ColumnType.values()){
            Column column = new Column(null, board, type);
            columnGateway.save(column);
        }
    }
}
