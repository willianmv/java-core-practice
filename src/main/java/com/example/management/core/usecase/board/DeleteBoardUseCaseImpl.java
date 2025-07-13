package com.example.management.core.usecase.board;

import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.core.gateway.BoardGateway;

public class DeleteBoardUseCaseImpl implements DeleteBoardUseCase{

    private final BoardGateway boardGateway;

    public DeleteBoardUseCaseImpl(BoardGateway boardGateway) {
        this.boardGateway = boardGateway;
    }

    @Override
    public void execute(long id) {
        boolean exists = boardGateway.existsById(id);
        if(!exists) throw new EntityNotFoundException("Board with ID " + id + " does not exist.");
        boardGateway.deleteById(id);
    }
}
