package me.zachcheatham.pixelshow.show.effect;

import me.zachcheatham.pixelshow.Constants;
import org.json.JSONObject;

import java.awt.*;

public abstract class Effect
{
     private int startFrame;
     private int duration = 2 * Constants.TARGET_FPS; // Default to Two seconds
     private PositionUpdateListener positionUpdateListener = null;

     public Effect(int startFrame)
     {
          this.startFrame = startFrame;
     }

     public Effect(JSONObject jsonObject)
     {
          startFrame = jsonObject.getInt("startFrame");
     }

     public abstract String getTypeId();

     public void setStartFrame(int startPosition)
     {
          int oldPosition = this.startFrame;
          this.startFrame = startPosition;

          if (positionUpdateListener != null)
               positionUpdateListener.onEffectPositionChanged(this, oldPosition);
     }

     public int getStartFrame()
     {
          return startFrame;
     }

     public int getEndFrame()
     {
          return startFrame + getDuration();
     }

     public void setDuration(int duration)
     {
          if (hasFlexibleDuration())
               this.duration = duration;
          else
               throw new IllegalStateException("You are unable to set the duration of this type of effect.");
     }

     public int getDuration()
     {
          return duration;
     }

     public int getStopPosition()
     {
          return startFrame + duration;
     }

     public abstract boolean hasFlexibleDuration();

     public void setPositionUpdateListener(PositionUpdateListener listener)
     {
          this.positionUpdateListener = listener;
     }

     public Color getGUIColor()
     {
          return Color.WHITE;
     }

     public abstract EffectProperty<?>[] getProperties();

     public interface PositionUpdateListener
     {
          void onEffectPositionChanged(Effect effect, int oldStartPosition);
     }

     public static Effect fromJson(JSONObject jsonObject) {

          int startFrame = jsonObject.getInt("start");
          String type = jsonObject.getString("type");

          switch (type) {
               case "solid":
                    return new SolidColorEffect(jsonObject);
               default:Coming
                    throw new IllegalArgumentException("Invalid effect type: " + type);
          }
     }
}
