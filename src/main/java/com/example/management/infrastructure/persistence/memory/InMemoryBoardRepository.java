package com.example.management.infrastructure.persistence.memory;

import com.example.management.core.domain.Board;
import com.example.management.infrastructure.persistence.BoardRepository;

import java.util.*;

public class InMemoryBoardRepository implements BoardRepository {

    private final Map<Long, Board> storage = new HashMap<>();
    private long nextId = 1;

    @Override
    public Board save(Board board){
        if(board.getId() == null){
            board.setId(nextId++);
        }
        storage.put(board.getId(), board);
        return board;
    }

    @Override
    public boolean existsById(long id) {
        return storage.values().stream().
                anyMatch(b -> b.getId().equals(id));
    }

    @Override
    public boolean existsByTitle(String title){
        return storage.values().stream()
                .anyMatch(b -> b.getTitle().equalsIgnoreCase(title));
    }

    @Override
    public boolean existsByTitleAndIdNot(String title, long id) {
        return storage.values().stream()
                .anyMatch(b -> b.getTitle().equalsIgnoreCase(title) && !b.getId().equals(id));
    }

    @Override
    public List<Board> getAll(){
        return storage.values().stream().sorted(Comparator.comparingLong(Board::getId)).toList();
    }

    @Override
    public Optional<Board> findById(long id) {
        return storage.values().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
    }

    @Override
    public void deleteById(long id) {
        storage.remove(id);
    }
}
