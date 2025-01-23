package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.util.YanHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static java.awt.GridBagConstraints.HORIZONTAL;

public class ChoiceWindowUpload extends ChoiceWindowBase {

    public ChoiceWindowUpload(
            WindowContext ctx,
            String title,
            int initialWidth,
            int initialHeight,
            String option1,
            String option2
    ) {
        super(ctx, title, initialWidth, initialHeight);
        this.option1 = option1;
        this.option2 = option2;
    }

    @Override
    protected void setupContent() {
        JButton pickYan = new JButton(option1);
        JButton pickGD = new JButton(option2);

        pickYan.addActionListener((ActionEvent e) -> {
            YanHandler yh = new YanHandler(this.ctx, this);
            yh.upload();
        });

        this.addElement(0, 0, 1, 1, pickYan, HORIZONTAL);
        this.addElement(0, 1, 1, 1, pickGD, HORIZONTAL);
    }

}
