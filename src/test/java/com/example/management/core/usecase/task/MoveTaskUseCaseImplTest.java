package com.example.management.core.usecase.task;

import com.example.management.core.domain.Board;
import com.example.management.core.domain.Column;
import com.example.management.core.domain.Task;
import com.example.management.core.dto.input.MoveTaskInput;
import com.example.management.core.enums.ColumnType;
import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.core.gateway.ColumnGateway;
import com.example.management.core.gateway.TaskGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoveTaskUseCaseImplTest {

    @Mock
    private TaskGateway taskGateway;

    @Mock
    private ColumnGateway columnGateway;

    @InjectMocks
    private MoveTaskUseCaseImpl moveTaskUseCase;

    @Test
    @DisplayName("Should move task with success")
    void shouldMoveTaskWithSuccess(){
        // Arrange
        long taskId = 3L;
        long columnId = 5L;

        Board board = new Board(1L, "board");
        Column fromColumn = new Column(2L, board, ColumnType.TO_DO);
        Column toColumn = new Column(columnId, board, ColumnType.IN_PROGRESS);
        Task existingTask = new Task(taskId, "Task title", "Task description", LocalDate.of(2025, 12, 12), fromColumn);

        when(taskGateway.findById(taskId)).thenReturn(existingTask);
        when(columnGateway.findById(columnId)).thenReturn(toColumn);
        when(taskGateway.save(existingTask)).thenReturn(existingTask);

        MoveTaskInput moveTaskInput = new MoveTaskInput(taskId, columnId);

        //Act
        moveTaskUseCase.execute(moveTaskInput);

        //Assert
        assertEquals(toColumn, existingTask.getColumn());

        verify(taskGateway, times(1)).findById(moveTaskInput.taskId());
        verify(columnGateway, times(1)).findById(moveTaskInput.columnId());
        verify(taskGateway, times(1)).save(existingTask);
    }


    @Test
    @DisplayName("Should throw exception when task does not exist")
    void shouldThrowExceptionWhenTaskDoesNotExist() {
        long taskId = 999L;
        long columnId = 5L;

        when(taskGateway.findById(taskId)).thenThrow( new EntityNotFoundException("Entity not found with ID: "+taskId));
        MoveTaskInput moveTaskInput = new MoveTaskInput(taskId, columnId);

        // Act & Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> moveTaskUseCase.execute(moveTaskInput));
        assertEquals("Entity not found with ID: "+taskId, ex.getMessage());

        verify(taskGateway, times(1)).findById(taskId);
        verify(columnGateway, never()).findById(columnId);
        verify(taskGateway, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when moving task to the same column")
    void shouldThrowExceptionWhenMovingToSameColumn() {
        // Arrange
        long taskId = 3L;
        long columnId = 2L;

        Board board = new Board(1L, "board");
        Column column = new Column(columnId, board, ColumnType.TO_DO);
        Task existingTask = new Task(taskId, "Task title", "Task description", LocalDate.of(2025, 12, 12), column);

        when(taskGateway.findById(taskId)).thenReturn(existingTask);
        when(columnGateway.findById(columnId)).thenReturn(column);

        MoveTaskInput moveTaskInput = new MoveTaskInput(taskId, columnId);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> moveTaskUseCase.execute(moveTaskInput));
        assertEquals("Task is already in the destination column", ex.getMessage());

        verify(taskGateway, times(1)).findById(taskId);
        verify(columnGateway, times(1)).findById(columnId);
        verify(taskGateway, never()).save(any(Task.class)); // Não deve salvar, pois houve erro
    }
}