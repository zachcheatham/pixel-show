package me.zachcheatham.pixelshow.file;

import me.zachcheatham.pixelshow.show.Layer;
import me.zachcheatham.pixelshow.show.Show;
import me.zachcheatham.pixelshow.show.effect.Effect;
import me.zachcheatham.pixelshow.show.effect.EffectProperty;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.io.*;

public class ShowIO
{
    public final static short FORMAT_VERSION = 1;

    public static void writeShow(Show show) throws IOException
    {
        JSONArray layersArray = new JSONArray();

        for (int i = 0; i < show.getLayerCount(); i++)
        {
            Layer layer = show.getLayerAt(i);

            JSONObject layerObject = new JSONObject();
            layerObject.put("name", layer.getName());

            JSONArray layerEffects = new JSONArray();
            for (Effect effect : layer.getEffects())
            {
                JSONObject effectObject = new JSONObject();
                effectObject.put("type", effect.getTypeId());
                effectObject.put("start", effect.getStartFrame());
                effectObject.put("duration", effect.getDuration());

                JSONArray properties = new JSONArray();

                for (EffectProperty<?> effectProperty : effect.getProperties())
                {
                    JSONObject effectPropertyObject = new JSONObject();
                    effectPropertyObject.put("type", effectProperty.getType().getSimpleName());
                    switch (effectProperty.getType().getSimpleName()) {
                        case "Color":
                            Color typedEffect = (Color) effectProperty.getValue();
                            effectPropertyObject.put("r", typedEffect.getRed());
                            effectPropertyObject.put("g", typedEffect.getGreen());
                            effectPropertyObject.put("b", typedEffect.getBlue());
                            break;
                        default:
                            throw new InvalidClassException("Unable to serialize type: " + effectProperty.getType().getName());

                    }
                    properties.put(effectPropertyObject);
                }

                effectObject.put("props", properties);

                layerEffects.put(effectObject);
            }
            layerObject.put("effects", layerEffects);

            layersArray.put(layerObject);
        }

        JSONObject fileObject = new JSONObject()
                .put("format-version", FORMAT_VERSION)
                .put("title", show.getTitle())
                .put("audio", show.getAudioLocation())
                .put("strip-size", show.getLEDLength())
                .put("layers", layersArray);

        File outputFile = new File(show.getFileLocation());
        FileWriter fileWriter = new FileWriter(outputFile, false);
        fileObject.write(fileWriter);
        fileWriter.close();
    }

    public static Show readShow(File file, Show.ShowListener showListener) throws FileNotFoundException
    {
        FileReader fileReader = new FileReader(file);

        JSONTokener tokener = new JSONTokener(fileReader);
        JSONObject fileObject = new JSONObject(tokener);

        Show show = new Show(showListener);
        show.setReading(true);

        if (fileObject.has("title"))
            show.setTitle(fileObject.getString("title"));

        if (fileObject.has("audio"))
            show.setAudioLocation(fileObject.getString("audio"));

        if (fileObject.has("strip-size"))
            show.setLightStripSize(fileObject.getInt("strip-size"));

        if (fileObject.has("layers"))
        {
            JSONArray layersArray = fileObject.getJSONArray("layers");

            for (int i = 0; i < layersArray.length(); i++)
            {
                JSONObject layerObject = layersArray.getJSONObject(i);
                Layer layer = new Layer(show);

                if (layerObject.has("name"))
                    layer.setName(layerObject.getString("name"));

                if (layerObject.has("effects"))
                {
                    JSONArray effectsArray = fileObject.getJSONArray("effects");
                    for (int c = 0; i < effectsArray.length(); i++)
                    {
                        JSONObject effectObject = effectsArray.getJSONObject(c);
                        Effect e = Effect.createFromType(
                                effectObject.getString("type"),
                                effectObject.getInt("start"));


                    }
                }

                show.addLayer(layer);
            }
        }

        show.setReading(false);

        return show;
    }
}
