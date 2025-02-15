package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.GridBagWindow;
import com.github.springerris.i18n.I18N;
import com.github.springerris.util.SSHHandler;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.userauth.UserAuthException;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.logging.Level;

import static java.awt.GridBagConstraints.HORIZONTAL;

public class SSHWindow extends GridBagWindow {

    private JTextField userField;
    private JTextField pwdField;
    private JTextField hostField;
    private JFormattedTextField portField;

    public SSHWindow(WindowContext ctx) {
        super(ctx, I18N.WINDOW_EXPORT_SFTP_TITLE.get(), 500, 300);
    }

    @Override
    protected void setupContent() {
        JButton confirm = new JButton(I18N.WINDOW_EXPORT_SFTP_CONFIRM.get());
        confirm.addActionListener(this::onConfirm);

        this.userField = new JTextField();
        this.pwdField = new JPasswordField();
        this.hostField = new JTextField();

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(0xFFFF);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        this.portField = new JFormattedTextField(formatter);
        this.portField.setText("22");

        // TODO: DELETE BEFORE RELEASE
        this.hostField.setText("crady.farted.net");
        this.pwdField.setText("zHdErdPzqBYj");
        this.userField.setText("lucky");

        this.addElement(0, 0, 5, new Label(I18N.WINDOW_EXPORT_SFTP_HOST.get()), HORIZONTAL);
        this.addElement(5, 0, 1, new Label(I18N.WINDOW_EXPORT_SFTP_PORT.get()));
        this.addElement(0, 1, 5, this.hostField, HORIZONTAL);
        this.addElement(5, 1, 1, this.portField, HORIZONTAL);
        this.addElement(0, 2, 5, new Label(I18N.WINDOW_EXPORT_SFTP_USERNAME.get()), HORIZONTAL);
        this.addElement(0, 3, 5, this.userField, HORIZONTAL);
        this.addElement(0, 4, 5, new Label(I18N.WINDOW_EXPORT_SFTP_PASSWORD.get()), HORIZONTAL);
        this.addElement(0, 5, 5, this.pwdField, HORIZONTAL);
        this.addElement(0, 6, 1, confirm);
    }

    private void onConfirm(ActionEvent ignored) {
        String host = this.hostField.getText();
        int port = ((Number) this.portField.getValue()).intValue();

        try (SSHHandler handler = new SSHHandler(host, port)) {
            handler.connect(this.userField.getText(), this.pwdField.getText());

            List<RemoteResourceInfo> files = handler.list();
            for (RemoteResourceInfo f : files) {
                this.showInfo(f.getPath());
            }
        } catch (UserAuthException e) {
            this.showError(I18N.WINDOW_EXPORT_SFTP_ERROR_AUTH.get());
        } catch (IOException e) {
            this.ctx.logger().log(Level.WARNING, "Failed to create SSH connection", e);
            this.showError(I18N.WINDOW_EXPORT_SFTP_ERROR_IO.get());
        }
    }

}
