package me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline;

import me.zachcheatham.pixelshow.Constants;

import java.awt.*;

public class TrackLayout implements LayoutManager
{
    @Override
    public void addLayoutComponent(String s, Component component) {}

    @Override
    public void removeLayoutComponent(Component component) {}

    @Override
    public Dimension preferredLayoutSize(Container parent)
    {
        return new Dimension(0, 0);
    }

    @Override
    public Dimension minimumLayoutSize(Container container)
    {
        return new Dimension(0, 0);
    }

    @Override
    public void layoutContainer(Container parent)
    {
        int componentCount = parent.getComponentCount();
        if (componentCount > 0)
        {
            Dimension parentSize = parent.getSize();

            for (int i = 0; i < componentCount; i++)
            {
                Component c = parent.getComponent(i);
                c.setBounds(0, Constants.TRACK_HEIGHT * i, parentSize.width, Constants.TRACK_HEIGHT);
            }
        }
    }
}
