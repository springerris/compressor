package com.github.springerris;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

import static javax.swing.JFileChooser.APPROVE_OPTION;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("Hello, World!");
        Zipper zipfile = new Zipper("archive.zip");

        JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        // set the selection mode to directories only
        j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int r = j.showOpenDialog(null);

        if (r == APPROVE_OPTION) {
            File dirRoot = Paths.get(j.getCurrentDirectory().getAbsolutePath(), j.getSelectedFile().getName()).toFile();

            System.out.println(dirRoot);
            zipfile.traverseDir("",dirRoot,true);
            //zipfile.zipFile(dirRoot, zipfile.filename, zipfile.zipStream);
            zipfile.zipStream.close();
            zipfile.stream.close();
        }

    }
}