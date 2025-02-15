package com.github.springerris.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Utility to reduce boilerplate around AWT/Swing listeners
 */
public final class Listeners {

    /**
     * Creates a MouseListener which defers to the specified callback
     * on {@link MouseListener#mouseClicked(MouseEvent) mouseClicked}.
     */
    @Contract("_ -> new")
    public static @NotNull MouseListener mouseClicked(@NotNull Consumer<MouseEvent> cb) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cb.accept(e);
            }
        };
    }

    /**
     * Creates a KeyListener which defers to the specified callback
     * on {@link KeyListener#keyReleased(KeyEvent) keyReleased}.
     */
    @Contract("_ -> new")
    public static @NotNull KeyListener keyReleased(@NotNull Consumer<KeyEvent> cb) {
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                cb.accept(e);
            }
        };
    }

}
