package com.u1337.filemanager;

import com.u1337.filemanager.GUI.MainWindow;

import javax.swing.*;

public class Main implements Runnable {

    public static final String APP_NAME = "File Manager";
    private MainWindow mWindow = new MainWindow();

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Main());
    }

    public void run()
    {
        mWindow.init();
    }
}
