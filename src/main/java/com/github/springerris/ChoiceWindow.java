package com.github.springerris;

import io.github.wasabithumb.yandisk4j.YanDisk;
import io.github.wasabithumb.yandisk4j.auth.AuthHandler;
import io.github.wasabithumb.yandisk4j.auth.AuthResponse;
import io.github.wasabithumb.yandisk4j.auth.AuthScheme;
import io.github.wasabithumb.yandisk4j.auth.scope.AuthScope;
import io.github.wasabithumb.yandisk4j.node.accessor.NodeUploader;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;

public class ChoiceWindow extends GridBagWindow{
    boolean uploading = true;
    public ChoiceWindow(WindowContext ctx, String title, int initialWidth, int initialHeight){
        super(ctx, title, initialWidth, initialHeight);
    }


    public ChoiceWindow(WindowContext ctx, String title, int initialWidth, int initialHeight, boolean uploading) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super(ctx, title, initialWidth, initialHeight);
        this.uploading = uploading;
    }

    @Override
    protected void setupContent() {




        Border padding = new EmptyBorder(8, 8, 8, 8);
        JButton pickYan = new JButton("Отправить на Yandex Disk");
        JButton pickGD = new JButton("Отправить на Google Drive");

        pickYan.addActionListener((ActionEvent e) -> {
            YanHandler yh = new YanHandler(this.ctx, this);
            yh.upload();
        });

        this.addElement(0,0,1,1,pickYan,HORIZONTAL);
        this.addElement(0,1,1,1,pickGD,HORIZONTAL);
    }
}
