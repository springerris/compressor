package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.BorderWindow;
import com.github.springerris.i18n.I18N;
import com.github.springerris.token.TokenType;
import io.github.wasabithumb.yandisk4j.YanDisk;
import io.github.wasabithumb.yandisk4j.except.YanDiskException;
import io.github.wasabithumb.yandisk4j.node.Node;
import io.github.wasabithumb.yandisk4j.node.accessor.NodeDownloader;
import io.github.wasabithumb.yandisk4j.util.PaginatedResult;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class YanFilesWindow extends BorderWindow {

    private List<Node> yanFiles;
    private DefaultListModel<String> list;
    private JList<String> fileList;

    public YanFilesWindow(@NotNull WindowContext ctx) {
        super(ctx, I18N.WINDOW_IMPORT_YANDEX_TITLE.get(), 300, 500);
    }

    @Override
    protected void setupContent() {
        this.list = new DefaultListModel<>();
        this.fillList();
        JList<String> fileList = new JList<>(this.list);
        ScrollPane sp = new ScrollPane();
        sp.add(fileList);
        this.addElement(sp, BorderLayout.CENTER);
        fileList.addListSelectionListener(this::onSelect);
        this.fileList = fileList;
    }

    private void fillList() {
        YanDisk yd = YanDisk.yanDisk(this.ctx.tokens().get(TokenType.YANDEX_DISK));

        PaginatedResult<Node> result;
        List<Node> nodes = new ArrayList<>();
        try {
            result = yd.list("disk:/.archives");
            for (Node n : result) nodes.add(n);
        } catch (YanDiskException ignored) { }

        this.yanFiles = nodes;
        for (Node n : this.yanFiles) {
            this.list.addElement(n.name());
        }
    }

    private void onSelect(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            System.out.println(this.list.get(this.fileList.getSelectedIndex()));
            Node zipNode = this.yanFiles.get(this.fileList.getSelectedIndex());
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int r = jfc.showOpenDialog(null);
            if (r != JFileChooser.APPROVE_OPTION) return;
            File dir = jfc.getSelectedFile();

            this.onSelect0(zipNode, dir);
        }
    }

    private void onSelect0(Node zipNode, File dir) {
        System.out.println(dir);
        System.out.println(Paths.get(dir.toString(), zipNode.name()));
        File newZip = new File(Paths.get(dir.toString(), zipNode.name()).toString());
        try {
            YanDisk yd = YanDisk.yanDisk(this.ctx.tokens().get(TokenType.YANDEX_DISK));
            NodeDownloader nd = yd.download(zipNode.path());
            if (newZip.exists()) {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        I18N.RECEIVE_CONFLICT.get(),
                        I18N.POPUP_INFO.get(),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result != JOptionPane.YES_OPTION) return;
            }
            Files.copy(nd.open(), newZip.toPath(), REPLACE_EXISTING);
            this.ctx.loadArchive(newZip.toPath(), this::passwordPrompt);
        } catch (IOException e) {
            // TODO: Better logging
            e.printStackTrace();
        }
    }

    private @NotNull String passwordPrompt() {
        return this.pester(I18N.STAGE_PASSWORD_PROMPT_ENTER);
    }

}
