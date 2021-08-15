package me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline;

import me.zachcheatham.pixelshow.Translations;
import me.zachcheatham.pixelshow.show.effect.Effect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class TrackEffect extends JPanel
{
    private final Effect effect;
    private final LayerTrack.Dragger dragger;
    private final MouseHandler mouseHandler = new MouseHandler();

    public TrackEffect(Effect effect, LayerTrack.Dragger dragger) {
        this.effect = effect;
        this.dragger = dragger;

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        g.setColor(effect.getTimelineColor());
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(UIManager.getColor("textText"));
        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString(Translations.get(effect.getName()), 5, 15);
    }

    protected class MouseHandler extends MouseAdapter implements MouseMotionListener
    {
        @Override
        public void mouseDragged(MouseEvent e)
        {
            if (dragger.isDragging())
            {
                if (!dragger.isDraggingLeft()) {
                    dragger.setDragOffset(e.getX() - getWidth());
                }
                else if (dragger.isDraggingLeft()) {
                    dragger.setDragOffset(e.getX());
                }
                else {
                    dragger.setDragOffset(0);
                }

                e.consume();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {
            if (getWidth() > 6 && (e.getX() < 5 || e.getX() > getWidth() - 5))
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            else
                setCursor(Cursor.getDefaultCursor());
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            if (effect.hasFlexibleDuration() && e.getButton() == MouseEvent.BUTTON1)
            {
                if (getWidth() > 6)
                {
                    if (e.getX() > getWidth() - 5)
                    {
                        dragger.startDrag(effect, false);
                    }
                    else if (e.getX() < 5)
                    {
                        dragger.startDrag(effect, true);
                    }
                }

                e.consume();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            if (dragger.isDragging()) {
                dragger.endDrag();
            }
        }
    }
}
