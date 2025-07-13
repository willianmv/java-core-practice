package com.example.management.infrastructure.presenter.swing.util;

import javax.swing.*;
import java.awt.*;

public class InputUtils {

    private final Component parent;

    public InputUtils(Component parent) {
        this.parent = parent;
    }

    public String collectString(String message, int minLength) {
        while (true) {
            String input = JOptionPane.showInputDialog(parent, message);

            if (input == null) return null;

            input = input.trim();
            if (input.length() < minLength) {
                JOptionPane.showMessageDialog(parent,
                        "Input must be at least " + minLength + " characters.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } else {
                return input;
            }
        }
    }

    public Long collectLong(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(parent, message);

            if (input == null) return null;

            try {
                return Long.parseLong(input.trim());

            } catch (NumberFormatException ex) {
                showErrors("Invalid Number", ex.getMessage());
            }
        }
    }

    public boolean showConfirmInput(String title, String message){
        int confirm = JOptionPane.showConfirmDialog(
                parent,
                message, title,
                JOptionPane.YES_NO_OPTION);

        return confirm == JOptionPane.YES_OPTION;
    }

    public void showErrors(String title, String message){
        JOptionPane.showMessageDialog(parent,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }


}
