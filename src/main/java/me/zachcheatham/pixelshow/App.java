package me.zachcheatham.pixelshow;

import me.zachcheatham.pixelshow.ui.mainwindow.MainWindow;

import javax.swing.*;

public class App implements Runnable
{
    public static void main(String[] args)
    {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        SwingUtilities.invokeLater(new App());
    }

    public void run()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        JFrame mainWindow = new MainWindow();
        mainWindow.setVisible(true);
    }
}
