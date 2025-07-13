package com.example.management.core.usecase.board;

import com.example.management.core.domain.Board;
import com.example.management.core.dto.input.UpdateBoardInput;
import com.example.management.core.exception.DuplicateTitleException;
import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.core.gateway.BoardGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Update board use case")
@ExtendWith(MockitoExtension.class)
class UpdateBoardUseCaseImplTest {

    @Mock
    private BoardGateway boardGateway;

    @InjectMocks
    private UpdateBoardUseCaseImpl updateBoardUseCase;

    @Test
    @DisplayName("Should update board with success")
    void shouldUpdateBoardWithSuccess(){
        //Arrange
        long boardId = 3L;
        Board existingBoard = new Board(boardId, "existing board");
        when(boardGateway.findById(boardId)).thenReturn(existingBoard);

        UpdateBoardInput updateBoardInput = new UpdateBoardInput(boardId, "New title");
        when(boardGateway.existsByTitleAndIdNot(updateBoardInput.newTitle(), updateBoardInput.id())).thenReturn(false);

        when(boardGateway.save(existingBoard)).thenReturn(existingBoard);

        //Act
        Board result = updateBoardUseCase.execute(updateBoardInput);

        //Assert
        assertNotNull(result);
        assertEquals(boardId, result.getId());
        assertEquals("New title", result.getTitle());

        verify(boardGateway, times(1)).findById(boardId);
        verify(boardGateway, times(1)).existsByTitleAndIdNot(updateBoardInput.newTitle(), updateBoardInput.id());
        verify(boardGateway, times(1)).save(existingBoard);
    }

    @Test
    @DisplayName("Should allow update boards without changing title")
    void shouldAllowUpdateSameTitle(){
        //Arrange
        long boardId = 3L;
        String title = "same title";
        Board existingBoard = new Board(boardId, title);
        when(boardGateway.findById(boardId)).thenReturn(existingBoard);

        UpdateBoardInput updateBoardInput = new UpdateBoardInput(boardId, title);
        when(boardGateway.existsByTitleAndIdNot(updateBoardInput.newTitle(), updateBoardInput.id())).thenReturn(false);

        when(boardGateway.save(existingBoard)).thenReturn(existingBoard);

        //Act
        Board result = updateBoardUseCase.execute(updateBoardInput);

        //Assert
        assertNotNull(result);
        assertEquals(boardId, result.getId());
        assertEquals(title, result.getTitle());

        verify(boardGateway, times(1)).findById(boardId);
        verify(boardGateway, times(1)).existsByTitleAndIdNot(updateBoardInput.newTitle(), updateBoardInput.id());
        verify(boardGateway, times(1)).save(existingBoard);
    }

    @Test
    @DisplayName("Should throw exception when board does not exist by id")
    void shouldThrowExceptionWhenBoardDoesNotExistById(){
        //Arrange
        long boardId = 999L;
        when(boardGateway.findById(boardId)).thenThrow(
                new EntityNotFoundException("Entity not found by ID: \""+boardId+"\""));

        //Act & Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> updateBoardUseCase.execute(new UpdateBoardInput(boardId, "no id teste")));

        assertEquals("Entity not found by ID: \""+boardId+"\"", ex.getMessage());

        verify(boardGateway, times(1)).findById(boardId);
        verify(boardGateway, never()).existsByTitleAndIdNot(anyString(), anyLong());
        verify(boardGateway, never()).save(any(Board.class));
    }

    @Test
    @DisplayName("Should throw exception when title already exists")
    void shouldThrowExceptionWhenTitleValidationFails(){
        //Arrange
        long boardId = 3L;
        Board existingBoard = new Board(boardId, "existing board");
        when(boardGateway.findById(boardId)).thenReturn(existingBoard);

        UpdateBoardInput updateBoardInput = new UpdateBoardInput(boardId, "New title");
        when(boardGateway.existsByTitleAndIdNot(updateBoardInput.newTitle(), updateBoardInput.id())).thenReturn(true);

        //Act & Assert
        DuplicateTitleException ex = assertThrows(DuplicateTitleException.class, () -> updateBoardUseCase.execute(updateBoardInput));

        assertEquals("Title: \""+ updateBoardInput.newTitle() +"\" already in use.", ex.getMessage());

        verify(boardGateway, times(1)).findById(boardId);
        verify(boardGateway, times(1)).existsByTitleAndIdNot(updateBoardInput.newTitle(), updateBoardInput.id());
        verify(boardGateway, never()).save(any(Board.class));
    }

}