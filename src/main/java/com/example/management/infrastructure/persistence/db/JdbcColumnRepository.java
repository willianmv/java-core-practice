package com.example.management.infrastructure.persistence.db;

import com.example.management.core.domain.Board;
import com.example.management.core.domain.Column;
import com.example.management.core.enums.ColumnType;
import com.example.management.infrastructure.persistence.ColumnRepository;
import com.example.management.infrastructure.persistence.db.connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcColumnRepository implements ColumnRepository {

    @Override
    public void save(Column column) {
        String sql = "INSERT INTO tb_column (board_id, type) VALUES (?, ?::column_type)";
        try(Connection conn = ConnectionFactory.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setLong(1, column.getBoard().getId());
            preparedStatement.setString(2, column.getType().name());
            preparedStatement.executeUpdate();

        }catch (SQLException e){
            System.out.println("Error saving column ");
        }

    }

    @Override
    public Optional<Column> findById(long id) {
        String sql = """
        SELECT c.id as column_id, c.type, c.board_id, b.title, b.created_at
        FROM tb_column c
        JOIN tb_board b ON c.board_id = b.id
        WHERE c.id = ?
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                Column column = new Column();
                column.setId(rs.getLong("column_id"));
                column.setType(ColumnType.valueOf(rs.getString("type")));

                Board board = new Board();
                board.setId(rs.getLong("board_id"));
                board.setTitle(rs.getString("title"));
                board.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                column.setBoard(board);

                return Optional.of(column);
            }

        } catch (SQLException e) {
            System.out.println("Error trying to find column by id");
        }
        return Optional.empty();
    }

    @Override
    public List<Column> findAllByBoardId(long boardId) {
        String sql = """
        SELECT c.id as column_id, c.type, b.id as board_id, b.title, b.created_at
        FROM tb_column c
        JOIN tb_board b ON c.board_id = b.id
        WHERE c.board_id = ?
        """;

        List<Column> columns = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, boardId);
            var rs = stmt.executeQuery();

            while (rs.next()) {
                Column column = new Column();
                column.setId(rs.getLong("column_id"));
                column.setType(ColumnType.valueOf(rs.getString("type")));

                Board board = new Board();
                board.setId(rs.getLong("board_id"));
                board.setTitle(rs.getString("title"));
                board.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                column.setBoard(board);

                columns.add(column);
            }

        } catch (SQLException e) {
            System.out.println("Error getting all columns by board id");
        }

        return columns;
    }
}
