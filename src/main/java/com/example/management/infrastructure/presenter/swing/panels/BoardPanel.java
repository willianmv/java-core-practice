package com.example.management.infrastructure.presenter.swing.panels;

import com.example.management.core.domain.Board;
import com.example.management.core.dto.input.UpdateBoardInput;
import com.example.management.core.dto.output.CompleteBoardOutput;
import com.example.management.core.exception.DuplicateTitleException;
import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.core.gateway.BoardGateway;
import com.example.management.core.usecase.board.CompleteBoardUseCase;
import com.example.management.core.usecase.board.CreateBoardUseCase;
import com.example.management.core.usecase.board.DeleteBoardUseCase;
import com.example.management.core.usecase.board.UpdateBoardUseCase;
import com.example.management.infrastructure.config.AppContext;
import com.example.management.infrastructure.dto.BoardResponse;
import com.example.management.infrastructure.mapper.BoardMapper;
import com.example.management.infrastructure.presenter.swing.MainFrame;
import com.example.management.infrastructure.presenter.swing.util.InputUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.example.management.infrastructure.presenter.swing.util.AppColors.*;
import static com.example.management.infrastructure.presenter.swing.util.ComponentsFactory.createCustomButton;
import static com.example.management.infrastructure.presenter.swing.util.ComponentsFactory.createSidebarFromButtons;

public class BoardPanel extends JPanel {

    private final MainFrame mainFrame;
    private JPanel selectedCard = null;
    private JPanel cardsPanel;
    private final InputUtils inputUtils;

    private final AppContext appContext;
    private final CompleteBoardUseCase completeBoardUseCase;
    private final CreateBoardUseCase createBoardUseCase;
    private final UpdateBoardUseCase updateBoardUseCase;
    private final DeleteBoardUseCase deleteBoardUseCase;
    private final BoardGateway boardGateway;
    private final BoardMapper boardMapper;

    public BoardPanel(MainFrame mainFrame, AppContext appContext){
        this.appContext = appContext;
        this.mainFrame = mainFrame;
        this.boardGateway = appContext.get(BoardGateway.class);
        this.completeBoardUseCase = appContext.get(CompleteBoardUseCase.class);
        this.createBoardUseCase = appContext.get(CreateBoardUseCase.class);
        this.updateBoardUseCase = appContext.get(UpdateBoardUseCase.class);
        this.deleteBoardUseCase = appContext.get(DeleteBoardUseCase.class);
        this.inputUtils = new InputUtils(this);
        this.boardMapper = new BoardMapper(completeBoardUseCase);

        setLayout(new BorderLayout());

        Dimension buttonSize = new Dimension(160, 40);

        JButton btnAccess = createCustomButton("Access Board", buttonSize, this::accessBoard);
        JButton btnCreate = createCustomButton("Create Board", buttonSize, this::createBoard);
        JButton btnEdit = createCustomButton("Edit Board", buttonSize, this::editBoard);
        JButton btnDelete = createCustomButton("Delete Board", buttonSize, this::deleteBoard);

        List<JButton> buttons = List.of(btnAccess, btnCreate, btnEdit, btnDelete);

        JPanel sidebar = createSidebarFromButtons(buttons, new Dimension(200, getHeight()));
        add(sidebar, BorderLayout.EAST);

        JScrollPane scrollPane = buildBoardListCards();
        add(scrollPane, BorderLayout.CENTER);

        reloadBoards();
    }

