// Класс, наследующий Window, с реализацией специфического расположения элементов

package com.github.springerris;

import com.github.springerris.Window;
import com.github.springerris.WindowContext;

import javax.swing.*;
import java.awt.*;

public abstract class GridBagWindow extends Window {

    public GridBagWindow(WindowContext ctx,  String title, int initialWidth, int initialHeight) {
        super(ctx, title, initialWidth, initialHeight);
    }

    @Override
    protected void setupLayout() {
        final Container contentPane = this.getContentPane();
        contentPane.setBackground(new Color(0xFFE8B3));
        contentPane.setLayout(new GridBagLayout());
    }

    protected void addElement(int gridx, int gridy, int width,  JComponent comp) {
        this.addElement(gridx, gridy, width, comp, GridBagConstraints.VERTICAL);
    }

    protected void addElement(int gridx, int gridy, int width,  JComponent comp, int fill) {
        this.addElement(gridx, gridy, width, 1, comp, fill);
    }

    protected void addElement(int gridx, int gridy, int width, int height,  JComponent comp, int fill) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = fill;
        c.weightx = 0.5d;
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = width;
        c.gridheight = height;
        this.getContentPane().add(comp, c);
    }
}
