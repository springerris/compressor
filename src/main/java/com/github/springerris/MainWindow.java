package com.github.springerris;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import static java.awt.GridBagConstraints.*;
import static javax.swing.JFileChooser.APPROVE_OPTION;

public class MainWindow extends GridBagWindow{

    private JButton buttonChoose;
    private JButton buttonAddFile;
    private JButton buttonAddDir;
    private JButton buttonListHead;
    private JButton buttonMoveUp;
    private JButton buttonWriteZip;
    private JList<String> fileList;
    private DefaultListModel<String> files;
    private List<File> currentFiles;
    boolean atRoot = true;

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

    public MainWindow(WindowContext ctx, String title, int initialWidth, int initialHeight) {
        super(ctx, title, initialWidth, initialHeight);
    }

    @Override
    protected void setupContent()  {

        Border padding = new EmptyBorder(8, 8, 8, 8);
        files = new DefaultListModel<String>();
        files.add(0,"AAA");
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
        fileList = new JList<String>(files);

        buttonMoveUp.setBorder(padding);
        buttonAddFile.setBorder(padding);

        fileList.addMouseListener(new MouseAdapter() {
              @Override
              public void mouseClicked(MouseEvent e) {
                  if (e.getClickCount() > 1) {
                      for (File f : currentFiles) {
                          if (Objects.equals(fileList.getSelectedValue(), f.getName())) {
                              if (f.isDirectory()) {
                                  try {
                                      atRoot = false;
                                      currentFiles = Arrays.asList(Objects.requireNonNull(f.listFiles()));
                                      updateList(currentFiles);
                                      System.out.println(f);
                                  } catch (NullPointerException ex) {
                                      throw new RuntimeException(ex);
                                  }
                              }
                              break;
                          }
                      }
                  }

              }
          }
          );


        buttonMoveUp.addActionListener((ActionEvent a) -> {
            if (!atRoot) {
                File parentF = currentFiles.getFirst().getParentFile();
                System.out.println(parentF.getAbsolutePath());
                //boolean hitRoot = true;
                int i = 0;
                int maxRoot = ctx.zipper().getSelected().size();
                for (Path p : ctx.zipper().getSelected()) {
                    System.out.println("HEAD: " +  p.toAbsolutePath());
                }
                if (ctx.zipper().getSelected().contains(parentF.toPath())) {
                    atRoot = true;
                    ArrayList<File> files = new ArrayList<>();
                    for (Path p :ctx.zipper().getSelected()) {
                        files.add(p.toFile());
                    }
                    currentFiles = files;
                } else
                {
                    currentFiles = Arrays.asList(Objects.requireNonNull(parentF.getParentFile().listFiles()));
                }
                updateList(currentFiles);


            }
        });

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
                else {
                    if (atRoot) {
                        currentFiles.add(dirRoot);
                    }
                }
                //zipfile.zipFile(dirRoot, zipfile.filename, zipfile.zipStream);
                ctx.zipper().printFiles();
                updateList();
            }
        });

        buttonWriteZip.addActionListener((ActionEvent a) -> {
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            j.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int r = j.showSaveDialog(this);

            if (r == APPROVE_OPTION) {
                try {
                    File file = j.getSelectedFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    ZipOutputStream zos = new ZipOutputStream(fos);
                    ctx.zipper().write(file);
                    System.out.println(file.getName());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


                //zipfile.zipFile(dirRoot, zipfile.filename, zipfile.zipStream);
                //ctx.zipper().printFiles();
                //updateList();
            }
        });

        buttonChoose.addActionListener((ActionEvent a) -> {

            ChoiceWindowUpload cw = new ChoiceWindowUpload(this.ctx, "Выбрать сервис",300,100, "Отправить на Yandex Disk","Отправить на Google Drive");
                cw.setVisible(true);

        });



        //this.addElement(0,0,1,2,buttonAddFile,HORIZONTAL);
        this.addElement(0,0,1,1,buttonAddDir,HORIZONTAL);
        this.addElement(1,0,1,1,buttonAddFile,HORIZONTAL);
        this.addElement(2,0,1,1,buttonWriteZip,HORIZONTAL);
        this.addElement(3,0,1,1,buttonChoose,HORIZONTAL);
        JScrollPane sp = new JScrollPane(fileList);
        this.addElement(0,1,1,1,buttonMoveUp,HORIZONTAL);
        this.addElement(0,2,8,8,sp,BOTH);

    }
}
