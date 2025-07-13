package com.example.management.infrastructure.persistence.file;

import com.example.management.core.domain.Board;
import com.example.management.infrastructure.persistence.BoardRepository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InFileBoardRepository implements BoardRepository {

    private static final Path FILE_PATH = Paths.get("data", "boards.csv");
    private static final String HEADER = "ID;TITLE;CREATED_AT";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    private final List<EntityDeletionListener> listeners = new ArrayList<>();

    public InFileBoardRepository(){
        try{
            FileUtils.initFile(FILE_PATH, HEADER);

        } catch (IOException e) {
            System.out.println("Error trying to load file: "+FILE_PATH.getFileName());
        }
    }

    public void addListener(EntityDeletionListener listener){
        this.listeners.add(listener);
    }

    public Board save(Board board){
        try{
            List<String[]> rows = FileUtils.readAllRows(FILE_PATH);

            if(board.getId() == null){
                board.setId(FileUtils.getNextId(FILE_PATH));

            } else{
                rows = rows.stream()
                        .filter(row -> Long.parseLong(row[0]) != board.getId())
                        .collect(Collectors.toList());
            }

            String[] newRow = {
                    String.valueOf(board.getId()),
                    board.getTitle(),
                    board.getCreatedAt().format(dateTimeFormatter)
            };

            rows.add(newRow);

            try(BufferedWriter bw = Files.newBufferedWriter(FILE_PATH)){
                bw.write(HEADER + "\n");

                for(String[] row : rows){
                    bw.write(String.join(";", row) + "\n");
                }
            }

        } catch (IOException e){
            System.out.println("Error trying to save board in file: "+FILE_PATH.getFileName());
        }
        return board;
    }

    public boolean existsById(long id){
        try{
            return FileUtils.readAllRows(FILE_PATH).stream()
                    .anyMatch(row -> Long.parseLong(row[0]) == id );

        } catch (IOException e) {
            System.out.println("Error trying to find board by ID in file: "+ FILE_PATH.getFileName());
            return false;
        }
    }

    public boolean existsByTitle(String title){
        try{
            return FileUtils.readAllRows(FILE_PATH).stream()
                    .anyMatch(row -> row[1].equalsIgnoreCase(title));
        } catch (IOException e) {
            System.out.println("Error trying to find board by title in file: "+FILE_PATH.getFileName());
            return false;
        }
    }

    public boolean existsByTitleAndIdNot(String title, long id){
        try{
            return FileUtils.readAllRows(FILE_PATH).stream()
                    .anyMatch(row -> row[1].equalsIgnoreCase(title) && Long.parseLong(row[0]) != id);

        } catch (IOException e) {
            System.out.println("Error trying to find board by title and id in file: "+FILE_PATH.getFileName());
            return false;
        }
    }

    public List<Board> getAll(){
        try{
            return FileUtils.readAllRows(FILE_PATH).stream()
                    .sorted(Comparator.comparingLong(row -> Long.parseLong(row[0])))
                    .map(row -> new Board(Long.parseLong(row[0]), row[1]))
                    .toList();

        } catch (IOException e){
            System.out.println("Error trying to get all boards in file: "+FILE_PATH.getFileName());
            return List.of();
        }
    }

    public Optional<Board> findById(long id) {
        try{
            return FileUtils.readAllRows(FILE_PATH).stream()
                    .filter(row -> Long.parseLong(row[0]) == id )
                    .map(row -> new Board(Long.parseLong(row[0]), row[1]))
                    .findFirst();

        } catch (IOException e){
            System.out.println("Error trying to find board by title and id in file: "+FILE_PATH.getFileName());
            return Optional.empty();
        }
    }

    public void deleteById(long id){
        try{
            List<String[]> rows = FileUtils.readAllRows(FILE_PATH);

            listeners.forEach(l -> l.onEntityDeleted(id));

            List<String> updatedLines = rows.stream()
                    .filter(row -> Long.parseLong(row[0]) != id)
                    .map(row -> String.join(";", row))
                    .toList();

            try(BufferedWriter bw = Files.newBufferedWriter(FILE_PATH)){
                bw.write(HEADER + "\n");
                for(String line : updatedLines){
                    bw.write(line + "\n");
                }
            }

        }catch (IOException e){
            System.out.println("Error trying to find board by title and id in file: "+FILE_PATH.getFileName());
        }
    }
}
