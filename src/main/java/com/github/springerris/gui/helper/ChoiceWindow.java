package com.github.springerris.gui.helper;

import com.github.springerris.gui.Modal;
import com.github.springerris.gui.Window;
import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.impl.AwaitingWindow;
import com.github.springerris.gui.impl.MainWindow;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CompletableFuture;

public abstract class ChoiceWindow extends GridBagWindow {

    public ChoiceWindow(
            @NotNull WindowContext ctx,
            @NotNull String title,
            int initialWidth,
            int initialHeight
    ) {
        super(ctx, title, initialWidth, initialHeight);

        // When close button is pressed, go to MainWindow
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ChoiceWindow.this.transfer(MainWindow.class);
            }
        });
    }

    //

    protected abstract @NotNull String @NotNull [] getChoices();

    @ApiStatus.OverrideOnly
    @Blocking
    protected abstract void onClickChoice(int index);

    //

    @Override
    protected void setupContent() {
        String[] choices = this.getChoices();
        for (int i=0; i < choices.length; i++) {
            JButton button = new JButton(choices[i]);
            this.addElement(button, constraints().dimensions(0, i, 1, 1).fill(true, false));
            button.addActionListener(new ClickHandler(this, i));
        }
    }

    /**
     * Utility that creates a new window which inherits the current context and blocks until it closes;
     * either by the user pressing the close button or the window calling {@link Window#dispose()} on itself.
     * @return The {@link Modal#modalValue() modal value} of the popup after it has closed.
     */
    @Blocking
    protected final <R, W extends Window & Modal<R>> @UnknownNullability R popup(@NotNull Class<W> clazz) {
        W window = construct(clazz, this.ctx);
        Object mutex = new Object();

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                synchronized (mutex) {
                    mutex.notify();
                }
            }
        });

        synchronized (mutex) {
            window.setAutoRequestFocus(true);
            window.setVisible(true);
            SwingUtilities.invokeLater(window::toFront);
            try {
                mutex.wait();
            } catch (InterruptedException ignored) { }
        }

        return window.modalValue();
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
            t.setName("Choice Executor Thread");
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
