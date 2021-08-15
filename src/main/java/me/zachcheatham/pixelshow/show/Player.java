package me.zachcheatham.pixelshow.show;

import javax.sound.sampled.*;
import java.io.*;

import static me.zachcheatham.pixelshow.Constants.TARGET_FPS;

public class Player
{
    private Clip clip = AudioSystem.getClip();
    private long pausedPosition = 0;
    private int totalFrames = 0;
    private int currentFrame = 0;
    private File audioFile;

    public Player() throws LineUnavailableException
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
        pausedPosition = microsecondPosition;
    }

    public void pause()
    {
        pausedPosition = clip.getMicrosecondPosition();
        clip.stop();
    }

    public void playPause()
    {
        if (clip.isActive()) pause();
        else play();
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
