package com.example.management.core.usecase.board;

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

@DisplayName("Delete board use case")
@ExtendWith(MockitoExtension.class)
class DeleteBoardUseCaseImplTest {

    @Mock
    private BoardGateway boardGateway;

    @InjectMocks
    private DeleteBoardUseCaseImpl deleteBoardUseCase;

    @Test
    @DisplayName("Should delete board with success")
    void shouldDeleteBoardWithSuccess(){
        //Arrange
        long boardId = 7L;
        when(boardGateway.existsById(boardId)).thenReturn(true);
        doNothing().when(boardGateway).deleteById(boardId);

        //Act
        deleteBoardUseCase.execute(boardId);

        //Assert / Verify
        verify(boardGateway, times(1)).existsById(boardId);
        verify(boardGateway, times(1)).deleteById(boardId);
    }

    @Test
    @DisplayName("Should throw exception when board does not exist by ID")
    void shouldThrowExceptionWhenBoardDoesNotExistById(){
        //Arrange
        long boardId = 999L;
        when(boardGateway.existsById(boardId)).thenReturn(false);

        //Act && Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> deleteBoardUseCase.execute(boardId));
        assertEquals("Board with ID " + boardId + " does not exist.", ex.getMessage());

        verify(boardGateway, times(1)).existsById(boardId);
        verify(boardGateway, never()).deleteById(boardId);
    }

}