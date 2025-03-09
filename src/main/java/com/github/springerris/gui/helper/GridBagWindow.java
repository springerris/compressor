package com.github.springerris.gui.helper;

import com.github.springerris.gui.Window;
import com.github.springerris.gui.WindowContext;
import com.github.springerris.util.GridBagConstraintsBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/** Класс, наследующий Window, с реализацией специфического расположения элементов */
public abstract class GridBagWindow extends Window {

    @Contract("-> new")
    protected static @NotNull GridBagConstraintsBuilder constraints() {
        return new GridBagConstraintsBuilder();
    }

    //

    public GridBagWindow(
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
        // contentPane.setBackground(new Color(0xFFE8B3));
        contentPane.setLayout(new GridBagLayout());
    }

    protected void addElement(@NotNull Component component, @NotNull GridBagConstraints constraints) {
        this.getContentPane().add(component, constraints);
    }

    protected void addElement(@NotNull Component component, @NotNull GridBagConstraintsBuilder constraintsBuilder) {
        this.addElement(component, constraintsBuilder.build());
    }

}
