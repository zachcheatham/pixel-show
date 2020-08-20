package me.zachcheatham.pixelshow.show;

import javax.sound.sampled.*;
import java.io.*;

import static me.zachcheatham.pixelshow.Constants.TARGET_FPS;

public class Renderer
{
    private Clip clip = AudioSystem.getClip();
    private long pausedPosition = 0;
    private int totalFrames = 0;
    private int currentFrame =0;
    private File audioFile;

    public Renderer() throws LineUnavailableException
    {
    }

    public void setAudio(File file)
    {
        audioFile = file.getAbsoluteFile();
        pausedPosition = 0;

        setupAudioStream();

        totalFrames = (int) Math.floor(clip.getMicrosecondLength() / 1000000f * TARGET_FPS);
    }

    public void play()
    {
        clip.setMicrosecondPosition(pausedPosition);
        clip.start();
    }

    public void setPlaybackPosition(long microsecondPosition)
    {
        clip.setMicrosecondPosition(microsecondPosition);
        System.out.println("Set position");
    }

    public void pause()
    {
        pausedPosition = clip.getMicrosecondPosition();
        clip.stop();
    }

    public int getCurrentFrame()
    {
        currentFrame = (int) Math.floor(clip.getMicrosecondPosition() / 1000000f * TARGET_FPS);
        if (currentFrame >= totalFrames)
            pausedPosition = 0;

        return currentFrame;
    }

    public int getTotalFrames()
    {
        return totalFrames;
    }

    private void setupAudioStream()
    {
        try
        {
            clip.close();
            clip.open(AudioSystem.getAudioInputStream(audioFile));
        }
        catch (LineUnavailableException | IOException | UnsupportedAudioFileException e)
        {
            e.printStackTrace();
        }
    }
}