    public void reloadBoards(){
        cardsPanel.removeAll();

        List<Board> boards = boardGateway.getAll();
        for (Board board : boards) {
            BoardResponse response = boardMapper.toDto(board);
            JPanel card = buildBoardCard(response);
            cardsPanel.add(card);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JScrollPane buildBoardListCards(){
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(THIRD_COLOR);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBackground(SECONDARY_COLOR);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Boards"),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        return scrollPane;
    }

    private JPanel buildBoardCard(BoardResponse board){
        JPanel card = new JPanel();
        card.putClientProperty("board", board);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        Dimension cardSize = new Dimension(Integer.MAX_VALUE, 100);
        card.setMaximumSize(cardSize);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Title: "+board.title());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBackground(BACKGROUND_COLOR);
        lblTitle.setOpaque(true);

        JLabel lblId = new JLabel("ID: "+board.id());
        JLabel lblTaskCount = new JLabel("Task Count: "+board.taskCount());
        JLabel lblProgress = new JLabel("Progress: "+board.progress()+"%");

        card.add(lblTitle);
        card.add(lblId);
        card.add(lblTaskCount);
        card.add(lblProgress);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if(card != selectedCard){
                    card.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(card != selectedCard){
                    card.setBackground(DEFAULT_COLOR);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if(selectedCard == card){
                    selectedCard.setBackground(DEFAULT_COLOR);
                    selectedCard = null;
                }
                else if (selectedCard != null) {
                    selectedCard.setBackground(DEFAULT_COLOR);
                    selectedCard = card;
                    selectedCard.setBackground(SELECTED_COLOR);
                }
                else{
                    selectedCard = card;
                    card.setBackground(SELECTED_COLOR);
                }
            }
        });

        return card;
    }

    private BoardResponse getSelectedBoardData(){
        if(selectedCard == null) return null;

        Object data = selectedCard.getClientProperty("board");
        if(data instanceof BoardResponse board) return board;
        return null;
    }

    private void updateSelectedBoardData(BoardResponse updatedBoard){
        Component[] components = selectedCard.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel label) {
                if (label.getText().startsWith("Title: ")) {
                    label.setText("Title: " + updatedBoard.title());
                } else if (label.getText().startsWith("Progress: ")) {
                    label.setText("Progress: " + updatedBoard.progress() + "%");
                } else if (label.getText().startsWith("Task Count: ")) {
                    label.setText("Task Count: " + updatedBoard.taskCount());
                }
            }
        }

        selectedCard.revalidate();
        selectedCard.repaint();
    }

    private void accessBoard(){
        BoardResponse existingBoard = getSelectedBoardData();
        if(existingBoard == null){
            JOptionPane.showMessageDialog(this, "No board selected.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CompleteBoardOutput boardOutput = completeBoardUseCase.execute(existingBoard.id());
        mainFrame.switchToTaskPanel(boardOutput);
    }

    private void createBoard(){
        String title = inputUtils.collectString("Enter board title: ", 3);

        if(title == null){
            return;

        } else{
            try{
                Board board = createBoardUseCase.execute(title);
                BoardResponse createdBoard = boardMapper.toDto(board);

                JPanel card = buildBoardCard(createdBoard);
                cardsPanel.add(card);
                cardsPanel.revalidate();
                cardsPanel.repaint();

            }catch (DuplicateTitleException ex){
                inputUtils.showErrors("Duplicate Value", ex.getMessage());
            }
        }
    }

    private void editBoard(){
        BoardResponse existingBoard = getSelectedBoardData();
        if(existingBoard == null){
            JOptionPane.showMessageDialog(this, "No board selected.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        long id = existingBoard.id();

        String newTitle = inputUtils.collectString("Enter new title: ", 3);
        if(newTitle == null){
            return;

        } else{
            try{
                BoardResponse updatedBoard = boardMapper.toDto(updateBoardUseCase
                        .execute(new UpdateBoardInput(id, newTitle)));

                selectedCard.putClientProperty("board", updatedBoard);

                updateSelectedBoardData(updatedBoard);

                JOptionPane.showMessageDialog(this, "Board updated!");

            } catch (DuplicateTitleException ex){
                inputUtils.showErrors("Duplicate Value", ex.getMessage());
            }
        }
    }

    private void deleteBoard(){
        BoardResponse existingBoard = getSelectedBoardData();
        if(existingBoard == null){
            JOptionPane.showMessageDialog(this, "No board selected.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean confirmOption = inputUtils.showConfirmInput("Confirm Deletion",
                "Do you really want do delete the board: \"" + existingBoard.title() + "\" ?");

        if(!confirmOption){
            return;

        } else{
            try{
                deleteBoardUseCase.execute(existingBoard.id());

                cardsPanel.remove(selectedCard);
                cardsPanel.revalidate();
                cardsPanel.repaint();
                selectedCard = null;

                JOptionPane.showMessageDialog(this, "Board removed!");

            } catch (EntityNotFoundException ex){
                inputUtils.showErrors("Invalid ID", ex.getMessage());
            }
        }
    }


}
