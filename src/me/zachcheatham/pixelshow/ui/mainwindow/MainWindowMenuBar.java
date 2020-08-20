package me.zachcheatham.pixelshow.ui.mainwindow;

import me.zachcheatham.pixelshow.Constants;

import javax.swing.*;

public class MainWindowMenuBar extends JMenuBar
{
    protected MainWindowMenuBar(MainWindow mainWindow)
    {
        JMenu file = new JMenu(Constants.TRANSLATION_FILE);
        add(file);
        addMenuItem(mainWindow, file, Constants.TRANSLATION_NEW_SHOW);
        addMenuItem(mainWindow, file, Constants.TRANSLATION_OPEN_SHOW);
        addMenuItem(mainWindow, file, Constants.TRANSLATION_SAVE_SHOW);
        addMenuItem(mainWindow, file, Constants.TRANSLATION_RENDER_SHOW);
        addMenuItem(mainWindow, file, Constants.TRANSLATION_CLOSE_APP);

        JMenu music = new JMenu(Constants.TRANSLATION_MUSIC);
        add(music);

        addMenuItem(mainWindow, music, Constants.TRANSLATION_SET_WAV);
        addMenuItem(mainWindow, music, Constants.TRANSLATION_PLAY);
        addMenuItem(mainWindow, music, Constants.TRANSLATION_PAUSE);
    }

    private void addMenuItem(MainWindow window, JMenu parent, String title)
    {
        JMenuItem item = new JMenuItem(title);
        item.addActionListener(window);
        item.setActionCommand(title);
        parent.add(item);
    }

}
