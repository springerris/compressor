package com.github.springerris;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.swing.JFileChooser.APPROVE_OPTION;

public class Main {
    public static void main(String[] args) throws IOException {
        final Logger logger = Logger.getLogger("PP2024");
        logger.log(Level.INFO, "Starting PP2024");

        // Connect to the database


        // Create a WindowContext

        logger.log(Level.INFO, "Created application context");

        // Open the Welcome window

        System.out.println("Hello, World!");
        Zipper zipfile = new Zipper();
        final WindowContext ctx = new WindowContext(logger, zipfile);

        MainWindow window = new MainWindow(ctx, "Главное окно", 800,500);
        window.setVisible(true);


    }
}