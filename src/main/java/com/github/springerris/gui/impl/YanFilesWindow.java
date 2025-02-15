package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.BorderWindow;
import com.github.springerris.i18n.I18N;
import com.github.springerris.util.YanHandler;
import io.github.wasabithumb.yandisk4j.node.Node;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class YanFilesWindow extends BorderWindow {

    private Node zipNode;
    private List<Node> yanFiles;
    private YanHandler yh;
    private DefaultListModel<String> list;

    public YanFilesWindow(WindowContext ctx) {
        super(ctx, I18N.WINDOW_IMPORT_YANDEX_TITLE.get(), 300, 500);
    }

    private String passwordPrompt() {
        String password;
        do {
            password = JOptionPane.showInputDialog(I18N.STAGE_PASSWORD_PROMPT_ENTER.get());
        } while (password.isBlank());
        return password;
    }

    @Override
    protected void setupContent() {
        this.list = new DefaultListModel<>();
        JList<String> fileList = new JList<>(this.list);
        ScrollPane sp = new ScrollPane();
        sp.add(fileList);
        this.addElement(sp, BorderLayout.CENTER);

        fileList.addListSelectionListener(arg0 -> {
            if (!arg0.getValueIsAdjusting()) {
                System.out.println(this.list.get(fileList.getSelectedIndex()));
                this.zipNode = this.yanFiles.get(fileList.getSelectedIndex());
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int r = jfc.showOpenDialog(null);
                if (r != JFileChooser.APPROVE_OPTION) return;
                File dir = jfc.getSelectedFile();
                this.setVisible(false);
                System.out.println(dir);
                System.out.println(Paths.get(dir.toString(), this.zipNode.name()));
                File newZip = new File(Paths.get(dir.toString(), this.zipNode.name()).toString());
                try {
                    this.yh.download(this.zipNode, newZip);
                    this.ctx.loadArchive(newZip.toPath(), this::passwordPrompt);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    // TODO: This is cancer.
    @Deprecated
    public void setYh(YanHandler yh) {
        this.yh = yh;
    }

    public void fillList() {
        this.yanFiles = this.yh.listFiles();
        for (Node n : this.yanFiles) {
            this.list.addElement(n.name());
        }
    }

}
