package com.example.management.infrastructure.persistence.db;

import com.example.management.core.domain.Board;
import com.example.management.core.domain.Column;
import com.example.management.core.domain.Task;
import com.example.management.core.enums.ColumnType;
import com.example.management.infrastructure.persistence.TaskRepository;
import com.example.management.infrastructure.persistence.db.connection.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcTaskRepository implements TaskRepository {

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            String sql = """
                INSERT INTO tb_task (title, description, due_date, blocked, created_at, column_id)
                VALUES (?, ?, ?, ?, ?, ?) RETURNING id
            """;
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, task.getTitle());
                stmt.setString(2, task.getDescription());
                stmt.setDate(3, Date.valueOf(task.getDueDate()));
                stmt.setBoolean(4, task.isBlocked());
                stmt.setTimestamp(5, Timestamp.valueOf(task.getCreatedAt()));
                stmt.setLong(6, task.getColumn().getId());

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    task.setId(rs.getLong("id"));
                }

            } catch (SQLException e) {
                System.out.println("Error saving task");
            }
        } else {
            String sql = """
                UPDATE tb_task SET title = ?, description = ?, due_date = ?, blocked = ?, column_id = ?
                WHERE id = ?
            """;
            try (Connection conn = ConnectionFactory.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, task.getTitle());
                stmt.setString(2, task.getDescription());
                stmt.setDate(3, Date.valueOf(task.getDueDate()));
                stmt.setBoolean(4, task.isBlocked()     );
                stmt.setLong(5, task.getColumn().getId());
                stmt.setLong(6, task.getId());
                stmt.executeUpdate();

            } catch (SQLException e) {
                System.out.println("Error updating task");
            }
        }
        return task;
    }

    @Override
    public Optional<Task> findById(long id) {
        String sql = """
            SELECT t.*, c.id as column_id, c.type, b.id as board_id, b.title as board_title, b.created_at as board_created
            FROM tb_task t
            JOIN tb_column c ON t.column_id = c.id
            JOIN tb_board b ON c.board_id = b.id
            WHERE t.id = ?
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapTask(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error finding task by id");
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(long id) {
        String sql = "SELECT 1 FROM tb_task WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeQuery().next();

        } catch (SQLException e) {
            System.out.println("Error checking if task exists by id");
        }
        return false;
    }

    @Override
    public boolean existsByTitleInBoard(String title, long boardId) {
        String sql = """
            SELECT 1
            FROM tb_task t
            JOIN tb_column c ON t.column_id = c.id
            WHERE t.title = ? AND c.board_id = ?
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setLong(2, boardId);
            return stmt.executeQuery().next();

        } catch (SQLException e) {
            System.out.println("Error checking task title in board");
        }
        return false;
    }

    @Override
    public boolean existsByTitleInBoardAndIdNot(String title, long boardId, long id) {
        String sql = """
            SELECT 1
            FROM tb_task t
            JOIN tb_column c ON t.column_id = c.id
            WHERE t.title = ? AND c.board_id = ? AND t.id <> ?
        """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setLong(2, boardId);
            stmt.setLong(3, id);
            return stmt.executeQuery().next();

        } catch (SQLException e) {
            System.out.println("Error checking task title in board excluding id");
        }
        return false;
    }

    @Override
    public List<Task> findAllByBoardId(long boardId) {
        String sql = """
            SELECT t.*, c.id as column_id, c.type, b.id as board_id, b.title as board_title, b.created_at as board_created
            FROM tb_task t
            JOIN tb_column c ON t.column_id = c.id
            JOIN tb_board b ON c.board_id = b.id
            WHERE b.id = ?
        """;
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, boardId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapTask(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error finding tasks by boardId");
        }
        return tasks;
    }

    @Override
    public List<Task> findAllByColumnId(long columnId) {
        String sql = """
            SELECT t.*, c.id as column_id, c.type, b.id as board_id, b.title as board_title, b.created_at as board_created
            FROM tb_task t
            JOIN tb_column c ON t.column_id = c.id
            JOIN tb_board b ON c.board_id = b.id
            WHERE c.id = ?
        """;
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, columnId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(mapTask(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error finding tasks by columnId");
        }
        return tasks;
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM tb_task WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error deleting task by id");
        }
    }

    private Task mapTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setDueDate(rs.getDate("due_date").toLocalDate());
        task.setBlocked(rs.getBoolean("blocked"));
        task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        Column column = new Column();
        column.setId(rs.getLong("column_id"));
        column.setType(ColumnType.valueOf(rs.getString("type")));

        Board board = new Board();
        board.setId(rs.getLong("board_id"));
        board.setTitle(rs.getString("board_title"));
        board.setCreatedAt(rs.getTimestamp("board_created").toLocalDateTime());

        column.setBoard(board);
        task.setColumn(column);

        return task;
    }
}
