package com.example.management.infrastructure.persistence.file;

import com.example.management.core.domain.Board;
import com.example.management.core.domain.Column;
import com.example.management.core.enums.ColumnType;
import com.example.management.infrastructure.persistence.ColumnRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

public class InFileColumnRepository implements ColumnRepository, EntityDeletionListener {

    private static final Path FILE_PATH = Paths.get("data", "columns.csv");
    private static final String HEADER = "ID;BOARD_ID;TYPE";

    private final InFileBoardRepository inFileBoardRepository;

    public InFileColumnRepository(InFileBoardRepository inFileBoardRepository) {
        this.inFileBoardRepository = inFileBoardRepository;
        try {
            FileUtils.initFile(FILE_PATH, HEADER);

        } catch (IOException e) {
            System.out.println("Error trying to load file: " + FILE_PATH.getFileName());
        }
    }

    public void save(Column column) {
        try {
            if (column.getId() == null) {
                column.setId(FileUtils.getNextId(FILE_PATH));
            }

            String line = column.getId() + ";" + column.getBoard().getId() + ";" + column.getType().name();

            try (BufferedWriter bw = Files.newBufferedWriter(FILE_PATH, StandardOpenOption.APPEND)) {
                bw.write(line + "\n");
            }

        } catch (IOException e) {
            System.out.println("Error trying to save column in file: " + FILE_PATH.getFileName());
        }
    }

    public Optional<Column> findById(long id) {
        try {
            try (BufferedReader br = Files.newBufferedReader(FILE_PATH)) {
                br.readLine();
                String line;

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(";");

                    if (Long.parseLong(parts[0]) == id) {
                        Board board = inFileBoardRepository.findById(Long.parseLong(parts[1])).get();
                        Column column = new Column(Long.parseLong(parts[0]), board, ColumnType.valueOf(parts[2]));
                        return Optional.of(column);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error trying to find column by ID in file: " + FILE_PATH.getFileName());
        }
        return Optional.empty();
    }

    public List<Column> findAllByBoardId(long boardId) {
        try {
            List<String[]> rows = FileUtils.readAllRows(FILE_PATH);
            return rows.stream()
                    .filter(row -> Long.parseLong(row[1]) == boardId)
                    .map(row -> {
                        Board board = inFileBoardRepository.findById(Long.parseLong(row[1])).get();
                        return new Column(Long.parseLong(row[0]), board, ColumnType.valueOf(row[2]));
                    })
                    .toList();

        } catch (IOException e) {
            System.out.println("Error trying to find columns by board ID in file: " + FILE_PATH.getFileName());
        }
        return List.of();
    }

    @Override
    public void onEntityDeleted(long deletedEntityId) {
        System.out.println("FILE COLUMN REPOSITORY NOTIFICATION: Board ID["+ deletedEntityId +"] deleted... Deleting columns related to it.");
        deleteColumnsByBoardId(deletedEntityId);
    }

    private void deleteColumnsByBoardId(long deletedEntityId) {
        try {
            List<String[]> rows = FileUtils.readAllRows(FILE_PATH);

            List<String> updatedLines = rows.stream()
                    .filter(row -> Long.parseLong(row[1]) != deletedEntityId)
                    .map(row -> String.join(";", row))
                    .toList();

            try(BufferedWriter bw = Files.newBufferedWriter(FILE_PATH)){
                bw.write(HEADER + "\n");
                for (String line : updatedLines) {
                    bw.write(line + "\n");
                }
            }

        } catch (IOException e) {
            System.out.println("Error trying to delete columns by board ID in file: " + FILE_PATH.getFileName());
        }
    }
}
