package com.example.management.core.usecase.task;

import com.example.management.core.domain.Board;
import com.example.management.core.domain.Column;
import com.example.management.core.domain.Task;
import com.example.management.core.dto.input.CreateTaskInput;
import com.example.management.core.enums.ColumnType;
import com.example.management.core.exception.DuplicateTitleException;
import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.core.exception.InvalidDueDateException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Create task use case")
@ExtendWith(MockitoExtension.class)
class CreateTaskUseCaseImplTest {

    @Mock
    private TaskGateway taskGateway;

    @Mock
    private ColumnGateway columnGateway;

    @InjectMocks
    private CreateTaskUseCaseImpl createTaskUseCase;

    @Test
    @DisplayName("Should create task with success")
    void shouldCreateTaskWithSuccess(){
        //Arrange
        Board board = new Board(1L, "board");
        Column column = new Column(3L, board, ColumnType.TO_DO);

        String title = "New Task";
        String description = "New TaskDescription";
        LocalDate dueDate = LocalDate.now().plusDays(5);
        long columnId = 3L;

        CreateTaskInput createTaskInput = new CreateTaskInput(title, description, dueDate, columnId);

        when(columnGateway.findById(createTaskInput.columnId())).thenReturn(column);

        when(taskGateway.existsByTitleInBoard(createTaskInput.title(), board.getId())).thenReturn(false);

        when(taskGateway.save(any(Task.class))).thenAnswer(invocationOnMock -> {
            Task t = invocationOnMock.getArgument(0);
            return new Task(10L, t.getTitle(), t.getDescription(), t.getDueDate(), t.getColumn());
        });

        //Act
        Task result = createTaskUseCase.execute(createTaskInput);

        //Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
        assertEquals(dueDate, result.getDueDate());
        assertEquals(column, result.getColumn());
        assertEquals(board, result.getColumn().getBoard());

        verify(columnGateway, times(1)).findById(columnId);
        verify(taskGateway, times(1)).existsByTitleInBoard(title, board.getId());
        verify(taskGateway, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when title already exists")
    void shouldThrowExceptionWhenTitleAlreadyExists(){
        //Arrange
        Board board = new Board(1L, "board");
        Column column = new Column(3L, board, ColumnType.TO_DO);

        String title = "Existing Task";
        String description = "New TaskDescription";
        LocalDate dueDate = LocalDate.now().plusDays(5);
        long columnId = 3L;

        CreateTaskInput createTaskInput = new CreateTaskInput(title, description, dueDate, columnId);

        when(columnGateway.findById(createTaskInput.columnId())).thenReturn(column);

        when(taskGateway.existsByTitleInBoard(title, board.getId())).thenReturn(true);

        //Act && Assert
        DuplicateTitleException ex = assertThrows(DuplicateTitleException.class, () -> createTaskUseCase.execute(createTaskInput));
        assertEquals("Title: \""+ title +"\" already in use.", ex.getMessage());

        verify(columnGateway, times(1)).findById(columnId);
        verify(taskGateway, times(1)).existsByTitleInBoard(title, board.getId());
        verify(taskGateway, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when due date is in the past")
    void shouldThrowExceptionWhenDueDateIsPast(){
        //Arrange
        Board board = new Board(1L, "board");
        Column column = new Column(3L, board, ColumnType.TO_DO);

        String title = "New Task";
        String description = "New TaskDescription";
        LocalDate dueDate = LocalDate.now().minusDays(1);
        long columnId = 3L;

        CreateTaskInput createTaskInput = new CreateTaskInput(title, description, dueDate, columnId);

        when(columnGateway.findById(createTaskInput.columnId())).thenReturn(column);

        //Act && Assert
        InvalidDueDateException ex = assertThrows(InvalidDueDateException.class, () -> createTaskUseCase.execute(createTaskInput));
        assertEquals("Due date cannot be in the past.", ex.getMessage());

        verify(columnGateway, times(1)).findById(columnId);
        verify(taskGateway, never()).existsByTitleInBoard(title, board.getId());
        verify(taskGateway, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when column does not exist by ID")
    void shouldThrowExceptionWhenColumnDoesNotExist(){
        //Arrange
        String title = "New Task";
        String description = "New TaskDescription";
        LocalDate dueDate = LocalDate.now().minusDays(1);
        long columnId = 3L;

        CreateTaskInput createTaskInput = new CreateTaskInput(title, description, dueDate, columnId);

        doThrow(new EntityNotFoundException("Entity not found by ID: \""+columnId+"\""))
                .when(columnGateway).findById(columnId);

        //Act && Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> createTaskUseCase.execute(createTaskInput));
        assertEquals("Entity not found by ID: \""+columnId+"\"", ex.getMessage());

        verify(columnGateway, times(1)).findById(columnId);
        verify(taskGateway, never()).existsByTitleInBoard(anyString(), anyLong());
        verify(taskGateway, never()).save(any(Task.class));
    }

}