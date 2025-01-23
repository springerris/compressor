package com.github.springerris;

import com.github.springerris.gui.impl.MainWindow;
import com.github.springerris.gui.WindowContext;
import com.github.springerris.util.Zipper;

import javax.swing.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        final Logger logger = Logger.getLogger("PP2024");
        logger.log(Level.INFO, "Starting PP2024");

        // Create a WindowContext
        Zipper zipper = new Zipper();
        final WindowContext ctx = new WindowContext(logger, zipper);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        logger.log(Level.INFO, "Created application context");

        // Open main window
        MainWindow window = new MainWindow(ctx, "Главное окно", 800, 500);
        window.setVisible(true);
    }

}