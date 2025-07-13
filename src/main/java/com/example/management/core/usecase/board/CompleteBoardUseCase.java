package com.example.management.core.usecase.board;

import com.example.management.core.dto.output.CompleteBoardOutput;

public interface CompleteBoardUseCase {

    CompleteBoardOutput execute(long boardId);

}
