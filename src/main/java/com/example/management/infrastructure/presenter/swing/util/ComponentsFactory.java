package com.example.management.infrastructure.presenter.swing.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.example.management.infrastructure.presenter.swing.util.AppColors.*;

public class ComponentsFactory {

    public static JButton createCustomButton(String text, Dimension size, Runnable onClick){
        JButton button = new JButton(text);
        button.setMaximumSize(size);
        button.setPreferredSize(size);
        button.setBackground(DEFAULT_COLOR);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(DEFAULT_COLOR);
            }
        });

        button.addActionListener(e -> onClick.run());
        return button;
    }

    public static JPanel createSidebarFromButtons(List<JButton> buttons, Dimension size){
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(size);

        panel.add(Box.createVerticalGlue());

        for(JButton button : buttons){
            panel.add(button);
            panel.add(Box.createVerticalStrut(10));
        }

        panel.add(Box.createVerticalGlue());

        return panel;
    }
}
