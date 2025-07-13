package com.example.management.infrastructure.presenter.swing.panels;

import com.example.management.core.domain.Task;
import com.example.management.core.dto.input.CreateTaskInput;
import com.example.management.core.dto.input.MoveTaskInput;
import com.example.management.core.dto.input.UpdateTaskInput;
import com.example.management.core.dto.output.ColumnOutput;
import com.example.management.core.dto.output.CompleteBoardOutput;
import com.example.management.core.enums.ColumnType;
import com.example.management.core.exception.DuplicateTitleException;
import com.example.management.infrastructure.exception.EntityNotFoundException;
import com.example.management.core.exception.InvalidDueDateException;
import com.example.management.core.gateway.TaskGateway;
import com.example.management.core.usecase.task.*;
import com.example.management.infrastructure.config.AppContext;
import com.example.management.infrastructure.dto.TaskResponse;
import com.example.management.infrastructure.presenter.swing.MainFrame;
import com.example.management.infrastructure.presenter.swing.util.AppColors;
import com.example.management.infrastructure.presenter.swing.util.InputUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.management.infrastructure.presenter.swing.util.AppColors.*;
import static com.example.management.infrastructure.presenter.swing.util.ComponentsFactory.createCustomButton;
import static com.example.management.infrastructure.presenter.swing.util.ComponentsFactory.createSidebarFromButtons;

public class TaskPanel extends JPanel {

    private final MainFrame mainFrame;
    private final CompleteBoardOutput boardOutput;
    private final InputUtils inputUtils;
    private final Map<Long, JPanel> columnPanels = new HashMap<>();
    private JPanel selectedTaskData = null;

    private final AppContext appContext;
    private final TaskGateway taskGateway;
    private final CreateTaskUseCase createTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final BlockTaskUseCase blockTaskUseCase;
    private final UnblockTaskUseCase unblockTaskUseCase;
    private final MoveTaskUseCase moveTaskUseCase;

    public TaskPanel(MainFrame mainFrame, CompleteBoardOutput boardOutput, AppContext appContext) {
        this.appContext = appContext;
        this.taskGateway = appContext.get(TaskGateway.class);
        this.createTaskUseCase = appContext.get(CreateTaskUseCase.class);
        this.updateTaskUseCase = appContext.get(UpdateTaskUseCase.class);
        this.deleteTaskUseCase = appContext.get(DeleteTaskUseCase.class);
        this.blockTaskUseCase = appContext.get(BlockTaskUseCase.class);
        this.unblockTaskUseCase = appContext.get(UnblockTaskUseCase.class);
        this.moveTaskUseCase = appContext.get(MoveTaskUseCase.class);

        this.mainFrame = mainFrame;
        this.inputUtils = new InputUtils(this);
        this.boardOutput = boardOutput;

        setLayout(new BorderLayout());

        Dimension buttonSize = new Dimension(160, 40);

        JButton btnGoBack = createCustomButton("Go back", buttonSize, this::goBackToMainFrame);
        JButton btnAccess = createCustomButton("Access Task", buttonSize, this::accessTask);
        JButton btnCreateTask = createCustomButton("Create Task", buttonSize, this::createTask);
        JButton btnEditTask = createCustomButton("Edit Task", buttonSize, this::editTask);
        JButton btnDeleteTask = createCustomButton("Delete Task", buttonSize, this::deleteTask);
        JButton btnBlockTask = createCustomButton("Block Task", buttonSize, this::blockTask);
        JButton btnUnblockTask = createCustomButton("Unblock Task", buttonSize, this::unblockTask);
        JButton btnMoveTask = createCustomButton("Move Task", buttonSize, this::moveTask);

        List<JButton> buttons = List.of(btnGoBack, btnAccess, btnCreateTask, btnEditTask,
                btnBlockTask, btnUnblockTask, btnMoveTask, btnDeleteTask);

        JPanel sidebar = createSidebarFromButtons(buttons, new Dimension(200, getHeight()));
        add(sidebar, BorderLayout.EAST);

        JScrollPane columnsScrollPane = buildColumnsView();
        add(columnsScrollPane, BorderLayout.CENTER);
    }

