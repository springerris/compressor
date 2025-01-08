package com.github.springerris;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import static java.awt.GridBagConstraints.*;
import static javax.swing.JFileChooser.APPROVE_OPTION;

public class MainWindow extends GridBagWindow{
    private JButton buttonAddFile;
    private JButton buttonAddDir;
    private JButton buttonListHead;
    private JList<String> fileList;
    private DefaultListModel<String> files;

    private void updateList() {
        fileList.removeAll();
        files.removeAllElements();
        Set<Path> paths = ctx.zipper().getSelected();
        int i = 0;
        for (Path p : paths) {
            files.add(i, p.getFileName().toFile().getName());
            i++;
        }
    }

    private void updateList(Set<File> paths) {
        files.removeAllElements();
        int i = 0;
        for (File f : paths) {
            files.add(i, f.getName());
            i++;
        }
    }

    public MainWindow(WindowContext ctx, String title, int initialWidth, int initialHeight) {
        super(ctx, title, initialWidth, initialHeight);
    }

    @Override
    protected void setupContent() {
        files = new DefaultListModel<String>();
        files.add(0,"AAA");
        buttonAddFile = new JButton("Добавить файл в архив");
        buttonAddDir = new JButton("Добавить папку с файлами в архив");
        buttonListHead = new JButton("DEBUG HEAD");
        buttonListHead.addActionListener((ActionEvent a) -> {
            ctx.zipper().printFiles();
        });
        fileList = new JList<String>(files);

        fileList.addListSelectionListener((ListSelectionEvent e)->
        {
            if (e.getValueIsAdjusting()) {
            for (Path p : ctx.zipper().getSelected()) {
                if (Objects.equals(fileList.getSelectedValue(), p.toFile().getName())) {
                    if (p.toFile().isDirectory()) {
                        Set<File> filesNew = Set.of(p.toFile().listFiles());
                        updateList(filesNew);
                        System.out.println(p);
                    }
                }
            }

            }
        } );

        buttonAddFile.addActionListener((ActionEvent a) -> {
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            j.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int r = j.showOpenDialog(null);

            if (r == APPROVE_OPTION) {
                File file = j.getSelectedFile();

                System.out.println(file);
                if (!ctx.zipper().add(file)) {
                    showError("Не удалось добавить файл");
                }
                //zipfile.zipFile(dirRoot, zipfile.filename, zipfile.zipStream);
                ctx.zipper().printFiles();
                updateList();
            }
        });
        buttonAddDir.addActionListener((ActionEvent a) -> {
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            // set the selection mode to directories only
            j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int r = j.showOpenDialog(null);

            if (r == APPROVE_OPTION) {
                File dirRoot = Paths.get(j.getCurrentDirectory().getAbsolutePath(), j.getSelectedFile().getName()).toFile();

                System.out.println(dirRoot);
                if (!ctx.zipper().add(dirRoot)) {
                    showError("Не удалось добавить папку");
                }
                //zipfile.zipFile(dirRoot, zipfile.filename, zipfile.zipStream);
                ctx.zipper().printFiles();
                updateList();
            }
        });



        //this.addElement(0,0,1,2,buttonAddFile,HORIZONTAL);
        this.addElement(0,1,1,1,buttonAddDir,HORIZONTAL);
        this.addElement(0,2,1,1,buttonListHead,HORIZONTAL);

        this.addElement(1,0,2,8,fileList,BOTH);

    }
}
