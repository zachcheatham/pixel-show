package me.zachcheatham.pixelshow.show;

import me.zachcheatham.pixelshow.Constants;
import me.zachcheatham.pixelshow.Translations;

import java.util.LinkedList;
import java.util.List;

public class Show
{
    public final List<Layer> layers = new LinkedList<>();
    private final ShowListener showListener;

    private String title = Translations.get(Constants.TRANSLATION_FILE_UNTITLED);
    private String audioLocation = null;
    private String fileLocation = null;
    private boolean unsaved = false;
    private boolean reading = false;
    private int frameLength = 0;
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

    /**
     * Used to update the show's internal management of the length of a song based after an audio renderer opens the
     * target audio file.
     * @param frames Frames in audio file
     */
    public void setFrameLength(int frames, boolean ignoreShorter)
    {
        // Frames of audio file have not yet been determined.
        if (frames == 0)
        {
            for (Layer layer : layers)
            {
                if (frames < layer.getFrameLength())
                {

                }
            }
        }

        this.frameLength = frames;
    }

    public int getFrameLength()
    {
        return frameLength;
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
        setUnsaved(true);
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
        Layer layer = new Layer(this);
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
