package com.github.springerris.gui.helper;

import com.github.springerris.gui.WindowContext;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.GridBagConstraints.HORIZONTAL;

public abstract class ChoiceWindow extends GridBagWindow {

    public ChoiceWindow(WindowContext ctx, String title, int initialWidth, int initialHeight) {
        super(ctx, title, initialWidth, initialHeight);
    }

    //

    protected abstract String[] getChoices();

    protected abstract void onClickChoice(int index);

    //

    @Override
    protected void setupContent() {
        String[] choices = this.getChoices();
        for (int i=0; i < choices.length; i++) {
            JButton button = new JButton(choices[i]);
            this.addElement(0, i, 1, 1, button, HORIZONTAL);
            button.addActionListener(new ClickHandler(this, i));
        }
    }

    //

    private record ClickHandler(
            ChoiceWindow parent,
            int index
    ) implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            this.parent.onClickChoice(this.index);
        }

    }

}
