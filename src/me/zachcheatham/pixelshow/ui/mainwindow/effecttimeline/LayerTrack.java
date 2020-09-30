package me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline;

import me.zachcheatham.pixelshow.show.Layer;

import javax.swing.*;
import java.awt.*;

public class LayerTrack extends JPanel
{
    private final Layer layer;

    public LayerTrack(Layer layer)
    {
        this.layer = layer;
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        g.setColor(Color.DARK_GRAY);
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }
}
