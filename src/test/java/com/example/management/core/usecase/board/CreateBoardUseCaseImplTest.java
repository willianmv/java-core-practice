package com.example.management.core.usecase.board;

import com.example.management.core.domain.Board;
import com.example.management.core.domain.Column;
import com.example.management.core.enums.ColumnType;
import com.example.management.core.exception.DuplicateTitleException;
import com.example.management.core.gateway.BoardGateway;
import com.example.management.core.gateway.ColumnGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Create board use case")
@ExtendWith(MockitoExtension.class)
class CreateBoardUseCaseImplTest {

    @Mock
    private BoardGateway boardGateway;

    @Mock
    private ColumnGateway columnGateway;

    @InjectMocks
    private CreateBoardUseCaseImpl createBoardUseCase;

    @Test
    @DisplayName("Should create board with columns")
    void shouldCreateBoardWithColumns(){
        //Arrange
        String title = "New board";
        Board savedBoard = new Board(1L, title);

        when(boardGateway.existsByTitle(title)).thenReturn(false);
        when(boardGateway.save(any(Board.class))).thenReturn(savedBoard);

        //Act
        Board result = createBoardUseCase.execute(title);

        //Assert
        assertNotNull(result);
        assertEquals(savedBoard.getId(), result.getId());
        assertEquals(savedBoard.getTitle(), result.getTitle());

        verify(boardGateway, times(1)).existsByTitle(title);
        verify(boardGateway, times(1))  .save(any(Board.class));
        verify(columnGateway, times(ColumnType.values().length)).save(any(Column.class));
    }

    @Test
    @DisplayName("Should create columns with the correct types")
    void shouldCreateColumnsWithCorrectType(){
        //Arrange
        String title = "Another board";
        Board savedBoard = new Board(1L, title);

        when(boardGateway.existsByTitle(title)).thenReturn(false);
        when(boardGateway.save(any(Board.class))).thenReturn(savedBoard);
        ArgumentCaptor<Column> columnArgumentCaptor = ArgumentCaptor.forClass(Column.class);

        //Act
        createBoardUseCase.execute(title);

        //Assert
        verify(columnGateway, times(ColumnType.values().length)).save(columnArgumentCaptor.capture());
        for(ColumnType type : ColumnType.values()){
            assertTrue(columnArgumentCaptor.getAllValues().stream()
                    .anyMatch(c -> c.getType() == type));
        }
    }

    @Test
    @DisplayName("Should throw exception if title already exists")
    void shouldThrowExceptionIfTitleAlreadyExists(){
        //Arrange
        String existingTitle = "existing title";

        when(boardGateway.existsByTitle(existingTitle)).thenReturn(true);

        //Act & Assert
        assertThrows(DuplicateTitleException.class, () -> createBoardUseCase.execute(existingTitle));

        verify(boardGateway, times(1)).existsByTitle(existingTitle);
        verify(boardGateway, never()).save(any());
        verify(columnGateway, never()).save(any());
    }



}