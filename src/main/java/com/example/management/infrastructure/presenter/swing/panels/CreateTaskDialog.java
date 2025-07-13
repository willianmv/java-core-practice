package com.example.management.infrastructure.presenter.swing.panels;

import com.example.management.core.dto.input.CreateTaskInput;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class CreateTaskDialog extends JDialog {

    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JTextField dueDateField; // formato yyyy-MM-dd
    private boolean confirmed = false;

    public CreateTaskDialog(JFrame owner) {
        super(owner, "Create Task", true);
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(owner);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        titleField = new JTextField(20);
        descriptionArea = new JTextArea(5, 20);
        dueDateField = new JTextField(10);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        inputPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(titleField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        inputPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(new JScrollPane(descriptionArea), gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        inputPanel.add(new JLabel("Due Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        inputPanel.add(dueDateField, gbc);
        row++;

        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnConfirm = new JButton("Create");
        JButton btnCancel = new JButton("Cancel");

        btnConfirm.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnConfirm);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean validateInput() {
        if (titleField.getText().trim().isEmpty()) {
            showError("Title is required.");
            return false;
        }

        String description = descriptionArea.getText().trim();
        if (description.isEmpty()) {
            showError("Description is required.");
            return false;
        }

        if (description.length() < 5) {
            showError("Description must be at least 5 characters long.");
            return false;
        }

        if (dueDateField.getText().trim().isEmpty()) {
            showError("Due date is required.");
            return false;
        }

        try {
            LocalDate.parse(dueDateField.getText().trim());
        } catch (Exception e) {
            showError("Due date format must be yyyy-MM-dd.");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public CreateTaskInput getInput(Long columnId) {
        return new CreateTaskInput(
                titleField.getText().trim(),
                descriptionArea.getText().trim(),
                LocalDate.parse(dueDateField.getText().trim()),
                columnId
        );
    }

}
