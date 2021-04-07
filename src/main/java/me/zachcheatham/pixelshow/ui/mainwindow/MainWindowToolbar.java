package me.zachcheatham.pixelshow.ui.mainwindow;

import me.zachcheatham.pixelshow.Translations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static me.zachcheatham.pixelshow.Constants.*;

public class MainWindowToolbar extends JToolBar
{
    public MainWindowToolbar(MainWindow w)
    {
        addButton(w, TRANSLATION_ACTION_PLAY);
        addSeparator();
        addButton(w, TRANSLATION_ACTION_ADD_LAYER);
        addSeparator();
        addButton(w, TRANSLATION_ACTION_ZOOM_IN);
        addButton(w, TRANSLATION_ACTION_ZOOM_OUT);
        addButton(w, TRANSLATION_ACTION_ZOOM_FIT);
    }

    private void addButton(ActionListener listener, String translation)
    {
        JButton button = new JButton(Translations.get(translation));
        button.setMinimumSize(new Dimension(-1, 20));
        button.setActionCommand(translation);
        button.addActionListener(listener);
        add(button);
    }
}
