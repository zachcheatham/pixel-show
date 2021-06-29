package me.zachcheatham.pixelshow;

import java.util.prefs.Preferences;

public class AppPreferences
{
    private static final String LAST_FILE = "LAST_FILE";

    public static final AppPreferences instance = new AppPreferences();

    private final Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

    public String getLastOpenedFile()
    {
        return prefs.get(LAST_FILE, null);
    }

    public void setLastOpenedFile(String path)
    {
        prefs.put(LAST_FILE, path);
    }
}
