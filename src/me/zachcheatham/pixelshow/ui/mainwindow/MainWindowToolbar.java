package me.zachcheatham.pixelshow.ui.mainwindow;

import me.zachcheatham.pixelshow.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainWindowToolbar extends JToolBar
{
    public MainWindowToolbar(MainWindow w)
    {
        addButton(w, Constants.TRANSLATION_PLAY);
        addSeparator();
        addButton(w, Constants.TRANSLATION_ADD_LAYER);
        addSeparator();
        addButton(w, Constants.TRANSLATION_ZOOM_IN);
        addButton(w, Constants.TRANSLATION_ZOOM_OUT);
        addButton(w, Constants.TRANSLATION_ZOOM_TO_WINDOW);
    }

    private void addButton(ActionListener listener, String title)
    {
        JButton button = new JButton(title);
        button.setMinimumSize(new Dimension(-1, 20));
        button.setActionCommand(title);
        button.addActionListener(listener);
        add(button);
    }
}
