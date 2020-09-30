package me.zachcheatham.pixelshow.ui.mainwindow;

import me.zachcheatham.pixelshow.Constants;
import me.zachcheatham.pixelshow.file.ShowIO;
import me.zachcheatham.pixelshow.show.Renderer;
import me.zachcheatham.pixelshow.show.Show;
import me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline.EffectTimelinePanel;
import org.apache.commons.io.FilenameUtils;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static me.zachcheatham.pixelshow.Constants.TARGET_FPS;

public class MainWindow extends JFrame implements ActionListener, Show.ShowListener, WaveformPanel.WaveformEventListener, EffectTimelinePanel.TimelinePanelListener
{
    private Renderer showRenderer;
    private int lastPaintFrame = 0;

    private JPanel rootPanel;
    private PreviewPanel PreviewPanel;
    private WaveformPanel waveformPanel;
    private MainWindowToolbar toolbar;
    private EffectTimelinePanel effectTimelinePanel;

    private Show show;
    private float zoomFramesPerPixel = 0.0f;
    private int visibleFramesStart = 0;

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

        setShow(show);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                int currentFrame = showRenderer.getCurrentFrame();
                if (currentFrame != lastPaintFrame)
                {
                    int visibleFramesEnd = Math.round(waveformPanel.getWidthWithOffset() * zoomFramesPerPixel) + visibleFramesStart;

                    if (currentFrame > visibleFramesEnd)
                        effectTimelinePanel.setViewStart(currentFrame);

                    waveformPanel.setCurrentPosition(Math.round(currentFrame / (float) TARGET_FPS * 1000));
                    effectTimelinePanel.setCurrentPosition(currentFrame);

                    waveformPanel.repaint();
                    effectTimelinePanel.repaintPosition();

                    lastPaintFrame = currentFrame;
                }

                PreviewPanel.repaint();
            }
        }, 100, 33);
    }

    private void setShow(Show show)
    {
        /*if (this.showRenderer != null)
            this.showRenderer.cleanup()*/
        this.show = show;
        setTitle(String.format("%s - %s", Constants.TRANSLATION_APP_TITLE, show.getTitle()));

        effectTimelinePanel.setShow(show);

        System.out.println(show.getAudioLocation());

        if (show.getAudioLocation() != null)
        {
            try
            {
                setAudio(new File(show.getAudioLocation()));
            }
            catch (IOException | UnsupportedAudioFileException e)
            {
                e.printStackTrace();
            }
        }
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

    private void saveShow(Component source)
    {
        if (show.getFileLocation() == null)
        {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(String.format(Constants.TRANSLATION_SAVE_SHOW_TITLE, show.getTitle()));
            fileChooser.setFileFilter(new FileNameExtensionFilter(Constants.TRANSLATION_JSON_SHOW_FILE, Constants.SAVE_EXTENSION));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            if (fileChooser.showSaveDialog(source) == JFileChooser.APPROVE_OPTION)
            {
                String path = fileChooser.getSelectedFile().getAbsolutePath();

                if (!FilenameUtils.getExtension(path).equals(Constants.SAVE_EXTENSION))
                    path += String.format(".%s", Constants.SAVE_EXTENSION);

                show.setFileLocation(path);
            }
            else
                return;
        }

        try
        {
            ShowIO.writeShow(show);
            show.setUnsaved(false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void openShow(Component source)
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Constants.TRANSLATION_OPEN_SHOW);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter(Constants.TRANSLATION_JSON_SHOW_FILE, Constants.SAVE_EXTENSION));
        if (fileChooser.showOpenDialog(source) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                Show show = ShowIO.readShow(fileChooser.getSelectedFile(), this);
                setShow(show);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void openMP3Chooser(Component source)
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Constants.TRANSLATION_OPEN_WAV);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter(Constants.WAV_AUDIO, "wav"));
        if (fileChooser.showOpenDialog(source) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                setAudio(fileChooser.getSelectedFile());
                show.setAudioLocation(fileChooser.getSelectedFile().getAbsolutePath());
            }
            catch (IOException | UnsupportedAudioFileException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void setAudio(File audioFile) throws IOException, UnsupportedAudioFileException
    {
        waveformPanel.setAudio(audioFile);
        showRenderer.setAudio(audioFile);
        effectTimelinePanel.setTotalFrames(showRenderer.getTotalFrames());
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case Constants.TRANSLATION_OPEN_SHOW:
                openShow((Component) e.getSource());
                break;
            case Constants.TRANSLATION_SAVE_SHOW:
                saveShow((Component) e.getSource());
                break;
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
            case Constants.TRANSLATION_ADD_LAYER:
                show.createLayer("New Layer");
                break;
        }
    }

    @Override
    public void onShowSavedChanged(boolean saved)
    {

    }

    @Override
    public void onLayerAdded(int position)
    {
        effectTimelinePanel.updateViewsForShow();
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
        PreviewPanel = new PreviewPanel(this);
        waveformPanel = new WaveformPanel(this);
        toolbar = new MainWindowToolbar(this);
        effectTimelinePanel = new EffectTimelinePanel(this);
    }

    @Override
    public void trackOffsetChanged(int offsetLeft, int offsetRight)
    {
        waveformPanel.setOffset(offsetLeft, offsetRight);
    }

    @Override
    public void trackScrollChanged(int startFrame)
    {
        this.visibleFramesStart = startFrame;
        updateWaveformViewBounds();
    }
}
