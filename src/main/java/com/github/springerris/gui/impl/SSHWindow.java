package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.GridBagWindow;
import com.github.springerris.util.SSHHandler;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import static java.awt.GridBagConstraints.HORIZONTAL;

public class SSHWindow extends GridBagWindow {

    private JTextField userField;
    private JTextField pwdField;
    private JTextField hostField;
    private JFormattedTextField portField;
    private JButton confirm;

    public SSHWindow(WindowContext ctx, String title, int initialWidth, int initialHeight) {
        super(ctx, title, initialWidth, initialHeight);
    }

    @Override
    protected void setupContent() {
        confirm = new JButton("Подключится");
        userField = new JTextField();
        pwdField = new JPasswordField();
        hostField = new JTextField();

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum((Short.MAX_VALUE*2)-1);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        portField = new JFormattedTextField(formatter);
        confirm.addActionListener(e -> {
            SSHHandler sshHandler = new SSHHandler(this.userField.getText(),this.pwdField.getText(),this.hostField.getText(),((Number) this.portField.getValue()).intValue());

        });
        portField.setText("22");

        // TODO: DELETE BEFORE RELEASE
        hostField.setText("crady.farted.net");
        pwdField.setText("zHdErdPzqBYj");
        userField.setText("lucky");

        this.addElement(0,0,5,new Label("Хост"),HORIZONTAL);
        this.addElement(5,0,1,new Label("Порт"));
        this.addElement(0,1,5,hostField,HORIZONTAL);
        this.addElement(5,1,1,portField,HORIZONTAL);
        this.addElement(0,2,5,new Label("Пользователь"),HORIZONTAL);
        this.addElement(0,3,5,userField,HORIZONTAL);
        this.addElement(0,4,5,new Label("Пароль"),HORIZONTAL);
        this.addElement(0,5,5,pwdField,HORIZONTAL);
        this.addElement(0,6,1,confirm);
    }
}
