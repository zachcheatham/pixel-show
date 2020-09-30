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
    private final Vector<int[]> lines = new Vector<>();
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
    private int offsetLeft = 0;
    private int offsetRight = 0;

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

            int totalLines = lines.size();
            int linesStart = (int) Math.floor(frameStart / framesPerPixel);
            int linesEnds = linesStart + getWidthWithOffset();
            if (linesEnds > totalLines) linesEnds = lines.size();

            final Color rmsColor = new Color(100, 100, 220);
            final Color peakColor = new Color(50, 50, 200);

            int x = offsetLeft;
            for (int i = linesStart; i < linesEnds; i++)
            {
                int[] lineValues = lines.get(i);

                g2.setColor(peakColor);
                g2.drawLine(x, lineValues[2], x, lineValues[3]);

                if (lineValues[0] != lineValues[2] && lineValues[1] != lineValues[3])
                {
                    g2.setColor(rmsColor);
                    g2.drawLine(x, lineValues[0], x, lineValues[1]);
                }

                x++;
            }

            int playbackLine = (int) Math.floor(playbackFrame / framesPerPixel);
            if (playbackLine >= linesStart && playbackLine <= linesEnds)
            {
                int playbackX = playbackLine - linesStart + offsetLeft;
                g2.setColor(Color.BLACK);
                g2.drawLine(playbackX, 0, playbackX, getHeight());
            }

            if (offsetRight > 0 || getWidthWithOffset() > totalLines - linesStart)
            {
                g2.setColor(Color.DARK_GRAY);
                g2.fillRect(totalLines - linesStart + offsetLeft, 0, getWidth(), getHeight());
            }

            if (offsetLeft > 0)
            {
                g2.setColor(Color.DARK_GRAY);
                g2.fillRect(0, 0, offsetLeft, getHeight());
            }
        }
    }

    private void generateLines()
    {
        long start = System.currentTimeMillis();
        lines.removeAllElements();

        int halfY = Math.round(getHeight() / 2.0f);

        int formWidth = (int) Math.ceil(values.length / framesPerPixel);

        int pixel = 0;
        int framesPerPixelFloor = (int) Math.floor(framesPerPixel);
        float[] upperRMS = new float[formWidth];
        float[] lowerRMS = new float[formWidth];
        int[] upperPeaks = new int[formWidth];
        int[] lowerPeaks = new int[formWidth];
        int maxValue = 0;
        for (int startPos = 0; startPos < values.length && pixel < formWidth; startPos += framesPerPixelFloor)
        {
            int foundUpper = 0;
            int foundLower = 0;
            int totalUpper = 0;
            int totalLower = 0;
            int upperPeak = 0;
            int lowerPeak = 0;
            for (int i = 0; (i < framesPerPixelFloor) && (i+startPos < values.length); i++)
            {
                int value = values[startPos + i];
                if (value > 0)
                {
                    totalUpper += value * value;
                    foundUpper += 1;
                    if (value > upperPeak)
                        upperPeak = value;
                }
                else if (value < 0)
                {
                    totalLower += value * value;
                    foundLower += 1;
                    if (value < lowerPeak)
                        lowerPeak = value;
                }
            }

            float upperAverage = 0;
            float lowerAverage = 0;

            if (foundUpper > 0)
                upperAverage = (float) Math.sqrt((float) totalUpper / (float) foundUpper);
            if (foundLower > 0)
                lowerAverage = (float) Math.sqrt((float) totalLower / (float) foundLower) * -1;

            if (upperPeak > maxValue)
                maxValue = upperPeak;
            if (Math.abs(lowerPeak) > maxValue)
                maxValue = Math.abs(lowerPeak);

            upperRMS[pixel] = upperAverage;
            lowerRMS[pixel] = lowerAverage;
            upperPeaks[pixel] = upperPeak;
            lowerPeaks[pixel] = lowerPeak;

            pixel++;
        }

        for (int i = 0; i < formWidth; i++)
        {
            float upperRMSFraction = upperRMS[i] / maxValue;
            float lowerRMSFraction = lowerRMS[i] / maxValue;
            float upperPeakFraction = upperPeaks[i] / (float) maxValue;
            float lowerPeakFraction = lowerPeaks[i] / (float) maxValue;


            lines.add(new int[]{
                    halfY + Math.round(halfY * upperRMSFraction),
                    halfY + Math.round(halfY * lowerRMSFraction),
                    halfY + Math.round(halfY * upperPeakFraction),
                    halfY + Math.round(halfY * lowerPeakFraction)
            });
        }

        System.out.println(((System.currentTimeMillis() - start)) + "ms to generate lines.");

        repaint();
    }

    public void setCurrentPosition(int millisecond)
    {
        playbackFrame = Math.round(millisecond / 1000.0f * frameRate);
    }

    public void setViewBounds(int startMS, float msPerPixel)
    {
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

    public void setOffset(int offsetLeft, int offsetRight)
    {
        this.offsetLeft = offsetLeft;
        this.offsetRight = offsetRight;
        repaint();
    }

    public int getWidthWithOffset()
    {
        return getWidth() - offsetLeft - offsetRight;
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
            int totalLines = lines.size();
            int linesStart = (int) Math.floor(frameStart / framesPerPixel);

            int x = mouseEvent.getX() - offsetLeft;

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
