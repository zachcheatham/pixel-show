package me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline;

import me.zachcheatham.pixelshow.show.Layer;
import me.zachcheatham.pixelshow.ui.mainwindow.TimelineBounds;

public class LayerPair
{
    public final LayerOptions layerOptions;
    public final LayerTrack layerTrack;

    public LayerPair(Layer layer, TimelineBounds timelineBounds)
    {
        layerOptions = new LayerOptions(layer);
        layerTrack = new LayerTrack(layer, timelineBounds);
    }
}
