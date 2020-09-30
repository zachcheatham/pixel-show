package me.zachcheatham.pixelshow.ui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupMouseAdapter extends MouseAdapter
{
    private final MenuCreator menuCreator;

    public PopupMouseAdapter(MenuCreator menuCreator)
    {
        this.menuCreator = menuCreator;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (e.isPopupTrigger())
            showMenu(e);
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.isPopupTrigger())
            showMenu(e);
    }

    private void showMenu(MouseEvent e)
    {
        menuCreator.getMenu().show(e.getComponent(), e.getX(), e.getY());
    }

    public interface MenuCreator
    {
        JPopupMenu getMenu();
    }
}
