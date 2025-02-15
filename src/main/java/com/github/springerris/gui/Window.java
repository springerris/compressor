package com.github.springerris.gui;

import com.github.springerris.i18n.I18N;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.util.logging.Level;

/**
 * Общая логика для всех окон
 */
public abstract class Window extends JFrame {

    protected static <T extends Window> T construct(Class<T> clazz, WindowContext ctx) {
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
    public Window(WindowContext ctx,  String title, int initialWidth, int initialHeight) {
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

    protected int defaultCloseOperation() {
        return JFrame.DISPOSE_ON_CLOSE;
    }

    protected abstract void setupLayout();

    protected abstract void setupContent();

    /**
     * Открывает окно с сообщением ошибки
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                I18N.POPUP_ERROR.get(),
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                I18N.POPUP_INFO.get(),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Закрывает это окно и открывает новое окно с выбранным классом
     */
    protected void transfer(Class<? extends Window> clazz) {
        Window window = construct(clazz, this.ctx);
        this.setVisible(false);
        window.setVisible(true);
        this.dispose();
    }

}
