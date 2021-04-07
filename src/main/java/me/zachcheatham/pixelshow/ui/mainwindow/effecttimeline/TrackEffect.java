package me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline;

import me.zachcheatham.pixelshow.show.effect.Effect;

import javax.swing.*;
import java.awt.*;

public class TrackEffect extends JPanel
{
    private final Effect effect;

    public TrackEffect(Effect effect) {
        System.out.println("TRACKE CREATED");

        this.effect = effect;
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        g.setColor(effect.getGUIColor());
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
