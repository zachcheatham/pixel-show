package me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline;

import me.zachcheatham.pixelshow.show.Layer;

public class LayerPair
{
    public final LayerOptions layerOptions;
    public final LayerTrack layerTrack;

    public LayerPair(Layer layer)
    {
        layerOptions = new LayerOptions(layer);
        layerTrack = new LayerTrack(layer);
    }
}
