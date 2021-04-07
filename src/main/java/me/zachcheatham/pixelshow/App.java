package me.zachcheatham.pixelshow;

import me.zachcheatham.pixelshow.ui.mainwindow.MainWindow;
import org.apache.log4j.Logger;

import javax.swing.*;

import static me.zachcheatham.pixelshow.Constants.TRANSLATION_APP_TITLE;
import static me.zachcheatham.pixelshow.Constants.VERSION;

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
        LOG.info(String.format("%s Version %s", Translations.get(TRANSLATION_APP_TITLE), VERSION));

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
