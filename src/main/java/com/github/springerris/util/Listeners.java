package com.github.springerris.util;

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
    public static MouseListener mouseClicked(Consumer<MouseEvent> cb) {
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
    public static KeyListener keyReleased(Consumer<KeyEvent> cb) {
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                cb.accept(e);
            }
        };
    }

}
