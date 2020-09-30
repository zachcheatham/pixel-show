package me.zachcheatham.pixelshow.show;

import me.zachcheatham.pixelshow.Constants;

import java.util.LinkedList;
import java.util.List;

public class Show
{
    public final List<Layer> layers = new LinkedList<>();
    private final ShowListener showListener;

    private String title = Constants.TRANSLATION_UNTITLED_SHOW;
    private String audioLocation = null;
    private String fileLocation = null;
    private boolean unsaved = false;
    private boolean reading = false;
    private int lightStripSize = 30;

    public Show(ShowListener listener)
    {
        this.showListener = listener;
    }

    public void setReading(boolean reading)
    {
        this.reading = reading;
    }

    public boolean isReading()
    {
        return reading;
    }

    public boolean isUnsaved()
    {
        return unsaved;
    }

    public void setUnsaved(boolean unsaved)
    {
        if (!reading)
        {
            this.unsaved = unsaved;
            showListener.onShowSavedChanged(unsaved);
        }
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
        setUnsaved(true);
    }

    public String getFileLocation()
    {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation)
    {
        this.fileLocation = fileLocation;
    }

    public String getAudioLocation()
    {
        return audioLocation;
    }

    public void setAudioLocation(String audioLocation)
    {
        this.audioLocation = audioLocation;
    }

    public int getLEDLength()
    {
        return lightStripSize;
    }

    public void setLightStripSize(int lightStripSize)
    {
        this.lightStripSize = lightStripSize;
        setUnsaved(true);
    }

    public void createLayer(String layerName)
    {
        Layer layer = new Layer();
        layer.setName(layerName);
        addLayer(layer);
    }

    public void addLayer(Layer layer)
    {
        layers.add(layer);
        if (!reading)
        {
            setUnsaved(true);
            showListener.onLayerAdded(layers.size() - 1);
        }
    }

    public int getLayerCount()
    {
        return layers.size();
    }

    public Layer getLayerAt(int i)
    {
        return layers.get(i);
    }

    public void removeLayerAt(int i)
    {
        if (i < layers.size())
        {
            layers.remove(i);
            setUnsaved(true);
        }
    }

    public interface ShowListener
    {
        void onShowSavedChanged(boolean saved);
        void onLayerAdded(int position);
    }
}
