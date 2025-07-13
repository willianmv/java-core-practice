package com.example.management.infrastructure.persistence.db;

import com.example.management.core.domain.Board;
import com.example.management.infrastructure.persistence.BoardRepository;
import com.example.management.infrastructure.persistence.db.connection.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBoardRepository implements BoardRepository {

    @Override
    public Board save(Board board) {
        if(board.getId() == null){
            String sql = "INSERT INTO tb_board (title, created_at) VALUES (? ,?) RETURNING id";
            try(Connection conn = ConnectionFactory.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)){

                preparedStatement.setString(1, board.getTitle());
                preparedStatement.setTimestamp(2, Timestamp.valueOf(board.getCreatedAt()));

                ResultSet rs = preparedStatement.executeQuery();
                if(rs.next()){
                    board.setId(rs.getLong("id"));
                }

            }catch (SQLException e){
                System.out.println("Error saving board");
            }

        } else {
            String sql = "UPDATE tb_board SET title = ? WHERE id = ?";
            try(Connection conn = ConnectionFactory.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)){

                preparedStatement.setString(1, board.getTitle());
                preparedStatement.setLong(2, board.getId());
                preparedStatement.executeUpdate();

            }catch (SQLException e){
                System.out.println("Error updating board");
            }
        }
        return board;
    }

    @Override
    public boolean existsById(long id) {
        String sql = "SELECT 1 FROM tb_board WHERE id = ?";
        try(Connection conn = ConnectionFactory.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setLong(1, id);
            return preparedStatement.executeQuery().next();

        }catch (SQLException e){
            System.out.println("Error finding board by id");
        }
        return false;
    }

    @Override
    public boolean existsByTitle(String title) {
        String sql = "SELECT 1 FROM tb_board WHERE title = ?";
        try(Connection conn = ConnectionFactory.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setString(1, title);
            return preparedStatement.executeQuery().next();

        }catch (SQLException e){
            System.out.println("Error finding board by title");
        }
        return false;
    }

    @Override
    public boolean existsByTitleAndIdNot(String title, long id) {
        String sql = "SELECT 1 FROM tb_board WHERE title = ? AND id <> ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, title);
            preparedStatement.setLong(2, id);
            return preparedStatement.executeQuery().next();
        } catch (SQLException e) {
            System.out.println("Error getting board by id and title");
        }
        return false;
    }

    @Override
    public List<Board> getAll() {
        String sql = "SELECT * FROM tb_board";
        List<Board> boards = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                Board board = new Board();
                board.setId(rs.getLong("id"));
                board.setTitle(rs.getString("title"));
                board.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                boards.add(board);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all boards");
        }
        return boards;
    }

    @Override
    public Optional<Board> findById(long id) {
        String sql = "SELECT * FROM tb_board WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                Board board = new Board();
                board.setId(rs.getLong("id"));
                board.setTitle(rs.getString("title"));
                board.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return Optional.of(board);
            }
        } catch (SQLException e) {
            System.out.println("Error finding board by id");
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM tb_board WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting board by id");
        }
    }
}
