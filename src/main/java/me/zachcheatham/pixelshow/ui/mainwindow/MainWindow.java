package me.zachcheatham.pixelshow.ui.mainwindow;

import me.zachcheatham.pixelshow.Constants;
import me.zachcheatham.pixelshow.Translations;
import me.zachcheatham.pixelshow.file.ShowIO;
import me.zachcheatham.pixelshow.show.Renderer;
import me.zachcheatham.pixelshow.show.Show;
import me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline.EffectTimelinePanel;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static me.zachcheatham.pixelshow.Constants.*;

public class MainWindow extends JFrame implements ActionListener, Show.ShowListener, WaveformPanel.WaveformEventListener, EffectTimelinePanel.TimelinePanelListener
{
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private TimelineBounds timelineBounds;

    private Renderer showRenderer;
    private int lastPaintFrame = 0;

    private JPanel rootPanel;
    private PreviewPanel PreviewPanel;
    private WaveformPanel waveformPanel;
    private MainWindowToolbar toolbar;
    private EffectTimelinePanel effectTimelinePanel;

    private Show show;
    private int visibleFramesStart = 0;

    public MainWindow()
    {
        super(Translations.get(TRANSLATION_APP_TITLE));

        if (timelineBounds == null) timelineBounds = new TimelineBounds();

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
                            Translations.get(TRANSLATION_DIALOG_EXIT_NO_SAVE),
                            Translations.get(TRANSLATION_DIALOG_EXIT_NO_SAVE_TITLE),
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
            LOG.fatal("Unable to open audio line.", e);

            JOptionPane.showMessageDialog(this,
                    Translations.get(TRANSLATION_DIALOG_ERROR_AUDIO_LINE),
                    Translations.get(TRANSLATION_DIALOG_ERROR_AUDIO_LINE_TITLE),
                    JOptionPane.ERROR_MESSAGE);

            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            return;
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
                    int visibleFramesEnd = Math.round(waveformPanel.getWidthWithOffset() * timelineBounds.framesPerPixel) + visibleFramesStart;

                    if (currentFrame > visibleFramesEnd)
                        effectTimelinePanel.setViewStart(currentFrame);

                    waveformPanel.setCurrentPosition(Math.round(currentFrame / (float) TARGET_FPS * 1000));
                    timelineBounds.currentFrame = currentFrame;

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
        LOG.info(String.format("Show opened: %s", show.getTitle()));

        this.show = show;
        setTitle(String.format("%s - %s", Translations.get(TRANSLATION_APP_TITLE), show.getTitle()));

        effectTimelinePanel.setShow(show);

        if (show.getAudioLocation() != null)
        {
            try
            {
                setAudio(new File(show.getAudioLocation()));
            }
            catch (IOException e)
            {
                LOG.error("Unable to open audio file.", e);

                JOptionPane.showMessageDialog(this,
                        Translations.getFormatted(TRANSLATION_DIALOG_ERROR_AUDIO_FILE, show.getAudioLocation()),
                        Translations.get(TRANSLATION_DIALOG_ERROR_AUDIO_FILE_TITLE),
                        JOptionPane.WARNING_MESSAGE);

                show.setAudioLocation(null);
                openMP3Chooser(null);
            }
            catch (UnsupportedAudioFileException e)
            {
                LOG.error(String.format("Unsupported audio file %s", show.getAudioLocation()), e);

                JOptionPane.showMessageDialog(this,
                        Translations.getFormatted(TRANSLATION_DIALOG_ERROR_UNSUPPORTED_AUDIO_FILE, FilenameUtils.getBaseName(show.getAudioLocation())),
                        Translations.get(TRANSLATION_DIALOG_ERROR_UNSUPPORTED_AUDIO_FILE_TITLE),
                        JOptionPane.WARNING_MESSAGE);

                show.setAudioLocation(null);
                openMP3Chooser(null);
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
        timelineBounds.setFramesPerPixel(timelineBounds.getTotalFrames() / (float) waveformPanel.getWidthWithOffset());
        visibleFramesStart = 0;
        updateWaveformViewBounds();
        effectTimelinePanel.updateBounds();
    }

    private void updateWaveformViewBounds()
    {
        float startMS = (visibleFramesStart / (float) TARGET_FPS) * 1000.0f;
        float msPerPixel = (timelineBounds.getFramesPerPixel() / (float) TARGET_FPS) * 1000.0f;
        waveformPanel.setViewBounds((int) startMS, msPerPixel);
    }

    private void saveShow(Component source)
    {
        if (show.getFileLocation() == null)
        {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(Translations.getFormatted(TRANSLATION_DIALOG_SAVE_SHOW_TITLE, show.getTitle()));
            fileChooser.setFileFilter(new FileNameExtensionFilter(
                    Translations.get(TRANSLATION_FILE_TYPE_SHW), Constants.SAVE_EXTENSION));
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
            LOG.error("Unable to save show.", e);

            JOptionPane.showMessageDialog(this,
                    Translations.getFormatted(TRANSLATION_DIALOG_ERROR_FILE_NOT_FOUND, show.getFileLocation()),
                    Translations.get(TRANSLATION_DIALOG_ERROR_FILE_NOT_FOUND_TITLE),
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openShow(Component source)
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Translations.get(TRANSLATION_ACTION_OPEN_SHOW));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(
                new FileNameExtensionFilter(Translations.get(TRANSLATION_FILE_TYPE_SHW),
                Constants.SAVE_EXTENSION));
        if (fileChooser.showOpenDialog(source) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                Show show = ShowIO.readShow(fileChooser.getSelectedFile(), this);
                setShow(show);
            }
            catch (FileNotFoundException e)
            {
                LOG.error("File not found while opening show", e);

                JOptionPane.showMessageDialog(this,
                        Translations.getFormatted(TRANSLATION_DIALOG_ERROR_FILE_NOT_FOUND, fileChooser.getSelectedFile().getAbsolutePath()),
                        Translations.get(TRANSLATION_DIALOG_ERROR_FILE_NOT_FOUND_TITLE),
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void openMP3Chooser(Component source)
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(Translations.get(TRANSLATION_ACTION_OPEN_SHOW));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                Translations.get(TRANSLATION_FILE_TYPE_WAV), "wav"));
        if (fileChooser.showOpenDialog(source) == JFileChooser.APPROVE_OPTION)
        {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            try
            {
                setAudio(selectedFile);
                show.setAudioLocation(filePath);
            }
            catch (UnsupportedAudioFileException e)
            {
                LOG.error(String.format("Unsupported file %s", filePath), e);

                JOptionPane.showMessageDialog(this,
                        Translations.getFormatted(TRANSLATION_DIALOG_ERROR_UNSUPPORTED_AUDIO_FILE, FilenameUtils.getBaseName(filePath)),
                        Translations.get(TRANSLATION_DIALOG_ERROR_UNSUPPORTED_AUDIO_FILE_TITLE),
                        JOptionPane.WARNING_MESSAGE);

                openMP3Chooser(source);
            }
            catch (IOException e)
            {
                LOG.error(String.format("Unable to open audio file %s", filePath), e);

                JOptionPane.showMessageDialog(this,
                        Translations.getFormatted(TRANSLATION_DIALOG_ERROR_AUDIO_FILE, filePath),
                        Translations.get(TRANSLATION_DIALOG_ERROR_AUDIO_FILE_TITLE),
                        JOptionPane.WARNING_MESSAGE);

                show.setAudioLocation(null);
                openMP3Chooser(null);
            }
        }
    }

    private void setAudio(File audioFile) throws IOException, UnsupportedAudioFileException
    {
        LOG.info(String.format("Opening Audio: %s", audioFile.getName()));

        waveformPanel.setAudio(audioFile);
        showRenderer.setAudio(audioFile);
        effectTimelinePanel.updateBounds();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case TRANSLATION_ACTION_OPEN_SHOW:
                openShow((Component) e.getSource());
                break;
            case TRANSLATION_ACTION_SAVE_SHOW:
                saveShow((Component) e.getSource());
                break;
            case TRANSLATION_MENU_FILE_EXIT:
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                break;
            case TRANSLATION_MENU_MUSIC_OPEN:
                openMP3Chooser((Component) e.getSource());
                break;
            case TRANSLATION_ACTION_PLAY:
                showRenderer.play();
                break;
            case TRANSLATION_ACTION_PAUSE:
                showRenderer.pause();
                break;
            case TRANSLATION_ACTION_ZOOM_FIT:
                zoomToWindow();
                break;
            case TRANSLATION_ACTION_ZOOM_IN:
                if (timelineBounds.getFramesPerPixel() > 1)
                {
                    timelineBounds.setFramesPerPixel((float) Math.floor(timelineBounds.getFramesPerPixel() - 1));
                }
                else if (timelineBounds.getFramesPerPixel() > 0.1f)
                {
                    timelineBounds.setFramesPerPixel(((float) Math.floor(timelineBounds.getFramesPerPixel() * 10 - 1)) / 10.0f);
                }
                else if (timelineBounds.getFramesPerPixel() > 0.01)
                {
                    timelineBounds.setFramesPerPixel(((float) Math.floor(timelineBounds.getFramesPerPixel() * 100 - 1)) / 100.0f);
                }
                else
                {
                    break;
                }

                updateWaveformViewBounds();
                effectTimelinePanel.updateBounds();

                break;
            case TRANSLATION_ACTION_ZOOM_OUT:
                if (timelineBounds.getFramesPerPixel() < 0.1f)
                {
                    timelineBounds.setFramesPerPixel(((float) Math.floor(timelineBounds.getFramesPerPixel() * 100 + 1)) / 100.0f);
                }
                else if (timelineBounds.getFramesPerPixel() < 1)
                {
                    timelineBounds.setFramesPerPixel(((float) Math.floor(timelineBounds.getFramesPerPixel() * 10 + 1)) / 10.0f);
                }
                else
                {
                    timelineBounds.setFramesPerPixel((float) Math.floor(timelineBounds.getFramesPerPixel() + 1));
                }
                updateWaveformViewBounds();
                effectTimelinePanel.updateBounds();
                break;
            case TRANSLATION_ACTION_ADD_LAYER:
                show.createLayer(Translations.get(TRANSLATION_LAYER_NEW));
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
        effectTimelinePanel.layoutLayers();
    }

    @Override
    public void onScrub(int millisecondPosition)
    {
        showRenderer.setPlaybackPosition(millisecondPosition * 1000L);
    }

    @Override
    public void onWaveformRendered(int millisecondDuration)
    {
        show.setFrameLength(showRenderer.getTotalFrames(), false);
        timelineBounds.setTotalFrames(showRenderer.getTotalFrames());
        zoomToWindow();
    }

    private void createUIComponents()
    {
        if (timelineBounds == null) timelineBounds = new TimelineBounds();

        System.out.println(timelineBounds.hashCode());

        PreviewPanel = new PreviewPanel(this);
        waveformPanel = new WaveformPanel(this);
        toolbar = new MainWindowToolbar(this);

        System.out.println("createUIComponents.");
        System.out.println(this.getClass());

        effectTimelinePanel = new EffectTimelinePanel(this, timelineBounds);
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
