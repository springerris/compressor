package com.github.springerris.gui.impl;

import com.github.springerris.gui.Modal;
import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.BorderWindow;
import com.github.springerris.i18n.I18N;
import com.github.springerris.util.GridBagConstraintsBuilder;
import com.github.springerris.util.SSHHandler;
import net.schmizz.sshj.userauth.UserAuthException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.logging.Level;

public class SSHWindow extends BorderWindow implements Modal<SSHHandler> {

    private SSHHandler handler = null;
    private JTextField inputHost;
    private JFormattedTextField inputPort;
    private JTextField inputUsername;
    private JTextArea inputPassword;
    private JTabbedPane authPane;

    public SSHWindow(@NotNull WindowContext ctx) {
        super(ctx, I18N.WINDOW_EXPORT_SFTP_TITLE.get(), 255, 245);
    }

    @Override
    public synchronized @Nullable SSHHandler modalValue() {
        return this.handler;
    }

    @Override
    protected void setupContent() {
        this.setupForm();
        this.setupFooter();
    }

    private void setupForm() {
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());

        JTextField inputHost = new JTextField();
        form.add(
                new JLabel(I18N.WINDOW_EXPORT_SFTP_HOST.get()),
                constraints(0, 0, 5, 1)
                        .padding(2)
                        .anchor(-1, 0)
                        .build()
        );
        form.add(
                inputHost,
                constraints(0, 1, 5, 1)
                        .fill(true, false)
                        .weightX(0.8d)
                        .build()
        );

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(0xFFFF);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        JFormattedTextField inputPort = new JFormattedTextField(formatter);
        inputPort.setText("22");
        form.add(
                new JLabel(I18N.WINDOW_EXPORT_SFTP_PORT.get()),
                constraints(5, 0, 1, 1)
                        .padding(2)
                        .anchor(1, 0)
                        .build()
        );
        form.add(
                inputPort,
                constraints(5, 1, 1, 1)
                        .fill(true, false)
                        .weightX(0.2d)
                        .build()
        );

        JTextField inputUsername = new JTextField();
        form.add(
                new JLabel(I18N.WINDOW_EXPORT_SFTP_USERNAME.get()),
                constraints(0, 2, 6, 1)
                        .padding(2)
                        .anchor(-1, 0)
                        .build()
        );
        form.add(
                inputUsername,
                constraints(0, 3, 6, 1)
                        .fill(true, false)
                        .weightX(1d)
                        .build()
        );

        JTabbedPane authPane = new JTabbedPane();
        this.setupAuthPane(authPane);
        form.add(
                new JLabel(I18N.WINDOW_EXPORT_SFTP_AUTHENTICATION.get()),
                constraints(0, 4, 6, 1)
                        .padding(2)
                        .anchor(-1, 0)
                        .build()
        );
        form.add(
                authPane,
                constraints(0, 5, 6, 4)
                        .fill(true, true)
                        .weight(1d, 1d)
                        .build()
        );

        this.inputHost = inputHost;
        this.inputPort = inputPort;
        this.inputUsername = inputUsername;
        this.authPane = authPane;

        // TODO: DELETE BEFORE RELEASE
        this.inputHost.setText("crady.farted.net");
        this.inputUsername.setText("lucky");
        this.authPane.setSelectedIndex(1);
        this.inputPassword.setText("zHdErdPzqBYj");

        this.addElement(form);
    }

    private void setupAuthPane(JTabbedPane pane) {
        JPanel panelNone = new JPanel();
        panelNone.setLayout(new BorderLayout());
        panelNone.add(
                new JLabel(I18N.WINDOW_EXPORT_SFTP_NONE_DESC.get(), JLabel.CENTER),
                BorderLayout.CENTER
        );
        pane.addTab(I18N.WINDOW_EXPORT_SFTP_NONE.get(), panelNone);

        JPanel panelPassword = new JPanel();
        panelPassword.setLayout(new GridLayout(1, 1));
        JTextArea areaPassword = new JTextArea();
        panelPassword.add(areaPassword);
        pane.addTab(I18N.WINDOW_EXPORT_SFTP_PASSWORD.get(), panelPassword);
        this.inputPassword = areaPassword;

        JPanel panelPrivateKey = new JPanel();
        panelPrivateKey.setLayout(new BorderLayout());
        panelPrivateKey.add(
                new JLabel(I18N.WINDOW_EXPORT_SFTP_PRIVATE_KEY_DESC.get(), JLabel.CENTER),
                BorderLayout.CENTER
        );
        pane.addTab(I18N.WINDOW_EXPORT_SFTP_PRIVATE_KEY.get(), panelPrivateKey);
    }

    private void setupFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 2));

        JButton btnConnect = new JButton(I18N.WINDOW_EXPORT_SFTP_CONFIRM.get());
        footer.add(btnConnect);
        btnConnect.addActionListener(this::onClickConnect);

        JButton btnCancel = new JButton(I18N.WINDOW_EXPORT_SFTP_CANCEL.get());
        footer.add(btnCancel);
        btnCancel.addActionListener(this::onClickCancel);

        this.addElement(footer, BorderLayout.PAGE_END);
    }

    private void onClickConnect(ActionEvent ignored) {
        SSHHandler.Authentication auth = switch (this.authPane.getSelectedIndex()) {
            case 0 -> SSHHandler.Authentication.basic(this.inputUsername.getText(), "");
            case 1 -> SSHHandler.Authentication.basic(this.inputUsername.getText(), this.inputPassword.getText());
            default -> SSHHandler.Authentication.publicKey(this.inputUsername.getText());
        };

        SSHHandler handler = new SSHHandler(this.inputHost.getText(), (Integer) this.inputPort.getValue());
        boolean close = true;
        try {
            try {
                handler.connect(auth);
                close = false;
                this.complete(handler);
                return;
            } finally {
                if (close) handler.close();
            }
        } catch (UserAuthException e) {
            this.showError(I18N.WINDOW_EXPORT_SFTP_ERROR_AUTH);
        } catch (IOException e) {
            this.ctx.logger().log(Level.WARNING, "Failed to create SSH connection", e);
            this.showError(I18N.WINDOW_EXPORT_SFTP_ERROR_IO);
        }
        this.complete(null);
    }

    private void onClickCancel(ActionEvent ignored) {
        this.complete(null);
    }

    private synchronized void complete(@Nullable SSHHandler handler) {
        this.handler = handler;
        this.dispose();
    }

    //

    @Contract("_, _, _, _ -> new")
    private static @NotNull GridBagConstraintsBuilder constraints(int x, int y, int w, int h) {
        return (new GridBagConstraintsBuilder()).dimensions(x, y, w, h);
    }

}
