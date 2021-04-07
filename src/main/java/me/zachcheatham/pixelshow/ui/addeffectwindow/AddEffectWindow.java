package me.zachcheatham.pixelshow.ui.addeffectwindow;

import me.zachcheatham.pixelshow.Translations;
import me.zachcheatham.pixelshow.show.effect.Effect;
import me.zachcheatham.pixelshow.show.effect.EffectProperty;
import me.zachcheatham.pixelshow.show.effect.SolidColorEffect;
import me.zachcheatham.pixelshow.ui.ColorTextField;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static me.zachcheatham.pixelshow.Constants.TRANSLATION_ACTION_ADD_EFFECT;

public class AddEffectWindow extends JFrame implements ActionListener
{
    private static final Map<String, Class<?>> effects = Map.of(
            Translations.get(SolidColorEffect.NAME), SolidColorEffect.class);

    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private final int startFrame;

    private final JComboBox<String> effectType = new JComboBox<>(effects.keySet().toArray(new String[]{}));
    private final JPanel rootPanel = new JPanel();
    private final JPanel propertiesPanel = new JPanel();
    private final EffectCreatedCallback callback;
    private Effect currentEffect = null;

    public AddEffectWindow(int startFrame, EffectCreatedCallback callback)
    {
        super(Translations.get(TRANSLATION_ACTION_ADD_EFFECT));
        this.startFrame = startFrame;
        this.callback = callback;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(600, 500));

        rootPanel.setLayout(new BorderLayout());
        setContentPane(rootPanel);

        propertiesPanel.setLayout(
                new MigLayout(
                        "fillx",
                        "[right][left,grow,fill]",
                        ""));

        rootPanel.add(propertiesPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));

        JButton createButton = new JButton("Create");
        createButton.addActionListener(this);
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(createButton);

        rootPanel.add(buttonsPanel, BorderLayout.PAGE_END);

        effectType.addActionListener(actionEvent -> loadEffectProperties());

        loadEffectProperties();
        pack();
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private void loadEffectProperties()
    {
        try
        {
            if (currentEffect == null || effects.get(effectType.getSelectedItem()) != currentEffect.getClass())
            {
                currentEffect = (Effect) effects.get(effectType.getSelectedItem()).getConstructor(int.class).newInstance(startFrame);
            }
            else return;
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            e.printStackTrace();
            return;
        }

        propertiesPanel.removeAll();

        JLabel typeLabel = new JLabel(Translations.get("effect.type"));
        propertiesPanel.add(typeLabel);
        propertiesPanel.add(effectType, "wrap");

        String propNameTranslation;
        JComponent component;

        for (EffectProperty<?> property : currentEffect.getProperties())
        {
            if (property.getType() == Color.class)
            {
                propNameTranslation = "effect.property.color";
                component = new ColorTextField();
            }
            else
            {
                throw new IllegalArgumentException("Unsupported property: " + property.getType().getSimpleName());
            }

            JLabel label = new JLabel(Translations.get(propNameTranslation));
            propertiesPanel.add(label);
            label.setLabelFor(component);
            propertiesPanel.add(component, "wrap");
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (currentEffect != null) {

            EffectProperty<?>[] properties = currentEffect.getProperties();
            for (int i = 0; i < properties.length; i++)
            {
                if (properties[i].getType() == Color.class)
                {
                    EffectProperty<Color> property = (EffectProperty<Color>) properties[i];
                    ColorTextField field = (ColorTextField) propertiesPanel.getComponent(i*2+3);
                    property.setValue(field.getColor());
                }
            }

            callback.effectCreated(currentEffect);
        }
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public interface EffectCreatedCallback
    {
        void effectCreated(Effect effect);
    }
}
