package com.example.management.core.usecase.board;

import com.example.management.core.domain.Board;
import com.example.management.core.domain.Column;
import com.example.management.core.domain.Task;
import com.example.management.core.dto.output.ColumnOutput;
import com.example.management.core.dto.output.CompleteBoardOutput;
import com.example.management.core.enums.ColumnType;
import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.core.gateway.BoardGateway;
import com.example.management.core.gateway.ColumnGateway;
import com.example.management.core.gateway.TaskGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Return complete board use case")
@ExtendWith(MockitoExtension.class)
class CompleteBoardUseCaseImplTest {

    @Mock
    private BoardGateway boardGateway;

    @Mock
    private ColumnGateway columnGateway;

    @Mock
    private TaskGateway taskGateway;

    @InjectMocks
    private CompleteBoardUseCaseImpl completeBoardUseCase;

    @Test
    @DisplayName("Should return complete board output")
    void shouldReturnCompleteBoardOutput(){
        //Arrange
        long boardId = 1L;
        var board = new Board(boardId, "My board");
        board.setCreatedAt(LocalDateTime.of(2023, 1, 1, 10, 0));

        var column1 = new Column(10L, board, ColumnType.TO_DO);
        var column2 = new Column(11L, board, ColumnType.DONE);

        var task1 = new Task(100L, "Task A", "Desc A",
                LocalDate.of(2023, 1, 1), column1);

        task1.setCreatedAt(LocalDateTime.of(2023, 1, 2, 12, 0));

        var task2 = new Task(101L, "Task B", "Desc B",
                LocalDate.of(2023, 2, 1), column2);

        task2.block();
        task2.setCreatedAt(LocalDateTime.of(2023, 1, 3, 14, 0));

        when(boardGateway.findById(boardId)).thenReturn(board);
        when(columnGateway.getAllByBoardId(boardId)).thenReturn(List.of(column1, column2));
        when(taskGateway.getAllByColumnId(column1.getId())).thenReturn(List.of(task1));
        when(taskGateway.getAllByColumnId(column2.getId())).thenReturn(List.of(task2));

        //Act
        CompleteBoardOutput result = completeBoardUseCase.execute(boardId);

        //Assert
        assertNotNull(result);
        assertEquals(boardId, result.id());
        assertEquals(board.getTitle(), result.title());

        var firstCol = result.columns().get(0);
        assertEquals(ColumnType.TO_DO, firstCol.type());
        assertEquals(column1.getId(), firstCol.id());
        assertEquals(1, firstCol.tasks().size());
        assertEquals("Task A", firstCol.tasks().get(0).title());

        var secondCol = result.columns().get(1);
        assertEquals(ColumnType.DONE, secondCol.type());
        assertEquals(column2.getId(), secondCol.id());
        assertEquals(1, secondCol.tasks().size());
        assertTrue(secondCol.tasks().get(0).blocked());

        verify(boardGateway, times(1)).findById(boardId);
        verify(columnGateway, times(1)).getAllByBoardId(boardId);
        verify(taskGateway, times(2)).getAllByColumnId(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when board does not exist by id")
    void shouldThrowExceptionWhenBoardDoesNotExistsById(){
        //Arrange
        long boardId = 999L;

        when(boardGateway.findById(boardId)).thenThrow(
                new EntityNotFoundException("Entity not found by ID: \""+boardId+"\""));

        //Act & Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> completeBoardUseCase.execute(boardId));
        assertTrue(ex.getMessage().contains("Entity not found by ID"));

        verify(boardGateway, times(1)).findById(boardId);
        verify(columnGateway, never()).getAllByBoardId(boardId);
        verify(taskGateway, never()).getAllByColumnId(anyLong());

    }

    @Test
    @DisplayName("Should return board with empty columns list when there's no columns present")
    void shouldReturnBoardWithNoColumns(){
        //Arrange
        long boardId = 1L;
        var board = new Board(boardId, "My board");
        board.setCreatedAt(LocalDateTime.of(2023, 1, 1, 10, 0));

        when(boardGateway.findById(boardId)).thenReturn(board);
        when(columnGateway.getAllByBoardId(boardId)).thenReturn(List.of());

        //Act
        CompleteBoardOutput result = completeBoardUseCase.execute(boardId);

        //Assert
        assertNotNull(result);
        assertEquals(boardId, result.id());
        assertTrue(result.columns().isEmpty());

        verify(boardGateway, times(1)).findById(boardId);
        verify(columnGateway, times(1)).getAllByBoardId(boardId);
        verify(taskGateway, never()).getAllByColumnId(anyLong());

    }

    @Test
    @DisplayName("Should return board with columns and empty tasks when there's no tasks present")
    void shouldReturnBoardWithNoTasks(){
        //Arrange
        long boardId = 1L;
        var board = new Board(boardId, "My board");
        board.setCreatedAt(LocalDateTime.of(2023, 1, 1, 10, 0));

        var column1 = new Column(10L, board, ColumnType.TO_DO);
        var column2 = new Column(11L, board, ColumnType.IN_PROGRESS);
        var column3 = new Column(12L, board, ColumnType.DONE);
        var column4 = new Column(13L, board, ColumnType.PAUSED);

        when(boardGateway.findById(boardId)).thenReturn(board);
        when(columnGateway.getAllByBoardId(boardId)).thenReturn(List.of(column1, column2, column3, column4));
        when(taskGateway.getAllByColumnId(anyLong())).thenReturn(List.of());

        //Act
        CompleteBoardOutput result = completeBoardUseCase.execute(boardId);

        //Assert
        assertNotNull(result);
        assertEquals(boardId, result.id());
        assertEquals(4, result.columns().size());
        assertEquals(ColumnType.TO_DO, result.columns().get(0).type());
        assertEquals(ColumnType.IN_PROGRESS, result.columns().get(1).type());
        assertEquals(ColumnType.DONE, result.columns().get(2).type());
        assertEquals(ColumnType.PAUSED, result.columns().get(3).type());

        for (ColumnOutput col : result.columns()){
            assertTrue(col.tasks().isEmpty());
        }

        verify(boardGateway, times(1)).findById(boardId);
        verify(columnGateway, times(1)).getAllByBoardId(boardId);
        verify(taskGateway, times(4)).getAllByColumnId(anyLong());


    }

}