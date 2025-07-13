package com.example.management.core.usecase.board;

import com.example.management.core.domain.Board;
import com.example.management.core.dto.input.UpdateBoardInput;
import com.example.management.core.exception.DuplicateTitleException;
import com.example.management.core.gateway.BoardGateway;

public class UpdateBoardUseCaseImpl implements UpdateBoardUseCase{

    private final BoardGateway boardGateway;

    public UpdateBoardUseCaseImpl(BoardGateway boardGateway) {
        this.boardGateway = boardGateway;
    }

    @Override
    public Board execute(UpdateBoardInput updateBoardInput) {
        Board existingBoard = boardGateway.findById(updateBoardInput.id());
        validateDuplicateTitle(updateBoardInput);
        existingBoard.setTitle(updateBoardInput.newTitle());
        return boardGateway.save(existingBoard);
    }

    private void validateDuplicateTitle(UpdateBoardInput board){
        if(boardGateway.existsByTitleAndIdNot(board.newTitle(), board.id()))
            throw new DuplicateTitleException("Title: \""+ board.newTitle() +"\" already in use.");
    }
}