    private JScrollPane buildColumnsView() {
        JPanel columnsPanel = new JPanel();
        columnsPanel.setLayout(new BoxLayout(columnsPanel, BoxLayout.X_AXIS));
        columnsPanel.setBackground(AppColors.SECONDARY_COLOR);
        columnsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (ColumnOutput column : boardOutput.columns()) {
            JScrollPane columnScrollPane = buildColumn(column);

            JPanel columnPanel = columnPanels.get(column.id());
            if(columnPanel != null && column.tasks() != null){
                for(var task : column.tasks()){
                    TaskResponse taskResponse = new TaskResponse(
                      task.id(),
                      task.title(),
                      task.description(),
                      task.dueDate(),
                      task.blocked(), column.id());
                    JPanel taskCard = buildTaskCard(taskResponse);
                    columnPanel.add(Box.createVerticalStrut(8));
                    columnPanel.add(taskCard);
                }
            }

            columnsPanel.add(columnScrollPane);
            columnsPanel.add(Box.createHorizontalStrut(20));
        }

        JScrollPane scrollPane = new JScrollPane(columnsPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        return scrollPane;
    }

    private JScrollPane buildColumn(ColumnOutput column) {
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));
        columnPanel.setBackground(AppColors.THIRD_COLOR);
        columnPanel.setBorder(BorderFactory.createTitledBorder(column.type().getTitle()));
        columnPanel.setPreferredSize(new Dimension(200, 400));

        columnPanels.put(column.id(), columnPanel);

        JScrollPane scrollPane = new JScrollPane(columnPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(220, 500));

