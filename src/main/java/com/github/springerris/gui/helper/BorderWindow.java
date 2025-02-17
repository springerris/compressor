package com.github.springerris.gui.helper;

import com.github.springerris.gui.Window;
import com.github.springerris.gui.WindowContext;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class BorderWindow extends Window {

    public BorderWindow(
            @NotNull WindowContext ctx,
            @NotNull CharSequence title,
            int initialWidth,
            int initialHeight
    ) {
        super(ctx, title, initialWidth, initialHeight);
    }

    @Override
    protected void setupLayout() {
        final Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
    }

    protected void addElement(
            @NotNull Component component,
            @NotNull @MagicConstant(valuesFromClass = BorderLayout.class) String placement
    ) {
        this.getContentPane().add(component, placement);
    }

    protected void addElement(@NotNull Component component) {
        this.addElement(component, BorderLayout.CENTER);
    }

}
