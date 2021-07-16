package me.zachcheatham.pixelshow.ui.mainwindow;

import me.zachcheatham.pixelshow.show.LightsRenderer;
import me.zachcheatham.pixelshow.show.Player;
import me.zachcheatham.pixelshow.show.Show;

import javax.swing.*;
import java.awt.*;

import static me.zachcheatham.pixelshow.Constants.TARGET_FPS;

public class PreviewPanel extends JPanel
{
    private final MainWindow mainWindow;
    private double fps = 0.0;
    private long lastTime;
    private int lastFrame;
    private Show s;
    private Player r;
    private int[] pixels = null;

    public PreviewPanel(MainWindow window)
    {
        this.mainWindow = window;

        setBackground(Color.BLACK);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        if (s == null || r == null)
        {
            s = mainWindow.getShow();
            r = mainWindow.getRenderer();
            return;
        }

        int currentFrame = r.getCurrentFrame();

        // Debug info
        fps = 1000000000.0 / (System.nanoTime() - lastTime);
        lastTime = System.nanoTime();

        g.setColor(Color.WHITE);
        g.drawString(String.format("%.2f FPS", fps), 10, 20);
        g.drawString(String.format("Frame %d / %d", r.getCurrentFrame(), r.getTotalFrames()), 10, 40);


        if (lastFrame != currentFrame)
        {
            lastFrame = currentFrame;
            pixels = LightsRenderer.renderLayers(s, TARGET_FPS, currentFrame);
        }

        int ledLength = s.getLEDLength();
        int ledSize = (int) Math.floor((float) getWidth() / ledLength);

        int y = Math.round((getHeight() / 2.0f) - (ledSize / 2.0f));
        int x = Math.round(((float) getWidth() / 2.0f) - ((ledSize * ledLength) / 2.0f));
        for (int i = 0; i < ledLength; i++)
        {
            if (pixels != null)
            {
                g.setColor(new Color(pixels[i]));
                g.fillRect(x, y, ledSize, ledSize);
            }
            g.setColor(Color.WHITE);
            g.drawRect(x, y, ledSize, ledSize);
            x += ledSize;
        }
    }
}
