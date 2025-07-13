package com.example.management.core.usecase.board;

import com.example.management.core.domain.Board;
import com.example.management.core.dto.input.UpdateBoardInput;

public interface UpdateBoardUseCase {

    Board execute(UpdateBoardInput updateBoardInput);

}
