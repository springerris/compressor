package com.github.springerris;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Общая логика для всех окон
 */
public abstract class Window extends JFrame {

    protected final WindowContext ctx;
    public Window( WindowContext ctx,  String title, int initialWidth, int initialHeight) {
        super(title);
        this.ctx = ctx;
        this.setSize(initialWidth, initialHeight);
        this.setResizable(true);
        this.setLocationRelativeTo(null); // center on screen
        this.setupLayout();
        this.setupContent();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    protected abstract void setupLayout();

    protected abstract void setupContent();

    /**
     * Открывает окно с сообщением ошибки
     */
    protected void showError( String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Ошибка",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Закрывает это окно и открывает новое окно с выбранным классом
     */
    protected void transfer( Class<? extends Window> clazz) {
        Window window;
        try {
            Constructor<? extends Window> con = clazz.getConstructor(WindowContext.class);
            window = con.newInstance(this.ctx);
        } catch (ReflectiveOperationException | SecurityException e) {
            this.ctx.logger().log(Level.SEVERE, "Unexpected reflection error", e);
            System.exit(1);
            return;
        }
        this.setVisible(false);
        window.setVisible(true);
        this.dispose();
    }

}
