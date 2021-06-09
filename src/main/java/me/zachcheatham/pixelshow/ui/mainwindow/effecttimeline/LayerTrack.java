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

    public LayerTrack(Layer layer, TimelineBounds timelineBounds)
    {
        this.timelineBounds = timelineBounds;

        setLayout(null);

        this.layer = layer;
        addMouseListener(new PopupMouseAdapter(e -> new LayerTrackPopupMenu(e.getX())));

        for (Effect effect : layer.getEffects()) {
            TrackEffect trackEffect = new TrackEffect(effect);
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

            TrackEffect trackEffect = new TrackEffect(effect);
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

        LOG.info(e.getStartFrame());
        LOG.info(startX);
        LOG.info(timelineBounds.getWaveformWidth());
        LOG.info(layer.getShow().getFrameLength());

        te.setBounds(startX, 0, width, getHeight() - 1);
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
}
