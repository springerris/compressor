package com.github.springerris;

import javax.swing.*;

import java.awt.event.ActionEvent;

import static java.awt.GridBagConstraints.HORIZONTAL;

public class ChoiceWindowBase extends GridBagWindow {

    protected String option1;
    protected String option2;

    public ChoiceWindowBase(WindowContext ctx, String title, int initialWidth, int initialHeight) {
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
