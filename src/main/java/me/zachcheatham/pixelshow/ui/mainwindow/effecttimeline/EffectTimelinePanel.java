package me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline;

import me.zachcheatham.pixelshow.Constants;
import me.zachcheatham.pixelshow.show.Layer;
import me.zachcheatham.pixelshow.show.Show;
import me.zachcheatham.pixelshow.ui.mainwindow.TimelineBounds;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class EffectTimelinePanel extends JPanel
{
    private final JScrollBar horizontalScrollBar;
    private final JPanel optionsPanel = new JPanel();
    private final JPanel timelinePanel = new JPanel();
    private final TimeIndicatorJScrollPane timelineScroll = new TimeIndicatorJScrollPane();
    private final JScrollPane optionsScroll = new JScrollPane();
    private final List<LayerPair> layerPairs = new LinkedList<>();
    private final TimelinePanelListener listener;
    private final TimelineBounds timelineBounds;
    private int lastScrollbarValue = 0;

    private Show show;

    public EffectTimelinePanel(TimelinePanelListener listener, TimelineBounds timelineBounds)
    {
        this.timelineBounds = timelineBounds;
        this.listener = listener;

        setLayout(new BorderLayout());

        optionsPanel.setPreferredSize(new Dimension(150, 10));
        optionsPanel.setLayout(new TrackLayout());

        optionsScroll.setViewportView(optionsPanel);
        optionsScroll.setBorder(null);
        optionsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        optionsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        timelineScroll.setViewportView(timelinePanel);
        timelineScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        timelineScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        timelineScroll.setBorder(null);
        horizontalScrollBar = timelineScroll.getHorizontalScrollBar();
        horizontalScrollBar.addAdjustmentListener(adjustmentEvent ->
        {
            if (lastScrollbarValue != adjustmentEvent.getValue())
            {
                listener.trackScrollChanged(Math.round(adjustmentEvent.getValue() * timelineBounds.getFramesPerPixel()));
                lastScrollbarValue = adjustmentEvent.getValue();
            }
        });

        timelineScroll.getVerticalScrollBar().addAdjustmentListener(adjustmentEvent ->
            optionsScroll.getVerticalScrollBar().setValue(adjustmentEvent.getValue()));

        timelinePanel.setLayout(new TrackLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, optionsScroll, timelineScroll);
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
        layoutLayers();
    }

    public void layoutLayers()
    {
        layerPairs.clear();
        timelinePanel.removeAll();
        optionsPanel.removeAll();

        for (Layer layer : show.layers)
        {
            LayerPair layerPair = new LayerPair(layer, timelineBounds);
            layerPairs.add(layerPair);
            timelinePanel.add(layerPair.layerTrack);
            optionsPanel.add(layerPair.layerOptions);
        }

        timelinePanel.doLayout();
        optionsPanel.doLayout();

        updateBounds();
    }

    public void setViewStart(int frame)
    {
        horizontalScrollBar.setValue(Math.round(frame / timelineBounds.getFramesPerPixel()));
    }

    public void updateBounds()
    {
        Dimension timelinePanelSize = new Dimension(
                timelineBounds.getWaveformWidth(),
                Constants.TRACK_HEIGHT * layerPairs.size());

        Dimension optionPanelSize = new Dimension(
                optionsPanel.getWidth(),
                Constants.TRACK_HEIGHT * layerPairs.size() + horizontalScrollBar.getPreferredSize().height);

        timelinePanel.setPreferredSize(timelinePanelSize);

        optionsPanel.setPreferredSize(optionPanelSize);

        repaint();

        for (LayerPair pair : layerPairs) {
            pair.layerTrack.trackBoundsUpdated();
        }
    }

    public void repaintPosition()
    {
        timelineScroll.repaintIndicator();
    }

    public interface TimelinePanelListener
    {
        void trackOffsetChanged(int offsetLeft, int offsetRight);
        void trackScrollChanged(int startFrame);
    }

    private class TimeIndicatorJScrollPane extends JScrollPane
    {
        private final int scrollXSize = getVerticalScrollBar().getPreferredSize().width;
        private final int scrollYSize = getHorizontalScrollBar().getPreferredSize().height;
        private int lastPosition = 0;

        @Override
        public void paint(Graphics g)
        {
            super.paint(g);

            int x = (int) Math.floor(timelineBounds.currentFrame / timelineBounds.getFramesPerPixel()) - horizontalScrollBar.getValue();
            g.setColor(Color.BLACK);
            g.drawLine(x , 0, x, getHeight() - scrollYSize);
        }

        public void repaintIndicator()
        {
            if (timelineBounds.currentFrame != lastPosition)
            {
                repaint(0, 0, getWidth() - scrollXSize + 1, getHeight() - scrollYSize + 1);
                lastPosition = timelineBounds.currentFrame;
            }
        }
    }
}
