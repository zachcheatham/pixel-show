package me.zachcheatham.pixelshow;

import me.zachcheatham.pixelshow.ui.mainwindow.MainWindow;
import org.apache.log4j.Logger;

import javax.swing.*;

public class App implements Runnable
{
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());

    public static void main(String[] args)
    {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        SwingUtilities.invokeLater(new App());
    }

    public void run()
    {
        LOG.info(String.format("%s Version %s", Constants.TRANSLATION_APP_TITLE, Constants.VERSION));

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
