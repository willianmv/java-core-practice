package com.example.management;

import com.example.management.infrastructure.config.AppContext;
import com.example.management.infrastructure.presenter.swing.MainFrame;

public class AppMain {

    public static void main(String[] args) {

        AppContext appContext = AppContext.getInstance();

        MainFrame mainFrame = new MainFrame(appContext);
        mainFrame.setVisible(true);

    }

}
