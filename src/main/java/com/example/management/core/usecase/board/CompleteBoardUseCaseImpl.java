package com.example.management.core.usecase.board;

import com.example.management.core.domain.Board;
import com.example.management.core.domain.Column;
import com.example.management.core.dto.output.ColumnOutput;
import com.example.management.core.dto.output.CompleteBoardOutput;
import com.example.management.core.dto.output.TaskOutput;
import com.example.management.core.gateway.BoardGateway;
import com.example.management.core.gateway.ColumnGateway;
import com.example.management.core.gateway.TaskGateway;

import java.util.List;
import java.util.Optional;

public class CompleteBoardUseCaseImpl implements CompleteBoardUseCase{

    private final BoardGateway boardGateway;
    private final ColumnGateway columnGateway;
    private final TaskGateway taskGateway;

    public CompleteBoardUseCaseImpl(BoardGateway boardGateway, ColumnGateway columnGateway, TaskGateway taskGateway) {
        this.boardGateway = boardGateway;
        this.columnGateway = columnGateway;
        this.taskGateway = taskGateway;
    }

    @Override
    public CompleteBoardOutput execute(long boardId) {
        Board board = boardGateway.findById(boardId);
        List<Column> columns = Optional.ofNullable(columnGateway.getAllByBoardId(boardId)).orElse(List.of());

        List<ColumnOutput> columnOutputList = columns.stream().map(col -> {
            var tasks = Optional.ofNullable(taskGateway.getAllByColumnId(col.getId())).orElse(List.of());
            var tasksOutput = tasks.stream().map(task -> new TaskOutput(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate(),
                    task.isBlocked(),
                    task.getCreatedAt()
            )).toList();
         return new ColumnOutput(col.getId(), col.getType(), tasksOutput);
        }).toList();

        return new CompleteBoardOutput(board.getId(),
                board.getTitle(),
                board.getCreatedAt(),
                columnOutputList);
    }
}
