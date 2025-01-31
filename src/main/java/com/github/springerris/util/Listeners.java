package com.github.springerris.util;

import java.awt.event.*;
import java.util.function.Consumer;

public final class Listeners {

    public static MouseListener mouseClicked(Consumer<MouseEvent> cb) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cb.accept(e);
            }
        };
    }

    public static KeyListener keyReleased(Consumer<KeyEvent> cb) {
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                cb.accept(e);
            }
        };
    }

}
