package me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline;

import me.zachcheatham.pixelshow.Translations;
import me.zachcheatham.pixelshow.show.Layer;
import me.zachcheatham.pixelshow.show.effect.Effect;
import me.zachcheatham.pixelshow.show.exception.InvalidEffectPositionException;
import me.zachcheatham.pixelshow.ui.PopupMouseAdapter;
import me.zachcheatham.pixelshow.ui.addeffectwindow.AddEffectWindow;
import me.zachcheatham.pixelshow.ui.mainwindow.TimelineBounds;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static me.zachcheatham.pixelshow.Constants.TRANSLATION_ACTION_ADD_EFFECT;

public class LayerTrack extends JPanel implements ActionListener, AddEffectWindow.EffectCreatedCallback
{
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private final Layer layer;
    private final Map<Effect, TrackEffect> trackEffects = new HashMap<>();
    private final TimelineBounds timelineBounds;
    private final Dragger dragger = new Dragger();

    public LayerTrack(Layer layer, TimelineBounds timelineBounds)
    {
        this.timelineBounds = timelineBounds;

        setLayout(null);

        this.layer = layer;
        addMouseListener(new PopupMouseAdapter(e -> new LayerTrackPopupMenu(e.getX())));

        for (Effect effect : layer.getEffects()) {
            TrackEffect trackEffect = new TrackEffect(effect, dragger);
            trackEffects.put(effect, trackEffect);

            add(trackEffect);
            updateEffectBounds(effect, trackEffect);
        }
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        g.setColor(UIManager.getColor("textText"));
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

        if (dragger.isDragging())
            g.drawLine(dragger.currentX, 0, dragger.currentX, getHeight() - 1);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        switch (actionEvent.getActionCommand())
        {
            case TRANSLATION_ACTION_ADD_EFFECT:
                AddEffectWindow addEffectWindow = new AddEffectWindow(timelineBounds.currentFrame,  this);
                addEffectWindow.setVisible(true);
                break;
        }
    }

    @Override
    public void effectCreated(Effect effect)
    {
        try
        {
            effect.setStartFrame(timelineBounds.currentFrame);

            layer.addEffect(effect);
            //layer.getEffectsBetweenFrames(effect.getStartPosition(), effect.getStopPosition());

            TrackEffect trackEffect = new TrackEffect(effect, dragger);
            trackEffects.put(effect, trackEffect);
            add(trackEffect);
            updateEffectBounds(effect, trackEffect);
        }
        catch (InvalidEffectPositionException e)
        {
            e.printStackTrace();
        }
    }

    public void trackBoundsUpdated()
    {
        for (Map.Entry<Effect, TrackEffect> entry : trackEffects.entrySet())
            updateEffectBounds(entry.getKey(), entry.getValue());
    }

    private void updateEffectBounds(Effect e, TrackEffect te)
    {
        int startX = Math.round((float) (e.getStartFrame() * timelineBounds.getWaveformWidth()) / (float) layer.getShow().getFrameLength());
        int width = Math.round((float) (e.getDuration() * timelineBounds.getWaveformWidth()) / (float) layer.getShow().getFrameLength());

        te.setBounds(startX, 0, width, getHeight() - 1);
        te.repaint();
    }

    public class LayerTrackPopupMenu extends JPopupMenu
    {
        public LayerTrackPopupMenu(float trackX)
        {
            int rightClickFrame = (int) Math.floor(layer.getShow().getFrameLength() * trackX / timelineBounds.getWaveformWidth());

            JMenuItem addEffect = new JMenuItem(Translations.get(TRANSLATION_ACTION_ADD_EFFECT));
            addEffect.addActionListener(LayerTrack.this);
            addEffect.setActionCommand(TRANSLATION_ACTION_ADD_EFFECT);
            add(addEffect);

            if (layer.getShow().getFrameLength() == 0 || layer.getEffectAtFrame(rightClickFrame) != null)
                addEffect.setEnabled(false);
        }
    }

    public class Dragger
    {
        private Effect currentEffect = null;
        private int minDragFrame = -1;
        private int maxDragFrame = -1;
        private boolean draggingLeft = false;
        private int currentX = 0;
        private int currentFrame = 0;

        public void startDrag(Effect e, boolean left)
        {
            if (!e.hasFlexibleDuration())
                throw new IllegalArgumentException("Attempted drag on inflexible effect.");

            currentEffect = e;
            draggingLeft = left;

            if (draggingLeft)
            {
                Effect previousEffect = layer.getPreviousEffect(e);
                if (previousEffect != null) {
                    minDragFrame = previousEffect.getEndFrame() + 1;
                }
                else {
                    minDragFrame = 0;
                }
                maxDragFrame = e.getEndFrame();
            }
            else
            {
                Effect nextEffect = layer.getNextEffect(e);
                if (nextEffect != null) {
                    maxDragFrame = nextEffect.getStartFrame() - 1;
                }
                else {
                    maxDragFrame = timelineBounds.getTotalFrames();
                }
                minDragFrame = e.getStartFrame();
            }
        }

        public void endDrag()
        {
            if (draggingLeft) {
                currentEffect.setDuration(currentEffect.getEndFrame() - currentFrame);
                currentEffect.setStartFrame(currentFrame);
            }
            else {
                currentEffect.setDuration(currentFrame - currentEffect.getStartFrame());
            }

            currentEffect = null;
            trackBoundsUpdated();
            LayerTrack.this.repaint();
        }

        public void setDragOffset(int offsetX)
        {
            int x;
            TrackEffect te = trackEffects.get(currentEffect);

            if (draggingLeft) x = te.getX() + offsetX;
            else x = te.getX() + te.getWidth() + offsetX;
            currentFrame = Math.round((float) (x * timelineBounds.getTotalFrames()) / timelineBounds.getWaveformWidth());

            if (currentFrame < minDragFrame)
                currentFrame = minDragFrame;
            else if (currentFrame > maxDragFrame)
                currentFrame = maxDragFrame;

            currentX = Math.round(currentFrame / timelineBounds.getFramesPerPixel());

            LayerTrack.this.repaint();
        }

        public boolean isDragging()
        {
            return currentEffect != null;
        }

        public boolean isDraggingLeft()
        {
            return draggingLeft;
        }

        public int getDragX()
        {
            return currentX;
        }
    }
}
