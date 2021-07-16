package me.zachcheatham.pixelshow.show;

import me.zachcheatham.pixelshow.show.effect.Effect;

public class LightsRenderer
{
    public static int[] renderLayers(Show show, double frameRate, int frame)
    {
        final int ledLength = show.getLEDLength();

        int[] stripValues = new int[ledLength];

        for (int i = show.getLayerCount() - 1; i > -1; i--) {
            Layer l = show.getLayerAt(i);
            Effect e = l.getEffectAtFrame(frame);
            if (e != null)
            {
                int[] values = e.render(frameRate, ledLength, frame);
                stripValues = values;
            }
        }

        return stripValues;
    }
}
