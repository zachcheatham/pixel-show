package me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline;

import me.zachcheatham.pixelshow.Constants;
import me.zachcheatham.pixelshow.show.Layer;
import me.zachcheatham.pixelshow.show.Show;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class EffectTimelinePanel extends JPanel
{
    private final JScrollBar horizontalScrollBar;
    private final JPanel optionsPanel = new JPanel();
    private final JPanel timelinePanel = new TimeIndicatorJPanel();
    private final List<LayerPair> layerPairs = new LinkedList<>();
    private final TimelinePanelListener listener;
    private int totalFrames = 0;
    private int currentFrame = 0;
    private float zoomFramesPerPixel = 1.0f;
    private int lastScrollbarValue = 0;

    private Show show;

    public EffectTimelinePanel(TimelinePanelListener listener)
    {
        this.listener = listener;

        setLayout(new BorderLayout());

        optionsPanel.setPreferredSize(new Dimension(150, 10));

        JScrollPane timelineScroll = new JScrollPane();
        timelineScroll.setViewportView(timelinePanel);

        timelineScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        timelineScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        timelineScroll.setBorder(null);
        horizontalScrollBar = timelineScroll.getHorizontalScrollBar();
        horizontalScrollBar.addAdjustmentListener(adjustmentEvent ->
        {
            if (lastScrollbarValue != adjustmentEvent.getValue())
            {
                listener.trackScrollChanged(Math.round(adjustmentEvent.getValue() * zoomFramesPerPixel));
                lastScrollbarValue = adjustmentEvent.getValue();
            }
        });

        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.PAGE_AXIS));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, optionsPanel, timelineScroll);
        splitPane.addPropertyChangeListener(propertyChangeEvent -> {
            if (propertyChangeEvent.getPropertyName().equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY))
            {
                listener.trackOffsetChanged(splitPane.getDividerLocation() + splitPane.getDividerSize(),
                        timelineScroll.getVerticalScrollBar().getPreferredSize().width);
            }
        });
        add(splitPane, BorderLayout.CENTER);
    }

    public void setShow(Show show)
    {
        this.show = show;
        updateViewsForShow();
    }

    private void updateViewsForShow()
    {
        layerPairs.clear();

        for (Layer layer : show.layers)
        {
            LayerPair layerPair = new LayerPair(layer);
            layerPairs.add(layerPair);
            timelinePanel.add(layerPair.layerTrack);
            layerPair.layerTrack.setMinimumSize(new Dimension(0, Constants.TRACK_HEIGHT));
            layerPair.layerTrack.setAlignmentX(LEFT_ALIGNMENT);
        }

        updateLayerBounds();
    }

    public void setViewStart(int frame)
    {
        horizontalScrollBar.setValue(Math.round(frame / zoomFramesPerPixel));
    }

    public void setTotalFrames(int frames)
    {
        totalFrames = frames;
        updateLayerBounds();
    }

    public void setZoom(float framesPerPixel)
    {
        zoomFramesPerPixel = framesPerPixel;
        updateLayerBounds();
    }

    public void updateLayerBounds()
    {
        timelinePanel.setPreferredSize(new Dimension(
                Math.round(totalFrames / zoomFramesPerPixel),
                Constants.TRACK_HEIGHT * layerPairs.size()));
    }

    public void setCurrentPosition(int frame)
    {
        currentFrame = frame;
        timelinePanel.repaint();
    }

    public interface TimelinePanelListener
    {
        void trackOffsetChanged(int offsetLeft, int offsetRight);
        void trackScrollChanged(int startFrame);
    }

    private class TimeIndicatorJPanel extends JPanel
    {
        @Override
        public void paint(Graphics g)
        {
            super.paint(g);

            int x = (int) Math.floor(currentFrame / zoomFramesPerPixel);
            g.setColor(Color.BLACK);
            g.drawLine(x, 0, x, getHeight());
        }
    }
}
