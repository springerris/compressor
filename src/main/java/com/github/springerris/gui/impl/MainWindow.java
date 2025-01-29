package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.GridBagWindow;
import com.github.springerris.util.Listeners;
import io.github.wasabithumb.magma4j.Magma;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.zip.ZipOutputStream;

import static java.awt.GridBagConstraints.*;
import static javax.swing.JFileChooser.APPROVE_OPTION;

@SuppressWarnings("FieldCanBeLocal")
public class MainWindow extends GridBagWindow {

    private JButton buttonChoose;
    private JButton buttonAddFile;
    private JButton buttonAddDir;
    private JButton buttonListHead;
    private JButton buttonMoveUp;
    private JButton buttonWriteZip;
    private JList<String> fileList;
    private DefaultListModel<String> files;
    private List<File> currentFiles;
    private boolean atRoot = true;

    public MainWindow(WindowContext ctx) {
        super(ctx, "Главное окно", 800, 500);
    }

    @Override
    protected void setupContent() {
        Border padding = new EmptyBorder(8, 8, 8, 8);
        files = new DefaultListModel<>();
        files.add(0, "AAA");
        currentFiles = new ArrayList<>();
        buttonAddFile = new JButton("<html>Добавить <br>файл в архив</html>");
        buttonWriteZip = new JButton("<html>Записать<br> архив</html>");
        buttonMoveUp = new JButton("На уровень выше ..");
        buttonAddDir = new JButton("<html>Добавить папку<br> с файлами в архив</html>");
        buttonMoveUp.setIcon(UIManager.getIcon("FileChooser.upFolderIcon"));
        buttonAddDir.setIcon(UIManager.getIcon("FileChooser.newFolderIcon"));
        buttonAddFile.setIcon(UIManager.getIcon("FileView.fileIcon"));
        buttonChoose = new JButton("<html>Отправить <br> архив в..       </html>");
        buttonWriteZip.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
        buttonChoose.setIcon(UIManager.getIcon("FileView.computerIcon"));
        buttonListHead = new JButton("DEBUG HEAD");
        buttonListHead.addActionListener((ActionEvent a) -> {
            ctx.zipper().printFiles();
        });
        fileList = new JList<>(files);

        buttonMoveUp.setBorder(padding);
        buttonAddFile.setBorder(padding);

        fileList.addMouseListener(Listeners.mouseClicked(this::onClickFileList));
        buttonMoveUp.addActionListener(this::onClickMoveUp);
        buttonAddFile.addActionListener(this::onClickAddFile);
        buttonAddDir.addActionListener(this::onClickAddDir);
        buttonWriteZip.addActionListener(this::onClickWriteZip);
        buttonChoose.addActionListener(this::onClickChoose);

        //this.addElement(0,0,1,2,buttonAddFile,HORIZONTAL);
        this.addElement(0, 0, 1, 1, buttonAddDir, HORIZONTAL);
        this.addElement(1, 0, 1, 1, buttonAddFile, HORIZONTAL);
        this.addElement(2, 0, 1, 1, buttonWriteZip, HORIZONTAL);
        this.addElement(3, 0, 1, 1, buttonChoose, HORIZONTAL);
        JScrollPane sp = new JScrollPane(fileList);
        this.addElement(0, 1, 1, 1, buttonMoveUp, HORIZONTAL);
        this.addElement(0, 2, 8, 8, sp, BOTH);
    }

    private void onClickFileList(MouseEvent e) {
        // \/ Double click, silly \/
        // TODO: is this a mistake? why would we ignore a click count of 1? should it be < instead of <=?
        if (e.getClickCount() <= 1) return;
        System.out.println("DOUBLE CLICK!");
        for (File f : currentFiles) {
            if (!Objects.equals(fileList.getSelectedValue(), f.getName())) continue;
            if (!f.isDirectory()) break;
            File[] list = f.listFiles();
            if (list == null) throw new AssertionError("Directory listing for " + f + " is null");
            atRoot = false;
            currentFiles = List.of(list);

            System.out.println(f);
            break;
        }
        updateList();
    }

    private void onClickMoveUp(ActionEvent e) {
        if (!atRoot) {
            File parentF = currentFiles.getFirst().getParentFile();
            System.out.println(parentF.getAbsolutePath());
            //boolean hitRoot = true;
            int i = 0;
            int maxRoot = ctx.zipper().getSelected().size();
            for (Path p : ctx.zipper().getSelected()) {
                System.out.println("HEAD: " + p.toAbsolutePath());
            }
            if (ctx.zipper().getSelected().contains(parentF.toPath())) {
                atRoot = true;
                List<File> files = new ArrayList<>();
                for (Path p : ctx.zipper().getSelected()) {
                    files.add(p.toFile());
                }
                currentFiles = files;
            } else {
                currentFiles = Arrays.asList(Objects.requireNonNull(parentF.getParentFile().listFiles()));
            }
            updateList(currentFiles);
        }
    }

    private void onClickAddFile(ActionEvent e) {
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
    }

    private void onClickAddDir(ActionEvent e) {
        JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int r = j.showOpenDialog(null);
        if (r == APPROVE_OPTION) {
            File dirRoot = Paths.get(j.getCurrentDirectory().getAbsolutePath(), j.getSelectedFile().getName()).toFile();

            System.out.println(dirRoot);
            if (!ctx.zipper().add(dirRoot)) {
                showError("Не удалось добавить папку");
            } else if (atRoot) {
                currentFiles.add(dirRoot);
            }
            // zipfile.zipFile(dirRoot, zipfile.filename, zipfile.zipStream);
            ctx.zipper().printFiles();
            updateList();
        }
    }

    private void onClickWriteZip(ActionEvent e) {
        String password = "";
        JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        j.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int r = j.showSaveDialog(this);
        if (r == APPROVE_OPTION) {

            int isProtected = JOptionPane.showConfirmDialog(this,
                    "Добавить пароль для доступа к архиву?",
                    "Выбор пароля",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (isProtected == JOptionPane.YES_OPTION) {
                while (password.isBlank()) {
                    password = JOptionPane.showInputDialog("Введите пароль для архива");
                }
            }
            try {
                File file = j.getSelectedFile();
                OutputStream fos = new FileOutputStream(file);
                if (!password.isBlank()) {
                    byte[] key = Magma.generateKeyFromPassword(password);
                    fos = Magma.newOutputStream(fos,key);
                }

                ZipOutputStream zos = new ZipOutputStream(fos);

                ctx.zipper().write(zos);
                System.out.println(file.getName());
                zos.close();
            } catch (IOException e1) {
                this.reportIOException(e1);
            }

            //zipfile.zipFile(dirRoot, zipfile.filename, zipfile.zipStream);
            //ctx.zipper().printFiles();
            //updateList();
        }
    }

    private void onClickChoose(ActionEvent e) {
        this.transfer(UploadChoiceWindow.class);
    }

    private void updateList() {
        fileList.removeAll();
        files.removeAllElements();
        List<File> filesL = currentFiles;
        int i = 0;
        for (File f : filesL) {
            files.add(i, f.getName());
            i++;
        }
    }

    private void updateList(List<File> filesL) {
        files.removeAllElements();
        int i = 0;
        for (File f : filesL) {
            files.add(i, f.getName());
            i++;
        }
    }

    private void reportIOException(IOException e) {
        this.ctx.logger().log(Level.WARNING, "Unexpected IO exception", e);
    }

}
