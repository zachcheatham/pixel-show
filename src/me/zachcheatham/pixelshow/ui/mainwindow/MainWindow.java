package me.zachcheatham.pixelshow.ui.mainwindow;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import me.zachcheatham.pixelshow.Constants;
import me.zachcheatham.pixelshow.show.Show;
import me.zachcheatham.pixelshow.show.Renderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import static me.zachcheatham.pixelshow.Constants.TARGET_FPS;

public class MainWindow extends JFrame implements ActionListener, Show.ShowListener, WaveformPanel.WaveformEventListener
{
    private Renderer showRenderer;

    private JPanel rootPanel;
    private LEDRendererPanel LEDRendererPanel;
    private WaveformPanel waveformPanel;
    private MainWindowToolbar toolbar;

    private Show show;
    private float zoomFramesPerPixel = 0.0f;
    private int visibleFramesStart = 0;

    public MainWindow()
    {
        super(Constants.TRANSLATION_APP_TITLE);

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

        setShow(new Show(this));
    }

    private void setShow(Show show)
    {
        /*if (this.showRenderer != null)
            this.showRenderer.cleanup()*/
        this.show = show;

        setTitle(String.format("%s - %s", Constants.TRANSLATION_APP_TITLE, show.getTitle()));
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
        zoomFramesPerPixel = showRenderer.getTotalFrames() / (float) waveformPanel.getWidth();
        visibleFramesStart = 0;
        updateWaveformViewBounds();
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
        }
    }

    @Override
    public void onShowSavedChanged(boolean saved)
    {

    }

    @Override
    public void onShowRenamed(String title)
    {

    }

    @Override
    public void onStripSizeUpdated(int size)
    {

    }

    @Override
    public void onLayerRemoved(int i)
    {

    }

    public void onFrameChanged(int frame)
    {
        int visibleFramesEnd = Math.round(waveformPanel.getWidth() * zoomFramesPerPixel) + visibleFramesStart;
        if (frame > visibleFramesEnd)
        {
            visibleFramesStart = frame;
            updateWaveformViewBounds();
        }

        waveformPanel.setCurrentPosition(Math.round(frame / (float) TARGET_FPS * 1000));
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
    }
}
