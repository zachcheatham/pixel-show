package me.zachcheatham.pixelshow.ui.mainwindow;

import me.zachcheatham.pixelshow.Translations;

import javax.swing.*;

import static me.zachcheatham.pixelshow.Constants.*;

public class MainWindowMenuBar extends JMenuBar
{
    protected MainWindowMenuBar(MainWindow mainWindow)
    {
        JMenu file = new JMenu(Translations.get(TRANSLATION_MENU_FILE));
        add(file);
        addMenuItem(mainWindow, file, TRANSLATION_MENU_FILE_NEW);
        addMenuItem(mainWindow, file, TRANSLATION_ACTION_OPEN_SHOW);
        addMenuItem(mainWindow, file, TRANSLATION_ACTION_SAVE_SHOW);
        addMenuItem(mainWindow, file, TRANSLATION_MENU_FILE_RENDER_SHOW);
        addMenuItem(mainWindow, file, TRANSLATION_MENU_FILE_EXIT);

        JMenu music = new JMenu(Translations.get(TRANSLATION_MENU_MUSIC));
        add(music);

        addMenuItem(mainWindow, music, TRANSLATION_MENU_MUSIC_OPEN);
        addMenuItem(mainWindow, music, TRANSLATION_ACTION_PLAY);
        addMenuItem(mainWindow, music, TRANSLATION_ACTION_PAUSE);

        JMenu layers = new JMenu(Translations.get(TRANSLATION_MENU_LAYERS));
        add(layers);

        addMenuItem(mainWindow, layers, TRANSLATION_ACTION_ADD_LAYER);
    }

    private void addMenuItem(MainWindow window, JMenu parent, String translation)
    {
        JMenuItem item = new JMenuItem(Translations.get(translation));
        item.addActionListener(window);
        item.setActionCommand(translation);
        parent.add(item);
    }

}
