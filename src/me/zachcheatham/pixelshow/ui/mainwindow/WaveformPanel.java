package me.zachcheatham.pixelshow.ui.mainwindow;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class WaveformPanel extends JPanel implements MouseListener
{
    private final SamplingRenderer renderer = new SamplingRenderer();
    private final Vector<int[]> cachedLines = new Vector<>();
    private final WaveformEventListener eventListener;

    private AudioInputStream audioStream;
    private int totalDuration = 0;
    private float frameRate = 0;
    private int playbackFrame = 0;
    private int frameStart = 0;
    private float msPerPixel = 0;
    private float framesPerPixel = 0.0f;
    private int[] values = null;
    private boolean ready = false;
    private int lastHeight = 0;

    public WaveformPanel(WaveformEventListener listener)
    {
        eventListener = listener;
        setBackground(Color.LIGHT_GRAY);
    }

    public void setAudio(File file) throws IOException, UnsupportedAudioFileException
    {
        ready = false;
        audioStream = AudioSystem.getAudioInputStream(file);
        frameRate = audioStream.getFormat().getFrameRate();
        totalDuration = Math.round((audioStream.getFrameLength() * 1000) / frameRate);

        new Thread(renderer).start();

        addMouseListener(this);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paintComponent(g);

        if (ready)
        {
            if (getHeight() != lastHeight)
            {
                generateLines();
                lastHeight = getHeight();
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.BLUE);
            int totalLines = cachedLines.size();
            int linesStart = (int) Math.floor(frameStart / framesPerPixel);
            int linesEnds = linesStart + getWidth();
            if (linesEnds > totalLines) linesEnds = cachedLines.size();

            int x = 0;
            for (int i = linesStart; i < linesEnds; i++)
            {
                int[] y = cachedLines.get(i);
                g2.drawLine(x, y[0], x, y[1]);
                x++;
            }

            int playbackLine = (int) Math.floor(playbackFrame / framesPerPixel);
            if (playbackLine >= linesStart && playbackLine <= linesEnds)
            {
                int playbackX = playbackLine - linesStart;
                g2.setColor(Color.BLACK);
                g2.drawLine(playbackX, 0, playbackX, getHeight());
            }

            if (getWidth() > totalLines - linesStart)
            {
                g2.setColor(Color.DARK_GRAY);
                g2.fillRect(totalLines - linesStart, 0, getWidth(), getHeight());
            }
        }
    }

    private void generateLines()
    {
        long start = System.currentTimeMillis();
        cachedLines.removeAllElements();

        int halfY = Math.round(getHeight() / 2.0f);

        int formWidth = (int) Math.ceil(values.length / framesPerPixel);

        int pixel = 0;
        int framesPerPixelFloor = (int) Math.floor(framesPerPixel);
        float[] upperAverages = new float[formWidth];
        float[] lowerAverages = new float[formWidth];
        float maxAverage = 0;
        for (int startPos = 0; startPos < values.length && pixel < formWidth; startPos += framesPerPixelFloor)
        {
            int foundUpper = 0;
            int foundLower = 0;
            int totalUpper = 0;
            int totalLower = 0;
            for (int i = 0; (i < framesPerPixelFloor) && (i+startPos < values.length); i++)
            {
                if (values[startPos + i] > 0)
                {
                    totalUpper += values[startPos + i];
                    foundUpper += 1;
                }
                else if (values[startPos + i] < 0)
                {
                    totalLower += values[startPos + i];
                    foundLower += 1;
                }
            }

            float upperAverage = 0;
            float lowerAverage = 0;

            if (foundUpper > 0)
                upperAverage = (float) totalUpper / (float) foundUpper;
            if (foundLower > 0)
                lowerAverage = (float) totalLower / (float) foundLower;

            if (upperAverage > maxAverage)
                maxAverage = upperAverage;
            if (Math.abs(lowerAverage) > maxAverage)
                maxAverage = Math.abs(lowerAverage);

            upperAverages[pixel] = upperAverage;
            lowerAverages[pixel] = lowerAverage;

            pixel++;
        }

        for (int i = 0; i < formWidth; i++)
        {
            float upperFraction;
            float lowerFraction;

            if (upperAverages[i] != Float.MAX_VALUE)
                upperFraction = upperAverages[i] / maxAverage;
            else
                upperFraction = 0;

            if (lowerAverages[i] != Float.MAX_VALUE)
                lowerFraction = lowerAverages[i] / maxAverage;
            else
                lowerFraction = 0;

            if (upperAverages[i] == Float.MAX_VALUE)
                upperFraction = lowerFraction;

            if (lowerAverages[i] == Float.MAX_VALUE)
                lowerFraction = upperFraction;

            cachedLines.add(new int[]{halfY + Math.round(halfY * upperFraction), halfY + Math.round(halfY * lowerFraction)});
        }

        System.out.println(((System.currentTimeMillis() - start)) + "ms to generate lines.");
    }

    public void setCurrentPosition(int millisecond)
    {
        playbackFrame = Math.round(millisecond / 1000.0f * frameRate);
        repaint();
    }

    public void setViewBounds(int startMS, float msPerPixel)
    {
        System.out.println("Waveform starts at " + startMS + "ms");
        System.out.println("Showing " + msPerPixel + "ms per pixel");

        if (msPerPixel != this.msPerPixel)
        {
            this.msPerPixel = msPerPixel;

            framesPerPixel = msPerPixel / 1000.0f * frameRate;

            generateLines();
            ready = true;
        }

        frameStart = Math.round(startMS / 1000.0f * frameRate);

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent)
    {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent)
    {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent)
    {
        if (ready)
        {
            int totalLines = cachedLines.size();
            int linesStart = (int) Math.floor(frameStart / framesPerPixel);
            int x = mouseEvent.getX();

            if (x <= totalLines - linesStart)
            {
                int scrubPosition = Math.round((linesStart + x) * msPerPixel);
                eventListener.onScrub(scrubPosition);
            }
        }
    }


    @Override
    public void mouseEntered(MouseEvent mouseEvent)
    {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent)
    {

    }

    public interface WaveformEventListener
    {
        void onScrub(int millisecondPosition);
        void onWaveformRendered(int millisecondDuration);
    }

    private class SamplingRenderer implements Runnable
    {
        @Override
        public void run()
        {
            long start = System.currentTimeMillis();

            AudioFormat audioFormat = audioStream.getFormat();
            int sampleSize = audioFormat.getSampleSizeInBits();

            byte[] audioBytes = new byte[(int) (audioStream.getFrameLength() * audioFormat.getFrameSize())];
            try
            {
                //noinspection ResultOfMethodCallIgnored
                audioStream.read(audioBytes);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return;
            }

            int[] audioData;
            int MSB;
            int LSB;
            if (sampleSize == 16)
            {
                int lengthInSamples = audioBytes.length / 2;
                audioData = new int[lengthInSamples];
                if (audioFormat.isBigEndian())
                {
                    for (int i = 0; i < lengthInSamples; i++)
                    {
                        MSB = audioBytes[2*i];
                        LSB = audioBytes[2*i+1];
                        audioData[i] = MSB << 8 | (255 & LSB);
                    }
                }
                else
                {
                    for (int i = 0; i < lengthInSamples; i++)
                    {
                        LSB = audioBytes[2*i];
                        MSB = audioBytes[2*i+1];
                        audioData[i] = MSB << 8 | (255 & LSB);
                    }
                }
            }
            else if (audioFormat.getSampleSizeInBits() == 8)
            {
                audioData = new int[audioBytes.length];
                if (audioFormat.getEncoding().toString().startsWith("PCM_SIGN"))
                {
                    for (int i = 0; i < audioBytes.length; i++)
                        audioData[i] = audioBytes[i];
                }
                else
                {
                    for (int i = 0; i < audioBytes.length; i++)
                        audioData[i] = audioBytes[i] - 128;
                }
            }
            else
            {
                System.err.println("Unsupported sample size.");
                return;
            }

            int channels = audioFormat.getChannels();
            values = new int[audioData.length / channels];

            for (int i = 0; i < audioData.length; i+= channels)
            {
                int value;
                if (sampleSize == 8)
                {
                    value = audioData[i];
                }
                else
                {
                    value = 128 * audioData[i] / 32768;
                }

                values[i / channels] = value;
            }

            try
            {
                audioStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            System.out.println(((System.currentTimeMillis() - start)) + "ms to read audio waveform.");

            eventListener.onWaveformRendered(totalDuration);
        }
    }
}
