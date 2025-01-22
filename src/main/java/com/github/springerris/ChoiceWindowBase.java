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

public class ChoiceWindowBase extends GridBagWindow{

    protected String option1;
    protected String option2;

    public ChoiceWindowBase(WindowContext ctx, String title, int initialWidth, int initialHeight) {
        super(ctx, title, initialWidth, initialHeight);
        this.option1 = option1;
        this.option2 = option2;
    }

    @Override
    protected void setupContent() {




        Border padding = new EmptyBorder(8, 8, 8, 8);
        JButton pickYan = new JButton(option1);
        JButton pickGD = new JButton(option2);

        pickYan.addActionListener((ActionEvent e) -> {
            YanHandler yh = new YanHandler(this.ctx, this);
            yh.upload();
        });

        this.addElement(0,0,1,1,pickYan,HORIZONTAL);
        this.addElement(0,1,1,1,pickGD,HORIZONTAL);
    }
}
