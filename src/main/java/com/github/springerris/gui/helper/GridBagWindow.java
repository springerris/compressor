package com.github.springerris.gui.helper;

import com.github.springerris.gui.Window;
import com.github.springerris.gui.WindowContext;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/** Класс, наследующий Window, с реализацией специфического расположения элементов */
public abstract class GridBagWindow extends Window {

    public GridBagWindow(
            @NotNull WindowContext ctx,
            @NotNull String title,
            int initialWidth,
            int initialHeight
    ) {
        super(ctx, title, initialWidth, initialHeight);
    }


    @Override
    protected void setupLayout() {
        final Container contentPane = this.getContentPane();
        // contentPane.setBackground(new Color(0xFFE8B3));
        contentPane.setLayout(new GridBagLayout());
    }

    protected void addElement(int gridx, int gridy, int width, @NotNull Component comp) {
        this.addElement(gridx, gridy, width, comp, GridBagConstraints.VERTICAL);
    }

    protected void addElement(int gridx, int gridy, int width, @NotNull Component comp, int fill) {
        this.addElement(gridx, gridy, width, 1, comp, fill);
    }

    protected void addElement(int gridx, int gridy, int width, int height, @NotNull Component comp, int fill) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = fill;
        c.weightx = 0.1d;
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = width;
        c.gridheight = height;
        this.getContentPane().add(comp, c);
    }

}
