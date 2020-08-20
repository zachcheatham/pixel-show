package me.zachcheatham.pixelshow.ui.mainwindow;

import me.zachcheatham.pixelshow.show.Renderer;
import me.zachcheatham.pixelshow.show.Show;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class LEDRendererPanel extends JPanel
{
    private final long INITIAL_DELAY = 100;
    private final long DELAY = 33;

    private final MainWindow mainWindow;
    private final Timer timer = new Timer();
    private double fps = 0.0;
    private long lastTime;
    private int lastFrame = 0;

    public LEDRendererPanel(MainWindow window)
    {
        this.mainWindow = window;
        setBackground(Color.BLACK);

        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                repaint();
            }
        }, INITIAL_DELAY, DELAY);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paintComponent(g);

        fps = 1000000000.0 / (System.nanoTime() - lastTime);
        lastTime = System.nanoTime();

        Show s = mainWindow.getShow();
        Renderer r = mainWindow.getRenderer();
        int currentFrame = r.getCurrentFrame();
        if (currentFrame != lastFrame)
        {
            mainWindow.onFrameChanged(currentFrame);
            lastFrame = currentFrame;
        }

        g.setColor(Color.WHITE);
        g.drawString(String.format("%.2f FPS", fps), 10, 20);
        g.drawString(String.format("Frame %d / %d", currentFrame, r.getTotalFrames()), 10, 40);

        int ledLength = s.getLEDLength();
        int ledSize = (int) Math.floor((float) getWidth() / ledLength);

        int y = Math.round((getHeight() / 2.0f) - (ledSize / 2.0f));
        int x = Math.round(((float) getWidth() / 2.0f) - ((ledSize * ledLength) / 2.0f));
        for (int i = 0; i < ledLength; i++)
        {
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, ledSize, ledSize);
            g.setColor(Color.WHITE);
            g.drawRect(x, y, ledSize, ledSize);
            x += ledSize;
        }
    }
}
