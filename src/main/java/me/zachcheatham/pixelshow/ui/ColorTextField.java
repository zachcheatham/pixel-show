package me.zachcheatham.pixelshow.ui;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorTextField extends JTextField implements DocumentListener
{
    private static final Pattern hexPattern = Pattern.compile("#?([0-9a-fA-F]{6})");
    private static final Logger LOG = Logger.getLogger(ColorTextField.class.getSimpleName());

    private Color color = Color.WHITE;

    public ColorTextField()
    {
        getDocument().addDocumentListener(this);
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent)
    {
        updateColorFromText();
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent)
    {
        updateColorFromText();
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent)
    {
        updateColorFromText();
    }

    private void updateColorFromText()
    {
        Matcher matcher = hexPattern.matcher(getText());
        if (matcher.find())
        {
            try
            {
                color = Color.decode("#" + matcher.group(1));

                float[] hsv = new float[3];
                Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);

                if (hsv[2] < 0.7f)
                    setForeground(Color.WHITE);
                else
                    setBackground(Color.BLACK);
                LOG.info(hsv[2]);

                setBackground(color);
            }
            catch (Exception e)
            {
                LOG.error("Invalid Color", e);
            }
        }
    }

    public Color getColor()
    {
        return color;
    }
}