        return scrollPane;
    }

    private JPanel buildTaskCard(TaskResponse task) {
        JPanel card = new JPanel();
        card.putClientProperty("task", task);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        card.setMaximumSize(new Dimension(180, 80));

        JLabel idLabel = new JLabel(String.valueOf(task.id()));
        JLabel titleLabel = new JLabel(task.title());
        JLabel dueDateLabel = new JLabel("Due: " + task.dueDate());
        JLabel isBlockedLabel = new JLabel("Blocked: " + task.blocked());

        card.putClientProperty("task", task);
        card.putClientProperty("titleLabel", titleLabel);
        card.putClientProperty("dueDateLabel", dueDateLabel);
        card.putClientProperty("isBlockedLabel", isBlockedLabel);

        card.add(idLabel);
        card.add(titleLabel);
        card.add(dueDateLabel);
        card.add(isBlockedLabel);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if(card != selectedTaskData){
                    card.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(card != selectedTaskData){
                    card.setBackground(DEFAULT_COLOR);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if(selectedTaskData == card){
                    selectedTaskData.setBackground(DEFAULT_COLOR);
                    selectedTaskData = null;
                }
                else if (selectedTaskData != null) {
                    selectedTaskData.setBackground(DEFAULT_COLOR);
                    selectedTaskData = card;
                    selectedTaskData.setBackground(SELECTED_COLOR);
                }
                else{
                    selectedTaskData = card;
                    card.setBackground(SELECTED_COLOR);
                }
            }
        });

        return card;
    }

    public CompleteBoardOutput getBoardOutput() {
        return boardOutput;
    }

    private void goBackToMainFrame(){
        mainFrame.switchToBoardPanel();
    }

    private TaskResponse getSelectedTaskData(){
        if(selectedTaskData == null) return null;

        Object data = selectedTaskData.getClientProperty("task");
        if(data instanceof TaskResponse task) return task;
        return null;
    }

    private void createTask(){
        ColumnOutput column = boardOutput.columns().stream()
                .filter(col -> col.type() == ColumnType.TO_DO)
                .findFirst().orElse(null);

        if(column == null){
            JOptionPane.showMessageDialog(this, "No \"TO DO\" column found to create the task.");
            return;
        }

        CreateTaskDialog dialog = new CreateTaskDialog(mainFrame);
        dialog.setVisible(true);

        if(dialog.isConfirmed()){
            try {
                CreateTaskInput createTaskInput = dialog.getInput(column.id());
                Task task = createTaskUseCase.execute(createTaskInput);

                JPanel taskCard = buildTaskCard(new TaskResponse(task.getId(),
                        task.getTitle(), task.getDescription(), task.getDueDate(),
                        task.isBlocked(), task.getColumn().getId()));

                JPanel columnPanel = columnPanels.get(column.id());
                if(columnPanel != null){
                    columnPanel.add(Box.createVerticalStrut(8));
                    columnPanel.add(taskCard);
                    columnPanel.revalidate();
                    columnPanel.repaint();
                }

                JOptionPane.showMessageDialog(this, "Task created successfully!");

            }catch (DuplicateTitleException | InvalidDueDateException | EntityNotFoundException ex) {
                inputUtils.showErrors("Error", ex.getMessage());
            }
        }
    }

    private void accessTask(){
        TaskResponse selectedTask = getSelectedTaskData();
        if(selectedTask == null){
            JOptionPane.showMessageDialog(this, "No task selected.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        showTaskDetails(selectedTask);
    }

    private void showTaskDetails(TaskResponse task) {
        JDialog dialog = new JDialog(mainFrame, "Task Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        content.add(new JLabel("Title: " + task.title()));
        content.add(Box.createVerticalStrut(8));
        content.add(new JLabel("Description:"));
        JTextArea descriptionArea = new JTextArea(task.description());
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(dialog.getBackground());
        content.add(new JScrollPane(descriptionArea));
        content.add(Box.createVerticalStrut(8));
        content.add(new JLabel("Due Date: " + task.dueDate()));
        content.add(new JLabel("Blocked: " + (task.blocked() ? "Yes" : "No")));

        dialog.add(content, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);

        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editTask(){
        TaskResponse selectedTask = getSelectedTaskData();
        if(selectedTask == null){
            JOptionPane.showMessageDialog(this, "No task selected.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CreateTaskDialog dialog = new CreateTaskDialog(mainFrame);
        dialog.setVisible(true);

        if(dialog.isConfirmed()){
            try{
                CreateTaskInput input = dialog.getInput(selectedTask.columnId());
                UpdateTaskInput updateTaskInput = new UpdateTaskInput(
                        selectedTask.id(),
                        input.title(),
                        input.description(),
                        input.dueDate());

                Task updatedTask = updateTaskUseCase.execute(updateTaskInput);

                // Atualize a referência da task armazenada
                TaskResponse updatedResponse = new TaskResponse(
                        updatedTask.getId(),
                        updatedTask.getTitle(),
                        updatedTask.getDescription(),
                        updatedTask.getDueDate(),
                        updatedTask.isBlocked(),
                        updatedTask.getColumn().getId()
                );

                selectedTaskData.putClientProperty("task", updatedResponse);

                // Atualize os campos visuais
                JLabel titleLabel = (JLabel) selectedTaskData.getClientProperty("titleLabel");
                JLabel descLabel = (JLabel) selectedTaskData.getClientProperty("titleLabel");
                JLabel dueDateLabel = (JLabel) selectedTaskData.getClientProperty("dueDateLabel");
                JLabel isBlockedLabel = (JLabel) selectedTaskData.getClientProperty("isBlockedLabel");

                if (titleLabel != null) titleLabel.setText(updatedResponse.title());
                if (dueDateLabel != null) dueDateLabel.setText("Due: " + updatedResponse.dueDate());
                if (isBlockedLabel != null) isBlockedLabel.setText("Blocked: " + updatedResponse.blocked());

                selectedTaskData.revalidate();
                selectedTaskData.repaint();

                JOptionPane.showMessageDialog(this, "Task updated successfully!");

            } catch (Exception e){
                inputUtils.showErrors("Error", e.getMessage());
            }

        }
    }

    private void deleteTask(){
        TaskResponse task = getSelectedTaskData();
        if (task == null) {
            JOptionPane.showMessageDialog(this, "No task selected.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean confirmOption = inputUtils.showConfirmInput("Confirm Deletion",
                "Do you really want to delete the task: \"" + task.title() + "\"?");

        if (!confirmOption) {
            return;
        }

        try {
            deleteTaskUseCase.execute(task.id());

            JPanel columnPanel = columnPanels.get(task.columnId());
            if (columnPanel != null && selectedTaskData != null) {
                columnPanel.remove(selectedTaskData);
                columnPanel.revalidate();
                columnPanel.repaint();
            }

            selectedTaskData = null;

            JOptionPane.showMessageDialog(this, "Task removed successfully!");

        } catch (EntityNotFoundException ex) {
            inputUtils.showErrors("Invalid ID", ex.getMessage());
        }
    }

    private void blockTask() {
        TaskResponse selectedTask = getSelectedTaskData();
        if (selectedTask == null) {
            JOptionPane.showMessageDialog(this, "No task selected.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmação
        boolean confirm = inputUtils.showConfirmInput("Block Task",
                "Do you really want to block this task?");
        if (!confirm) return;

        try {
            blockTaskUseCase.execute(selectedTask.id());
            JOptionPane.showMessageDialog(this, "Task blocked successfully!");

            refreshTaskCard(selectedTaskData);
        } catch (Exception e) {
            inputUtils.showErrors("Error", e.getMessage());
        }
    }

    private void unblockTask() {
        TaskResponse selectedTask = getSelectedTaskData();
        if (selectedTask == null) {
            JOptionPane.showMessageDialog(this, "No task selected.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean confirm = inputUtils.showConfirmInput("Unblock Task",
                "Do you really want to unblock this task?");
        if (!confirm) return;

        try {
            unblockTaskUseCase.execute(selectedTask.id());
            JOptionPane.showMessageDialog(this, "Task unblocked successfully!");

            refreshTaskCard(selectedTaskData);
        } catch (Exception e) {
            inputUtils.showErrors("Error", e.getMessage());
        }
    }

    private void moveTask() {
        TaskResponse selectedTask = getSelectedTaskData();
        if (selectedTask == null) {
            JOptionPane.showMessageDialog(this, "No task selected.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mostra lista de colunas possíveis para mover (exceto a atual)
        List<ColumnOutput> availableColumns = boardOutput.columns().stream()
                .filter(c -> !c.id().equals(selectedTask.columnId()))
                .toList();

        if (availableColumns.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No other columns available to move the task.");
            return;
        }

        String[] options = availableColumns.stream()
                .map(c -> c.type().getTitle())
                .toArray(String[]::new);

        String selectedOption = (String) JOptionPane.showInputDialog(
                this,
                "Select target column:",
                "Move Task",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selectedOption == null) return; // Usuário cancelou

        // Recupera a coluna selecionada
        ColumnOutput targetColumn = availableColumns.stream()
                .filter(c -> c.type().getTitle().equals(selectedOption))
                .findFirst()
                .orElse(null);

        if (targetColumn == null) {
            inputUtils.showErrors("Error", "Invalid column selected.");
            return;
        }

        try {
            MoveTaskInput moveTaskInput = new MoveTaskInput(selectedTask.id(), targetColumn.id());
            moveTaskUseCase.execute(moveTaskInput);

            // Remove do painel atual
            JPanel fromPanel = columnPanels.get(selectedTask.columnId());
            JPanel toPanel = columnPanels.get(targetColumn.id());

            if (fromPanel != null && toPanel != null) {
                fromPanel.remove(selectedTaskData);
                fromPanel.revalidate();
                fromPanel.repaint();

                toPanel.add(Box.createVerticalStrut(8));
                toPanel.add(selectedTaskData);
                toPanel.revalidate();
                toPanel.repaint();
            }

            // Atualiza visual e referência
            Task updated = taskGateway.findById(selectedTask.id());
            TaskResponse updatedTask = new TaskResponse(
                    updated.getId(), updated.getTitle(), updated.getDescription(),
                    updated.getDueDate(), updated.isBlocked(), updated.getColumn().getId()
            );

            selectedTaskData.putClientProperty("task", updatedTask);
            refreshTaskCard(selectedTaskData);

            JOptionPane.showMessageDialog(this, "Task moved to \"" + selectedOption + "\" column.");

        } catch (Exception e) {
            inputUtils.showErrors("Error", e.getMessage());
        }
    }

    private void refreshTaskCard(JPanel card) {
        TaskResponse original = getSelectedTaskData();
        if (original == null) return;

        Task updated = taskGateway.findById(original.id());
        for (Component comp : card.getComponents()) {
            if (comp instanceof JLabel label && label.getText().startsWith("Blocked:")) {
                label.setText("Blocked: " + updated.isBlocked());
            }
        }

        TaskResponse updatedTask = new TaskResponse(
                updated.getId(), updated.getTitle(), updated.getDescription(),
                updated.getDueDate(), updated.isBlocked(), updated.getColumn().getId()
        );
        card.putClientProperty("task", updatedTask);

        card.revalidate();
        card.repaint();
    }

}
