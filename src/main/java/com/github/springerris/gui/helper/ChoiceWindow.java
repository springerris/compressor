package com.github.springerris.gui.helper;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.impl.AwaitingWindow;
import com.github.springerris.gui.impl.MainWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CompletableFuture;

import static java.awt.GridBagConstraints.HORIZONTAL;

public abstract class ChoiceWindow extends GridBagWindow {

    public ChoiceWindow(WindowContext ctx, String title, int initialWidth, int initialHeight) {
        super(ctx, title, initialWidth, initialHeight);

        // When close button is pressed, go to MainWindow instead of exiting
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ChoiceWindow.this.transfer(MainWindow.class);
            }
        });
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
            CompletableFuture<Object> future = new CompletableFuture<>();
            Thread t = new Thread(() -> this.execute(future));
            t.setName("Choice Execution Thread");
            t.start();

            this.parent.ctx.setActiveTask(future);
            this.parent.transfer(AwaitingWindow.class);
        }

        private void execute(CompletableFuture<?> future) {
            try {
                this.parent.onClickChoice(this.index);
            } catch (Throwable err) {
                future.completeExceptionally(err);
                return;
            }
            future.complete(null);
        }

    }

}
