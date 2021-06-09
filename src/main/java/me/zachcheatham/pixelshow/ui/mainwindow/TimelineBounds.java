package me.zachcheatham.pixelshow.ui.mainwindow;

public class TimelineBounds
{
    private int totalFrames = 0;
    private int waveformWidth = 0;
    protected float framesPerPixel = 1f;
    public int currentFrame = 0;

    protected void setTotalFrames(int frames)
    {
        totalFrames = frames;
        waveformWidth = Math.round(totalFrames / framesPerPixel);
    }

    protected void setFramesPerPixel(float frames)
    {
        framesPerPixel = frames;
        waveformWidth = Math.round(totalFrames / framesPerPixel);
    }

    public int getTotalFrames()
    {
        return totalFrames;
    }

    public int getWaveformWidth()
    {
        return waveformWidth;
    }

    public float getFramesPerPixel()
    {
        return framesPerPixel;
    }
}
