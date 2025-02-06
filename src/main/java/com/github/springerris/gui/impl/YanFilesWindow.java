package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.BorderWindow;
import com.github.springerris.util.YanHandler;
import io.github.wasabithumb.yandisk4j.node.Node;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class YanFilesWindow extends BorderWindow {

    private List<Node> yanFiles;
    private YanHandler yh;

    public YanFilesWindow(WindowContext ctx, String title, int initialWidth, int initialHeight, YanHandler yh) {
        super(ctx, title, initialWidth, initialHeight);
        this.yh = yh;
    }

    @Override
    protected void setupContent() {
        DefaultListModel<String> list = new DefaultListModel<String>();
        yanFiles = yh.listFiles();

        JList<String> fileList = new JList<>(list);
        for (Node n: yanFiles) {
            list.addElement(n.toString());

        }

    this.addElement(fileList, BorderLayout.CENTER);


    }
}
