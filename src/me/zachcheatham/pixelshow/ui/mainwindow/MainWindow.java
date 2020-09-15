package me.zachcheatham.pixelshow.ui.mainwindow;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import me.zachcheatham.pixelshow.Constants;
import me.zachcheatham.pixelshow.show.Show;
import me.zachcheatham.pixelshow.show.Renderer;
import me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline.EffectTimelinePanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import static me.zachcheatham.pixelshow.Constants.TARGET_FPS;

public class MainWindow extends JFrame implements ActionListener, Show.ShowListener, WaveformPanel.WaveformEventListener, EffectTimelinePanel.TimelinePanelListener
{
    private Renderer showRenderer;

    private JPanel rootPanel;
    private LEDRendererPanel LEDRendererPanel;
    private WaveformPanel waveformPanel;
    private MainWindowToolbar toolbar;
    private EffectTimelinePanel effectTimelinePanel;

    private Show show;
    private float zoomFramesPerPixel = 0.0f;
    private int visibleFramesStart = 0;
    private int leftOffset = 0;

    public MainWindow()
    {
        super(Constants.TRANSLATION_APP_TITLE);

        setPreferredSize(new Dimension(1000, 600));

        setJMenuBar(new MainWindowMenuBar(this));
        setContentPane(rootPanel);
        pack();

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (show.isUnsaved())
                {
                    int response = JOptionPane.showConfirmDialog(null,
                            Constants.TRANSLATION_CONFIRM_EXIT_NO_SAVE,
                            Constants.TRANSLATION_CONFIRM_EXIT_NO_SAVE_TITLE,
                            JOptionPane.YES_NO_OPTION);

                    if (response == JOptionPane.NO_OPTION)
                        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    else
                        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
                else
                {
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        });

        try
        {
            showRenderer = new Renderer();
        }
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }

        Show show = new Show(this);
        // TODO Debug Layers
        for (int i = 0; i < 10; i++)
            show.createLayer("Layer " + i);

        setShow(show);
    }

    private void setShow(Show show)
    {
        /*if (this.showRenderer != null)
            this.showRenderer.cleanup()*/
        this.show = show;
        setTitle(String.format("%s - %s", Constants.TRANSLATION_APP_TITLE, show.getTitle()));

        effectTimelinePanel.setShow(show);
    }

    public Show getShow()
    {
        return show;
    }

    public Renderer getRenderer()
    {
        return showRenderer;
    }

    private void zoomToWindow()
    {
        zoomFramesPerPixel = showRenderer.getTotalFrames() / (float) waveformPanel.getWidthWithOffset();
        visibleFramesStart = 0;
        updateWaveformViewBounds();
        effectTimelinePanel.setZoom(zoomFramesPerPixel);
    }

    private void updateWaveformViewBounds()
    {
        float startMS = (visibleFramesStart / (float) TARGET_FPS) * 1000.0f;
        float msPerPixel = (zoomFramesPerPixel / (float) TARGET_FPS) * 1000.0f;
        waveformPanel.setViewBounds((int) startMS, msPerPixel);
    }

    public void openMP3Chooser(Component source)
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Constants.TRANSLATION_OPEN_WAV);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter(Constants.WAV_AUDIO, "wav"));
        if (fileChooser.showOpenDialog(source) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                waveformPanel.setAudio(fileChooser.getSelectedFile());
            }
            catch (IOException | UnsupportedAudioFileException e)
            {
                e.printStackTrace();
            }
            showRenderer.setAudio(fileChooser.getSelectedFile());
            effectTimelinePanel.setTotalFrames(showRenderer.getTotalFrames());
            // todo show.setMusicFile(fileChooser.getSelectFile().getPath());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case Constants.TRANSLATION_CLOSE_APP:
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                break;
            case Constants.TRANSLATION_SET_WAV:
                openMP3Chooser((Component) e.getSource());
                break;
            case Constants.TRANSLATION_PLAY:
                showRenderer.play();
                break;
            case Constants.TRANSLATION_PAUSE:
                showRenderer.pause();
                break;
            case Constants.TRANSLATION_ZOOM_TO_WINDOW:
                zoomToWindow();
                break;
            case Constants.TRANSLATION_ZOOM_IN:
                if (zoomFramesPerPixel > 1)
                {
                    zoomFramesPerPixel = (float) Math.floor(zoomFramesPerPixel - 1);
                }
                else if (zoomFramesPerPixel > 0.1f)
                {
                    zoomFramesPerPixel = ((float) Math.floor(zoomFramesPerPixel * 10 - 1)) / 10.0f;
                }
                else if (zoomFramesPerPixel > 0.01)
                {
                    System.out.println(100);
                    zoomFramesPerPixel = ((float) Math.floor(zoomFramesPerPixel * 100 - 1)) / 100.0f;
                }
                else
                {
                    break;
                }
                updateWaveformViewBounds();
                effectTimelinePanel.setZoom(zoomFramesPerPixel);
                break;
            case Constants.TRANSLATION_ZOOM_OUT:
                if (zoomFramesPerPixel < 0.1f)
                {
                    zoomFramesPerPixel = ((float) Math.floor(zoomFramesPerPixel * 100 + 1)) / 100.0f;
                }
                else if (zoomFramesPerPixel < 1)
                {
                    zoomFramesPerPixel = ((float) Math.floor(zoomFramesPerPixel * 10 + 1)) / 10.0f;
                }
                else
                {
                    zoomFramesPerPixel = (float) Math.floor(zoomFramesPerPixel + 1);
                }
                updateWaveformViewBounds();
                effectTimelinePanel.setZoom(zoomFramesPerPixel);
                break;
        }
    }

    @Override
    public void onShowSavedChanged(boolean saved)
    {

    }

    public void onFrameChanged(int frame)
    {
        int visibleFramesEnd = Math.round(waveformPanel.getWidthWithOffset() * zoomFramesPerPixel) + visibleFramesStart;
        if (frame > visibleFramesEnd)
        {
            effectTimelinePanel.setViewStart(frame);
        }

        waveformPanel.setCurrentPosition(Math.round(frame / (float) TARGET_FPS * 1000));
        effectTimelinePanel.setCurrentPosition(frame);
    }

    @Override
    public void onScrub(int millisecondPosition)
    {
        showRenderer.setPlaybackPosition(millisecondPosition * 1000L);
    }

    @Override
    public void onWaveformRendered(int millisecondDuration)
    {
        zoomToWindow();
    }

    private void createUIComponents()
    {
        LEDRendererPanel = new LEDRendererPanel(this);
        waveformPanel = new WaveformPanel(this);
        toolbar = new MainWindowToolbar(this);
        effectTimelinePanel = new EffectTimelinePanel(this);
    }

    @Override
    public void trackOffsetChanged(int offsetLeft, int offsetRight)
    {
        this.leftOffset = offsetLeft;
        waveformPanel.setOffset(offsetLeft, offsetRight);
    }

    @Override
    public void trackScrollChanged(int startFrame)
    {
        this.visibleFramesStart = startFrame;
        updateWaveformViewBounds();
    }
}
