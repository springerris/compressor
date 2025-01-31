package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.ChoiceWindow;
import com.github.springerris.i18n.I18N;
import com.github.springerris.util.YanHandler;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

public class ExportChoiceWindow extends ChoiceWindow {

    public ExportChoiceWindow(WindowContext ctx) {
        super(ctx, I18N.WINDOW_EXPORT_TITLE.get(), 300, 200);
    }

    //

    @Override
    protected String[] getChoices() {
        return new String[] {
                I18N.WINDOW_EXPORT_OPTION_ZIP.get(),
                I18N.WINDOW_EXPORT_OPTION_YANDEX.get(),
                I18N.WINDOW_EXPORT_OPTION_DRIVE.get()
        };
    }

    @Override
    protected void onClickChoice(int index) {
        switch (index) {
            case 0 -> this.onClickChoice0();
            case 1 -> this.onClickChoice1();
            case 2 -> this.onClickChoice2();
        }
        this.transfer(MainWindow.class);
    }

    //

    private void onClickChoice0() {
        final String zipDesc = I18N.WINDOW_EXPORT_OPTION_ZIP_TYPE_BASIC.get();
        final String encryptedZipDesc = I18N.WINDOW_EXPORT_OPTION_ZIP_TYPE_ENCRYPTED.get();

        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());
        jfc.setFileFilter(new FileNameExtensionFilter(zipDesc, ".zip"));
        jfc.setFileFilter(new FileNameExtensionFilter(encryptedZipDesc, ".zip.m64"));

        int r = jfc.showSaveDialog(null);
        if (r != JFileChooser.APPROVE_OPTION) return;

        Path p = jfc.getSelectedFile().toPath();
        String fn = p.getFileName().toString();
        String password = null;

        if (fn.lastIndexOf('.') == -1) {
            FileFilter ff = jfc.getFileFilter();
            if (ff != null && encryptedZipDesc.equals(ff.getDescription())) {
                password = this.passwordPrompt(false);
                fn += ".zip.m64";
            } else {
                fn += ".zip";
            }
            p = p.getParent().resolve(fn);
        } else if (fn.endsWith(".zip.m64")) {
            password = this.passwordPrompt(false);
        }

        try (OutputStream os = Files.newOutputStream(
                p,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE
             )
        ) {
            this.ctx.archive().write(os, password);
        } catch (IOException e) {
            this.ctx.logger().log(Level.SEVERE, "Unexpected IO exception", e);
        }
    }

    private void onClickChoice1() {
        YanHandler yh = new YanHandler(this.ctx, this);
        yh.upload(this.passwordPrompt(true));
    }

    private void onClickChoice2() {
        // TODO
    }

    //

    private String passwordPrompt(boolean ask) {
        if (ask) {
            int isProtected = JOptionPane.showConfirmDialog(
                    this,
                    I18N.STAGE_PASSWORD_PROMPT_CONFIRM.get(),
                    I18N.STAGE_PASSWORD_PROMPT_TITLE.get(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (isProtected != JOptionPane.YES_OPTION) return null;
        }

        String password;
        do {
            password = JOptionPane.showInputDialog(I18N.STAGE_PASSWORD_PROMPT_ENTER.get());
        } while (password.isBlank());
        return password;
    }

}
