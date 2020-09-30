package me.zachcheatham.pixelshow;

import java.util.Locale;

public class Constants
{
    public final static String VERSION = "0.0.1-dev";
    public final static Locale LOCALE = Locale.US;

    public static final int TRACK_HEIGHT = 60;

    public static final int TARGET_FPS = 30;

    public static final String SAVE_EXTENSION = "pxlshw";

    public final static String TRANSLATION_APP_TITLE = "Pixel Show ALPHA";
    public final static String TRANSLATION_FILE = "File";
    public static final String TRANSLATION_OPEN_SHOW = "Open Show...";
    public static final String TRANSLATION_CLOSE_APP = "Exit";
    public static final String TRANSLATION_SAVE_SHOW = "Save Show";
    public static final String TRANSLATION_RENDER_SHOW = "Render to Pixel Code...";
    public static final String TRANSLATION_NEW_SHOW = "New Show";
    public static final String TRANSLATION_UNTITLED_SHOW = "Untitled";
    public static final String TRANSLATION_CONFIRM_EXIT_NO_SAVE = "This show has not been saved. Are you sure you want to exit?";
    public static final String TRANSLATION_CONFIRM_EXIT_NO_SAVE_TITLE = "Unsaved Show";
    public static final String TRANSLATION_SET_WAV = "Set WAV...";
    public static final String TRANSLATION_MUSIC = "Music";
    public static final String TRANSLATION_OPEN_WAV = "Open WAV Audio";
    public static final String WAV_AUDIO = "WAV Audio";
    public static final String TRANSLATION_PLAY = "Play";
    public static final String TRANSLATION_PAUSE = "Pause";
    public static final String TRANSLATION_LAYERS = "Layers";
    public static final String TRANSLATION_ZOOM_IN = "Zoom In";
    public static final String TRANSLATION_ZOOM_OUT = "Zoom Out";
    public static final String TRANSLATION_ZOOM_TO_WINDOW = "Zoom To Window";
    public static final String TRANSLATION_ADD_LAYER = "Add Layer";
    public static final String TRANSLATION_RENAME = "Rename";
    public static final String TRANSLATE_ENTER_NEW_NAME = "Enter new name";
    public static final String TRANSLATE_FORMAT_RENAME_LAYER = "Rename Layer \"%s\"";
    public static final String TRANSLATION_SAVE_SHOW_TITLE = "Save Show \"%s\" As...";
    public static final String TRANSLATION_JSON_SHOW_FILE = "Show File (*.pxlshw)";
    public static final String TRANSLATION_ERROR_AUDIO_LINE_TITLE = "Unable to Open Audio Line";
    public static final String TRANSLATION_ERROR_AUDIO_LINE = "Unable to open audio line for output. The application will now close.";
    public static final String TRANSLATION_ERROR_AUDIO_FILE_TITLE = "Unable to Open Audio File";
    public static final String TRANSLATION_ERROR_AUDIO_FILE = "There was an error while opening %s. Please select the WAV file again.";
    public static final String TRANSLATION_ERROR_UNSUPPORTED_AUDIO_FILE_TITLE = "Unsupported audio file.";
    public static final String TRANSLATION_ERROR_UNSUPPORTED_AUDIO_FILE = "The audio file %s is not supported. " +
            "This application currently only supports WAV files. Please choose another file...";
    public static final String TRANSLATION_ERROR_SAVING_FILE_TITLE = "Unable to Save Show";
    public static final String TRANSLATION_ERROR_SAVING_FILE = "Unable to save show to %s. Please make sure this location is available and has permission to be written to.";
    public static final String TRANSLATION_ERROR_FILE_NOT_FOUND_TITLE = "File Not Found";
    public static final String TRANSLATION_ERROR_FILE_NOT_FOUND = "Unable to open %s";
}
