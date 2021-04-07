package me.zachcheatham.pixelshow;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import org.apache.log4j.Logger;

public class Translations
{
    private static final ConfigParseOptions parseOptions = ConfigParseOptions.defaults().setAllowMissing(false);
    private static final Config config = ConfigFactory.parseResources(String.format("translation/%s.properties", Constants.LOCALE.toLanguageTag()), parseOptions);

    private static final Logger LOG = Logger.getLogger(Translations.class.getSimpleName());

    public static String get(String key)
    {
        try
        {
            return config.getString(key);
        }
        catch (ConfigException.Missing e)
        {
            LOG.error(String.format("Translation not found: %s", key));
            return key;
        }
    }

    public static String getFormatted(String key, Object... args)
    {
        try
        {
            return String.format(config.getString(key), args);
        }
        catch (ConfigException.Missing e)
        {
            LOG.error(String.format("Translation not found: %s", key));
            return key;
        }
    }
}
