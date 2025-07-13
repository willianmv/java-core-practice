package com.example.management.core.usecase.task;

import com.example.management.core.domain.Board;
import com.example.management.core.domain.Column;
import com.example.management.core.domain.Task;
import com.example.management.core.dto.input.UpdateTaskInput;
import com.example.management.core.enums.ColumnType;
import com.example.management.core.exception.DuplicateTitleException;
import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.core.exception.InvalidDueDateException;
import com.example.management.core.gateway.TaskGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Update task use case")
@ExtendWith(MockitoExtension.class)
class UpdateTaskUseCaseImplTest {

    @Mock
    private TaskGateway taskGateway;

    @InjectMocks
    private UpdateTaskUseCaseImpl updateTaskUseCase;

    @Test
    @DisplayName("Should update task with success")
    void shouldUpdateTaskWithSuccess(){
        //Arrange
        Board board = new Board(1L, "Board");
        Column column = new Column(1L, board, ColumnType.TO_DO);
        Task existingTask = new Task(1L, "First Task", "Desc", LocalDate.now().plusDays(1), column);

        long taskId = 1L;
        String title = "New Task";
        String description = "New Description";
        LocalDate dueDate = LocalDate.of(2025, 12, 12);

        UpdateTaskInput updateTaskInput = new UpdateTaskInput(taskId, title, description, dueDate);

        when(taskGateway.findById(taskId)).thenReturn(existingTask);
        when(taskGateway.existsByTitleInBoardAndIdNot(title, board.getId(), taskId)).thenReturn(false);
        when(taskGateway.save(any(Task.class))).thenReturn(existingTask);

        //Act
        Task result = updateTaskUseCase.execute(updateTaskInput);

        //Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
        assertEquals(LocalDate.of(2025, 12, 12), result.getDueDate());

        verify(taskGateway, times(1)).findById(taskId);
        verify(taskGateway, times(1)).existsByTitleInBoardAndIdNot(title, board.getId(), taskId);
        verify(taskGateway, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should allow update tasks without changing title")
    void shouldAllowUpdateSameTitle(){
        //Arrange
        Board board = new Board(1L, "Board");
        Column column = new Column(1L, board, ColumnType.TO_DO);
        Task existingTask = new Task(1L, "Same Title", "Desc", LocalDate.now().plusDays(1), column);

        long taskId = 1L;
        String title = "Same Title";
        String description = "New Description";
        LocalDate dueDate = LocalDate.of(2025, 12, 12);

        UpdateTaskInput updateTaskInput = new UpdateTaskInput(taskId, title, description, dueDate);

        when(taskGateway.findById(taskId)).thenReturn(existingTask);
        when(taskGateway.existsByTitleInBoardAndIdNot(title, board.getId(), taskId)).thenReturn(false);
        when(taskGateway.save(any(Task.class))).thenReturn(existingTask);

        //Act
        Task result = updateTaskUseCase.execute(updateTaskInput);

        //Assert
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
        assertEquals(LocalDate.of(2025, 12, 12), result.getDueDate());

        verify(taskGateway, times(1)).findById(taskId);
        verify(taskGateway, times(1)).existsByTitleInBoardAndIdNot(title, board.getId(), taskId);
        verify(taskGateway, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when task does not exist by ID")
    void shouldThrowExceptionWhenTaskDoesNotExistById(){
        //Arrange
        long taskId = 999L;
        String title = "New Task";
        String description = "New Description";
        LocalDate dueDate = LocalDate.of(2025, 12, 12);

        UpdateTaskInput updateTaskInput = new UpdateTaskInput(taskId, title, description, dueDate);

        when(taskGateway.findById(taskId)).thenThrow(new EntityNotFoundException("Entity not found with ID: "+taskId));

        //Act & Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> updateTaskUseCase.execute(updateTaskInput));
        assertEquals("Entity not found with ID: "+taskId, ex.getMessage());

        verify(taskGateway, times(1)).findById(taskId);
        verify(taskGateway, never()).existsByTitleInBoardAndIdNot(anyString(), anyLong(), anyLong());
        verify(taskGateway, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when due date is past")
    void shouldThrowExceptionWhenDueDateIsPast(){
        //Arrange
        Board board = new Board(1L, "Board");
        Column column = new Column(1L, board, ColumnType.TO_DO);
        Task existingTask = new Task(1L, "First Task", "Desc", LocalDate.now().plusDays(1), column);

        long taskId = 1L;
        String title = "New Task";
        String description = "New Description";
        LocalDate dueDate = LocalDate.now().minusDays(1);

        UpdateTaskInput updateTaskInput = new UpdateTaskInput(taskId, title, description, dueDate);

        when(taskGateway.findById(taskId)).thenReturn(existingTask);

        //Act & Assert
        InvalidDueDateException ex = assertThrows(InvalidDueDateException.class, () -> updateTaskUseCase.execute(updateTaskInput));
        assertEquals("Due date cannot be in the past.", ex.getMessage());

        verify(taskGateway, times(1)).findById(taskId);
        verify(taskGateway, never()).existsByTitleInBoardAndIdNot(anyString(), anyLong(), anyLong());
        verify(taskGateway, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when title already exists")
    void shouldThrowExceptionWhenTitleAlreadyExists(){
        //Arrange
        Board board = new Board(1L, "Board");
        Column column = new Column(1L, board, ColumnType.TO_DO);
        Task existingTask = new Task(1L, "First Task", "Desc", LocalDate.now().plusDays(1), column);

        long taskId = 1L;
        String title = "New Task";
        String description = "New Description";
        LocalDate dueDate = LocalDate.of(2025, 12, 12);

        UpdateTaskInput updateTaskInput = new UpdateTaskInput(taskId, title, description, dueDate);

        when(taskGateway.findById(taskId)).thenReturn(existingTask);

        when(taskGateway.existsByTitleInBoardAndIdNot(title, board.getId(), taskId))
                .thenThrow(new DuplicateTitleException("Title: \""+ title +"\" already in use."));

        //Act & Assert
        DuplicateTitleException ex = assertThrows(DuplicateTitleException.class, () -> updateTaskUseCase.execute(updateTaskInput));
        assertEquals("Title: \""+ title +"\" already in use.", ex.getMessage());

        verify(taskGateway, times(1)).findById(taskId);
        verify(taskGateway, times(1)).existsByTitleInBoardAndIdNot(title, board.getId(), taskId);
        verify(taskGateway, never()).save(any(Task.class));
    }
}