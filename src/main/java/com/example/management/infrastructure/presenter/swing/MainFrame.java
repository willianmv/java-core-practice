package com.example.management.infrastructure.presenter.swing;

import com.example.management.core.dto.output.CompleteBoardOutput;
import com.example.management.infrastructure.config.AppContext;
import com.example.management.infrastructure.presenter.swing.panels.BoardPanel;
import com.example.management.infrastructure.presenter.swing.panels.TaskPanel;

import javax.swing.*;

public class MainFrame extends JFrame {

    private BoardPanel boardPanel;
    private TaskPanel taskPanel;
    private AppContext appContext;

    public MainFrame(AppContext appContext){
        this.appContext = appContext;
        setTitle("Task Management");
        setSize(1000,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        boardPanel = new BoardPanel(this, appContext);
        setContentPane(boardPanel);
    }

    public void switchToTaskPanel(CompleteBoardOutput boardOutput) {
        if(taskPanel == null || !taskPanel.getBoardOutput().id().equals(boardOutput.id())) {
            taskPanel = new TaskPanel(this, boardOutput, appContext);
        }
        setContentPane(taskPanel);
        revalidate();
        repaint();
    }

    public void switchToBoardPanel() {
        boardPanel.reloadBoards();
        setContentPane(boardPanel);
        revalidate();
        repaint();
    }

}
