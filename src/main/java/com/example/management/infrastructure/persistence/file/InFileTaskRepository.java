package com.example.management.infrastructure.persistence.file;

import com.example.management.core.domain.Column;
import com.example.management.core.domain.Task;
import com.example.management.infrastructure.persistence.TaskRepository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InFileTaskRepository implements TaskRepository, EntityDeletionListener{

    private static final Path FILE_PATH = Paths.get("data", "tasks.csv");
    private static final String HEADER = "ID;TITLE;DESCRIPTION;DUE_DATE;BLOCKED;CREATED_AT;COLUMN_ID";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;

    private final InFileColumnRepository inFileColumnRepository;

    public InFileTaskRepository(InFileColumnRepository inFileColumnRepository) {
        this.inFileColumnRepository =  inFileColumnRepository;
        try{
            FileUtils.initFile(FILE_PATH, HEADER);

        } catch (IOException e) {
            System.out.println("Error trying to load file: "+FILE_PATH.getFileName());
        }
    }

    public Task save(Task task){
        try{
            List<String[]> rows = FileUtils.readAllRows(FILE_PATH);

            if(task.getId() == null){
                task.setId(FileUtils.getNextId(FILE_PATH));

            } else{
                rows = rows.stream().filter(row -> Long.parseLong(row[0]) != task.getId())
                        .collect(Collectors.toList());
            }

            String[] newRow = {
                    String.valueOf(task.getId()),
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate().format(dateFormatter),
                    String.valueOf(task.isBlocked()),
                    task.getCreatedAt().format(dateTimeFormatter),
                    String.valueOf(task.getColumn().getId())
            };

            rows.add(newRow);

            try(BufferedWriter bw = Files.newBufferedWriter(FILE_PATH)){
                bw.write(HEADER + "\n");

                for (String[] row : rows){
                    bw.write(String.join(";", row) + "\n");
                }
            }

        } catch (IOException e){
            System.out.println("Error trying to save task in file: "+FILE_PATH.getFileName());
        }
        return task;
    }

    public Optional<Task> findById(long id){
        try{
            List<String[]> rows = FileUtils.readAllRows(FILE_PATH);

            return rows.stream().filter(row -> Long.parseLong(row[0]) == id)
                    .map(row -> {
                        Task task = new Task();
                        task.setId(Long.parseLong(row[0]));
                        task.setTitle(row[1]);
                        task.setDescription(row[2]);
                        task.setDueDate(LocalDate.parse(row[3]));
                        task.setBlocked(Boolean.valueOf(row[4]));
                        task.setCreatedAt(LocalDateTime.parse(row[5]));

                        Column column = inFileColumnRepository.findById(Long.parseLong(row[6])).get();
                        task.setColumn(column);

                        return task;})
                    .findFirst();

        } catch (IOException e){
            System.out.println("Error trying to find task by ID in file: "+FILE_PATH.getFileName());
        }
        return Optional.empty();
    }

    public List<Task> findAll(){
        try{
            List<String[]> rows = FileUtils.readAllRows(FILE_PATH);

            return rows.stream().map( row -> {
                Task task = new Task();
                task.setId(Long.parseLong(row[0]));
                task.setTitle(row[1]);
                task.setDescription(row[2]);
                task.setDueDate(LocalDate.parse(row[3]));
                task.setBlocked(Boolean.valueOf(row[4]));
                task.setCreatedAt(LocalDateTime.parse(row[5]));

                Column column = inFileColumnRepository.findById(Long.parseLong(row[6])).get();
                task.setColumn(column);

                return task;})
                .toList();

        } catch (IOException e){
            System.out.println("Error trying to find tasks in file: "+FILE_PATH.getFileName());
        }
        return List.of();
    }

    public List<Task> findAllByBoardId(long boardId){
        return findAll().stream().filter(task ->
                task.getColumn().getBoard().getId().equals(boardId)).toList();
    }

    public List<Task> findAllByColumnId(long columnId){
        return findAll().stream().filter(task ->
                task.getColumn().getId().equals(columnId)).toList();
    }

    public boolean existsById(long id){
        try{
            List<String[]> rows = FileUtils.readAllRows(FILE_PATH);

            return rows.stream().anyMatch(row -> Long.parseLong(row[0]) == id);

        } catch (IOException e){
            System.out.println("Error trying to find task by ID in file: "+FILE_PATH.getFileName());
        }
        return false;
    }

    public boolean existsByTitleInBoard(String title, long boardId){
        return findAllByBoardId(boardId).stream()
                .anyMatch(task -> task.getTitle().equalsIgnoreCase(title));
    }

    public boolean existsByTitleInBoardAndIdNot(String title, long boardId, long id){
        return findAllByBoardId(boardId).stream()
                .anyMatch(task -> task.getTitle().equalsIgnoreCase(title) && !task.getId().equals(id));
    }

    public void deleteById(long id) {
        try{
            List<String[]> rows = FileUtils.readAllRows(FILE_PATH);

            rows = rows.stream().filter(row -> Long.parseLong(row[0]) != id).toList();

            try(BufferedWriter bw = Files.newBufferedWriter(FILE_PATH)){
                bw.write(HEADER + "\n");

                for(String[] row : rows){
                    bw.write(String.join(";", row) + "\n");
                }
            }

        }catch (IOException e){
            System.out.println("Error trying to delete task in file: "+FILE_PATH.getFileName());
        }
    }

    @Override
    public void onEntityDeleted(long deletedEntityId) {
        System.out.println("FILE TASK REPOSITORY NOTIFICATION: BOARD ID["+ deletedEntityId +"] deleted... Deleting tasks related to it.");
        deleteAllByBoardId(deletedEntityId);
    }

    private void deleteAllByBoardId(long deletedColumnId){
        List<Task> tasks = findAllByBoardId(deletedColumnId);
        tasks.forEach(task -> deleteById(task.getId()));
    }
}
