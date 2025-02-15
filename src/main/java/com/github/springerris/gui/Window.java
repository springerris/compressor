package com.github.springerris.gui;

import com.github.springerris.i18n.I18N;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NonBlocking;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.util.logging.Level;

/**
 * Общая логика для всех окон
 */
public abstract class Window extends JFrame {

    protected static <T extends Window> @NotNull T construct(
            @NotNull Class<T> clazz,
            @NotNull WindowContext ctx
    ) {
        try {
            Constructor<T> con = clazz.getConstructor(WindowContext.class);
            return con.newInstance(ctx);
        } catch (ReflectiveOperationException | SecurityException e) {
            ctx.logger().log(Level.SEVERE, "Unexpected reflection error", e);
            System.exit(1);
            return null;
        }
    }

    //

    protected final WindowContext ctx;
    public Window(
            @NotNull WindowContext ctx,
            @NotNull String title,
            int initialWidth,
            int initialHeight
    ) {
        super(title);
        this.ctx = ctx;
        this.setSize(initialWidth, initialHeight);
        this.setResizable(true);
        this.setLocationRelativeTo(null); // center on screen
        this.setupLayout();
        this.setupContent();
        //noinspection MagicConstant
        this.setDefaultCloseOperation(this.defaultCloseOperation());
    }

    @ApiStatus.OverrideOnly
    protected int defaultCloseOperation() {
        return JFrame.DISPOSE_ON_CLOSE;
    }

    @NonBlocking
    @ApiStatus.OverrideOnly
    protected abstract void setupLayout();

    @NonBlocking
    @ApiStatus.OverrideOnly
    protected abstract void setupContent();

    /**
     * Открывает окно с сообщением ошибки
     */
    public void showError(@NotNull CharSequence message) {
        JOptionPane.showMessageDialog(
                this,
                message.toString(),
                I18N.POPUP_ERROR.get(),
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void showInfo(@NotNull CharSequence message) {
        JOptionPane.showMessageDialog(
                this,
                message.toString(),
                I18N.POPUP_INFO.get(),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public @NotNull String pester(@NotNull CharSequence message) {
        String input;
        do {
            input = JOptionPane.showInputDialog(this, message.toString());
        } while (input == null || input.isBlank());
        return input;
    }

    /**
     * Закрывает это окно и открывает новое окно с выбранным классом
     */
    protected void transfer(@NotNull Class<? extends Window> clazz) {
        Window window = construct(clazz, this.ctx);
        this.setVisible(false);
        window.setVisible(true);
        this.dispose();
    }

}
