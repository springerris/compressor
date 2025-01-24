package com.github.springerris.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

}
