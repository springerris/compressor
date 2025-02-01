package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.ChoiceWindow;
import com.github.springerris.i18n.I18N;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public class ImportWindow extends ChoiceWindow {

    public ImportWindow(WindowContext ctx) {
        super(ctx, I18N.WINDOW_IMPORT_TITLE.get(), 300, 200);
    }

    //

    @Override
    protected String[] getChoices() {
        return new String[] {
                I18N.WINDOW_IMPORT_OPTION_ZIP.get(),
                I18N.WINDOW_IMPORT_OPTION_YANDEX.get(),
                I18N.WINDOW_IMPORT_OPTION_DRIVE.get()
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
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(new FileNameExtensionFilter(
                I18N.WINDOW_EXPORT_OPTION_ZIP_TYPE_BASIC.get(),
                "zip"
        ));
        jfc.setFileFilter(new FileNameExtensionFilter(
                I18N.WINDOW_EXPORT_OPTION_ZIP_TYPE_ENCRYPTED.get(),
                "m64"
        ));

        int r = jfc.showOpenDialog(null);
        if (r != JFileChooser.APPROVE_OPTION) return;

        Path p = jfc.getSelectedFile().toPath();
        if (!Files.isRegularFile(p)) {
            // TODO: Maybe show an error?
            return;
        }
        this.loadArchive(p);
    }

    private void onClickChoice1() {
        // TODO
    }

    private void onClickChoice2() {
        // TODO
    }

    //

    private void loadArchive(Path p) {
        try {
            this.ctx.loadArchive(p, this::passwordPrompt);
        } catch (IllegalArgumentException e) {
            this.showError(I18N.WINDOW_IMPORT_ERROR_FORMAT.get());
        } catch (IllegalStateException e) {
            this.showError(I18N.WINDOW_IMPORT_ERROR_PASSWORD.get());
        } catch (IOException e) {
            this.ctx.logger().log(Level.WARNING, "Failed to extract", e);
            System.exit(1);
        }
    }

    private String passwordPrompt() {
        String password;
        do {
            password = JOptionPane.showInputDialog(I18N.STAGE_PASSWORD_PROMPT_ENTER.get());
        } while (password.isBlank());
        return password;
    }

}
