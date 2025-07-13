package com.example.management.core.usecase.task;

import com.example.management.core.domain.Board;
import com.example.management.core.domain.Column;
import com.example.management.core.domain.Task;
import com.example.management.core.enums.ColumnType;
import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.core.gateway.TaskGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Block task use case")
@ExtendWith(MockitoExtension.class)
class BlockTaskUseCaseImplTest {

    @Mock
    private TaskGateway taskGateway;

    @InjectMocks
    private BlockTaskUseCaseImpl blockTaskUseCase;

    @Test
    @DisplayName("Should block task with success")
    void shouldBlockTaskWithSuccess(){
        //Arrange
        Board board = new Board(1L, "board");
        Column column = new Column(3L, board, ColumnType.TO_DO);
        long taskId = 3L;
        Task existingTask = new Task(taskId, "title", "Description", LocalDate.of(2025, 12, 12), column);

       when(taskGateway.findById(taskId)).thenReturn(existingTask);
       when(taskGateway.save(any(Task.class))).thenReturn(existingTask);

       //Act
        blockTaskUseCase.execute(taskId);

        //Assert
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);

        verify(taskGateway, times(1)).findById(taskId);
        verify(taskGateway, times(1)).save(taskArgumentCaptor.capture());
        assertTrue(taskArgumentCaptor.getValue().isBlocked());
    }

    @Test
    @DisplayName("Should not change task already blocked")
    void shouldNotChangeTaskAlreadyBlocked(){
        //Arrange
        Board board = new Board(1L, "board");
        Column column = new Column(3L, board, ColumnType.TO_DO);
        long taskId = 3L;
        Task existingTask = new Task(taskId, "title", "Description", LocalDate.of(2025, 12, 12), column);
        existingTask.block();

        when(taskGateway.findById(taskId)).thenReturn(existingTask);

        //Act
        blockTaskUseCase.execute(taskId);

        //Assert
        assertTrue(existingTask.isBlocked());
        verify(taskGateway, times(1)).findById(taskId);
        verify(taskGateway, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when task does not exist by ID")
    void shouldThrowExceptionWhenTaskDoesNotExistById(){
        //Arrange
        long taskId = 999L;

        when(taskGateway.findById(taskId)).thenThrow(new EntityNotFoundException("Entity not found with ID: "+taskId));

        //Act & assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> blockTaskUseCase.execute(taskId));
        assertEquals("Entity not found with ID: "+taskId, ex.getMessage());

        verify(taskGateway, times(1)).findById(taskId);
        verify(taskGateway, never()).save(any(Task.class));
    }
}