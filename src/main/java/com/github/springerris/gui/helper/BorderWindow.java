package com.github.springerris.gui.helper;

import com.github.springerris.gui.Window;
import com.github.springerris.gui.WindowContext;

import java.awt.*;

public abstract class BorderWindow extends Window {

    public BorderWindow(WindowContext ctx, String title, int initialWidth, int initialHeight) {
        super(ctx, title, initialWidth, initialHeight);
    }

    @Override
    protected void setupLayout() {
        final Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
    }

    protected void addElement(Component component, String placement) {
        this.getContentPane().add(component, placement);
    }

    protected void addElement(Component component) {
        this.addElement(component, BorderLayout.CENTER);
    }

}
