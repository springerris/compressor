package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.GridBagWindow;
import com.github.springerris.i18n.I18N;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.CompletableFuture;

/**
 * A window which waits for the
 * {@link WindowContext#setActiveTask(CompletableFuture) active task} to complete.
 * The active task should be set before transferring to this window.
 */
public class AwaitingWindow extends GridBagWindow {

    private JLabel text;
    private JButton continueButton;
    private boolean canContinue = false;

    public AwaitingWindow(@NotNull WindowContext ctx) {
        super(ctx, I18N.WINDOW_AWAITING_TITLE.get(), 340, 240);
        this.ctx.whenActiveTaskComplete(this::onComplete);
    }

    //


    @Override
    protected int defaultCloseOperation() {
        return JFrame.DO_NOTHING_ON_CLOSE;
    }

    @Override
    protected void setupContent() {
        JLabel label = new JLabel(I18N.WINDOW_AWAITING_TEXT.get());
        this.addElement(label, constraints().dimensions(0, 0, 1, 1).fill(false, true));
        this.text = label;

        JButton btn = new JButton(I18N.WINDOW_AWAITING_CONTINUE.get());
        btn.setEnabled(false);
        this.addElement(btn, constraints().dimensions(0, 1, 1, 1).fill(false, true));
        this.continueButton = btn;
        btn.addActionListener(this::onClickContinue);
    }

    //

    private void onComplete() {
        SwingUtilities.invokeLater(() -> {
            this.canContinue = true;
            this.continueButton.setEnabled(true);
            this.text.setText(I18N.WINDOW_AWAITING_COMPLETE.get());
        });
    }

    private void onClickContinue(ActionEvent ignored) {
        if (!this.canContinue) return;
        this.transfer(MainWindow.class);
    }

}
