package com.example.management.infrastructure.mapper;

import com.example.management.core.domain.Board;
import com.example.management.core.dto.output.CompleteBoardOutput;
import com.example.management.core.enums.ColumnType;
import com.example.management.core.usecase.board.CompleteBoardUseCase;
import com.example.management.infrastructure.dto.BoardResponse;

public class BoardMapper {

    private final CompleteBoardUseCase completeBoardUseCase;

    public BoardMapper(CompleteBoardUseCase completeBoardUseCase) {
        this.completeBoardUseCase = completeBoardUseCase;
    }

    public BoardResponse toDto(Board board) {
        CompleteBoardOutput completeBoard = completeBoardUseCase.execute(board.getId());

        int totalTasks = 0;
        int completedTasks = 0;

        for(var column : completeBoard.columns()){
            int taskCount = column.tasks() != null ? column.tasks().size() : 0;
            totalTasks += taskCount;

            if(column.type() != null && column.type() == ColumnType.DONE){
                completedTasks += taskCount;
            }
        }

        int progress = totalTasks > 0 ? (int) ((completedTasks * 100.0f) / totalTasks) : 0;

        return new BoardResponse(board.getId(), board.getTitle(), totalTasks, progress);
    }
}
