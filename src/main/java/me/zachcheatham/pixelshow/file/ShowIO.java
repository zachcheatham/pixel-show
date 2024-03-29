package me.zachcheatham.pixelshow.file;

import me.zachcheatham.pixelshow.show.Layer;
import me.zachcheatham.pixelshow.show.Show;
import me.zachcheatham.pixelshow.show.effect.Effect;
import me.zachcheatham.pixelshow.show.exception.InvalidEffectPositionException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

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
                JSONObject effectObject = effect.toJSON();
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

        show.setFileLocation(file.getAbsolutePath());

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
                    JSONArray effectsArray = layerObject.getJSONArray("effects");
                    for (int c = 0; c < effectsArray.length(); c++)
                    {
                        JSONObject effectObject = effectsArray.getJSONObject(c);
                        Effect e = Effect.fromJson(effectObject);
                        try
                        {
                            layer.addEffect(e);
                        }
                        catch (InvalidEffectPositionException invalidEffectPositionException)
                        {
                            invalidEffectPositionException.printStackTrace();
                        }
                    }
                }

                show.addLayer(layer);
            }
        }

        show.setReading(false);

        return show;
    }
}
