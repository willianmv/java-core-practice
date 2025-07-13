package com.example.management.core.usecase.board;

import com.example.management.core.domain.Board;

public interface CreateBoardUseCase {

    Board execute(String title);

}
