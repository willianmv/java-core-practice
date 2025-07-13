package com.example.management.infrastructure.gateway;

import com.example.management.core.domain.Board;
import com.example.management.core.gateway.BoardGateway;
import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.infrastructure.persistence.BoardRepository;

import java.util.List;

public class BoardRepositoryGateway implements BoardGateway {

    private final BoardRepository boardRepository;

    public BoardRepositoryGateway(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public Board save(Board board) {
        return boardRepository.save(board);
    }

    @Override
    public boolean existsById(long id) {
        return boardRepository.existsById(id);
    }

    @Override
    public boolean existsByTitle(String title) {
        return boardRepository.existsByTitle(title);
    }

    @Override
    public boolean existsByTitleAndIdNot(String title, long id) {
        return boardRepository.existsByTitleAndIdNot(title, id);
    }

    @Override
    public List<Board> getAll() {
        return boardRepository.getAll();
    }

    @Override
    public Board findById(long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found by ID: \""+id+"\""));
    }

    @Override
    public void deleteById(long id) {
        boardRepository.deleteById(id);
    }
}
