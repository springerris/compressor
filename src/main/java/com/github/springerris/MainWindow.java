package com.github.springerris;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static java.awt.GridBagConstraints.*;

public class MainWindow extends GridBagWindow{
    private JButton buttonAddFile;
    private JButton buttonAddDir;
    private JList<String> fileList;
    private DefaultListModel<String> files;


    public MainWindow(WindowContext ctx, String title, int initialWidth, int initialHeight) {
        super(ctx, title, initialWidth, initialHeight);
    }

    @Override
    protected void setupContent() {
        files = new DefaultListModel<String>();
        files.add(0,"AAA");
        buttonAddFile = new JButton("Добавить файл в архив");
        buttonAddDir = new JButton("Добавить папку с файлами в архив");
        fileList = new JList<String>(files);


        this.addElement(0,0,1,1,buttonAddFile,HORIZONTAL);
        this.addElement(0,1,1,1,buttonAddDir,HORIZONTAL);
        this.addElement(1,0,2,4,fileList,BOTH);

    }
}
