package me.zachcheatham.pixelshow.show.effect;

import me.zachcheatham.pixelshow.Constants;

import java.awt.*;
import java.util.Map;

import static java.util.Map.entry;
import static me.zachcheatham.pixelshow.Constants.TRANSLATION_EFFECT_SOLID;

public class SolidColorEffect extends Effect
{
    public static final String NAME = TRANSLATION_EFFECT_SOLID;
    public static final String TYPE_ID = "solid";

    private final EffectProperty<Color> color = new EffectProperty<>(Constants.TRANSLATION_PROPERTY_COLOR, new Color(255, 255, 255));

    public static final Map<String, Class> PROPERTIES = Map.ofEntries(
            entry(Constants.TRANSLATION_PROPERTY_COLOR, Color.class)
    );

    public SolidColorEffect(int startPosition)
    {
        super(startPosition);
    }

    @Override
    public String getTypeId() {
        return TYPE_ID;
    }

    @Override
    public boolean hasFlexibleDuration()
    {
        return true;
    }

    @Override
    public Color getGUIColor()
    {
        return color.getValue();
    }

    @Override
    public EffectProperty<?>[] getProperties()
    {
        return new EffectProperty[]{color};
    }
}
