package me.zachcheatham.pixelshow.show;

import me.zachcheatham.pixelshow.show.effect.Effect;
import me.zachcheatham.pixelshow.show.exception.InvalidEffectPositionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Layer implements Effect.PositionUpdateListener
{
    private String name = "Untitled Layer";

    private final TreeMap<Integer, Effect> effects = new TreeMap<>();
    private final Show show;

    public Layer(Show show)
    {
        this.show = show;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get all effects as array
     * @return All Effects
     */
    public Effect[] getEffects() {
        List<Effect> foundEffects = new ArrayList<>();
        for (Map.Entry<Integer, Effect> effect : effects.entrySet())
            foundEffects.add(effect.getValue());
        return foundEffects.toArray(new Effect[0]);
    }

    /**
     * Get effect at frame or null when frame contains no effect in this layer
     * @param frame Frame number to find effect at.
     * @return Effect at specified frame
     */
    public Effect getEffectAtFrame(int frame)
    {
        Map.Entry<Integer, Effect> floorEntry = effects.floorEntry(frame);
        if (floorEntry != null && floorEntry.getValue().getStopPosition() >= frame)
            return floorEntry.getValue();
        else
            return null;
    }

    /**
     * Get effects between frames or null when no effect exists in the range
     * @param startFrame First frame of range
     * @param stopFrame Last frame of range
     * @return Effects found in range or null if none.
     */
    public Effect[] getEffectsBetweenFrames(int startFrame, int stopFrame)
    {
        List<Effect> foundEffects = new ArrayList<>();
        for (Map.Entry<Integer, Effect> effect : effects.entrySet()) {


            if (effect.getValue().getStartFrame() <= stopFrame &&
                    effect.getValue().getStopPosition() >= startFrame)
                foundEffects.add(effect.getValue());
        }

        return foundEffects.toArray(new Effect[0]);
    }

    /**
     * Add an effect to the layer. Will throw exception if effect overlaps another.
     * @param effect Effect to be added.
     */
    public void addEffect(Effect effect) throws InvalidEffectPositionException
    {
        if (show.getFrameLength() != 0 && effect.getStopPosition() > show.getFrameLength())
        {
            throw new InvalidEffectPositionException("Show cannot end after show ends.");
        }
        else
        {
            Effect existingEffect = getEffectAtFrame(effect.getStartFrame());
            if (existingEffect == null)
            {
                effect.setPositionUpdateListener(this);
                effects.put(effect.getStartFrame(), effect);
            }
            else
            {
                throw new InvalidEffectPositionException("Attempted to add an overlapping effect to layer!");
            }
        }
    }

    /**
     * Get the frame length ending with the end of the last effect in layer or -1 when layer does not have any effects.
     * @return Ending frame
     */
    public int getFrameLength()
    {
        if (effects.size() > 0)
            return -1; // No effects in layer.
        else
            return effects.lastEntry().getValue().getStopPosition();
    }

    @Override
    public void onEffectPositionChanged(Effect effect, int oldStartPosition)
    {
        effects.remove(oldStartPosition);
        effects.put(effect.getStartFrame(), effect);
    }

    /**
     * Access to show object this layer belongs to.
     * @return Show
     */
    public Show getShow()
    {
        return show;
    }
}
