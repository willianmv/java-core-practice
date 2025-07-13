package com.example.management.core.usecase.task;

import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.core.gateway.TaskGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Delete task use case")
@ExtendWith(MockitoExtension.class)
class DeleteTaskUseCaseImplTest {

    @Mock
    private TaskGateway taskGateway;

    @InjectMocks
    private DeleteTaskUseCaseImpl deleteTaskUseCase;

    @Test
    @DisplayName("Should delete task with success")
    void shouldDeleteTaskWithSuccess(){
        //Arrange
        long taskId = 5L;

        when(taskGateway.existsById(taskId)).thenReturn(true);
        doNothing().when(taskGateway).deleteById(taskId);

        //Act & Assert
        deleteTaskUseCase.execute(taskId);

        verify(taskGateway, times(1)).existsById(taskId);
        verify(taskGateway, times(1)).deleteById(taskId);
    }

    @Test
    @DisplayName("Should throw exception when task does not exist by ID")
    void shouldThrowExceptionWhenTaskDoesNotExistById(){
        //Arrange
        long taskId = 5L;

        when(taskGateway.existsById(taskId)).thenReturn(false);

        //Act & Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> deleteTaskUseCase.execute(taskId));
        assertEquals("Entity not found with ID: "+taskId, ex.getMessage());

        verify(taskGateway, times(1)).existsById(taskId);
        verify(taskGateway, never()).deleteById(anyLong());
    }
}
