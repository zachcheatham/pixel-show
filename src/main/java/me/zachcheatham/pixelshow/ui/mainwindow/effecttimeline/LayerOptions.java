package me.zachcheatham.pixelshow.ui.mainwindow.effecttimeline;

import me.zachcheatham.pixelshow.Translations;
import me.zachcheatham.pixelshow.show.Layer;
import me.zachcheatham.pixelshow.ui.PopupMouseAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static me.zachcheatham.pixelshow.Constants.*;

public class LayerOptions extends JPanel implements ActionListener
{
    private final Layer layer;

    public LayerOptions(Layer layer)
    {
        this.layer = layer;
        addMouseListener(new PopupMouseAdapter(e -> new LayerOptionsPopupMenu()));
    }

    private void promptRename()
    {
        String name = JOptionPane.showInputDialog(
                null,
                Translations.get(TRANSLATION_DIALOG_RENAME_LAYER),
                Translations.getFormatted(TRANSLATION_DIALOG_RENAME_LAYER_TITLE, layer.getName()),
                JOptionPane.PLAIN_MESSAGE);

        if (name != null)
        {
            layer.setName(name);
            repaint();
        }
    }

    private void promptDelete()
    {
        JOptionPane.showConfirmDialog(null,
                Translations.getFormatted(TRANSLATION_DIALOG_DELETE_LAYER, layer.getName()),
                Translations.get(TRANSLATION_DIALOG_DELETE_LAYER_TITLE),
                JOptionPane.YES_NO_OPTION);

        layer.getShow().deleteLayer(layer);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(UIManager.getColor("textText"));
        g2.fillRect(0, 0, 20, getHeight());
        g2.drawLine(20, getHeight() - 1, getWidth(), getHeight() - 1);

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString(String.valueOf(layer.getName()), 25, 15);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        switch (actionEvent.getActionCommand())
        {
            case TRANSLATION_ACTION_RENAME:
                promptRename();
                break;
            case TRANSLATION_ACTION_DELETE:
                promptDelete();
                break;
        }
    }

    public class LayerOptionsPopupMenu extends JPopupMenu
    {
        public LayerOptionsPopupMenu()
        {
            JMenuItem rename = new JMenuItem(Translations.get(TRANSLATION_ACTION_RENAME));
            rename.addActionListener(LayerOptions.this);
            rename.setActionCommand(TRANSLATION_ACTION_RENAME);
            add(rename);

            JMenuItem delete = new JMenuItem(Translations.get(TRANSLATION_ACTION_DELETE));
            delete.addActionListener(LayerOptions.this);
            delete.setActionCommand(TRANSLATION_ACTION_DELETE);
            add(delete);
        }
    }
}
