package com.github.springerris;

import com.github.springerris.gui.impl.MainWindow;
import com.github.springerris.gui.WindowContext;
import com.github.springerris.i18n.I18N;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        final Logger logger = Logger.getLogger("PP2024");
        logger.log(Level.INFO, I18N.LIFECYCLE_START.get());

        // Setup Swing
        setupSwing(logger);

        // Create a WindowContext
        final WindowContext ctx = new WindowContext(logger);
        logger.log(Level.INFO, I18N.LIFECYCLE_CONTEXT.get());

        // Open main window
        MainWindow window = new MainWindow(ctx);
        window.setVisible(true);
    }

    private static void setupSwing(Logger logger) {
        // Set the LookAndFeel
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            logger.log(Level.WARNING, "Failed to set LookAndFeel", e);
        }
    }

}