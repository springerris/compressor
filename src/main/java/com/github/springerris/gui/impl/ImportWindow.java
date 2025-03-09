package com.github.springerris.gui.impl;

import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.ChoiceWindow;
import com.github.springerris.i18n.I18N;
import com.github.springerris.token.TokenType;
import com.github.springerris.util.SSHHandler;
import io.github.wasabithumb.yandisk4j.YanDisk;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public class ImportWindow extends ChoiceWindow {

    public ImportWindow(@NotNull WindowContext ctx) {
        super(ctx, I18N.WINDOW_IMPORT_TITLE, 300, 200);
    }

    //

    @Override
    protected @NotNull String @NotNull [] getChoices() {
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
    }

    //

    private void onClickChoice0() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(new FileNameExtensionFilter(
                I18N.FILE_TYPE_ZIP.get(),
                "zip"
        ));
        jfc.setFileFilter(new FileNameExtensionFilter(
                I18N.FILE_TYPE_ZIP_ENCRYPTED.get(),
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
        YanDisk yd = YanDisk.yanDisk(this.ctx.tokens().get(TokenType.YANDEX_DISK));

        VFS vfs = VFS.yanDisk(yd);
        this.ctx.setRemote(vfs);
        this.popup(RemoteImportWindow.class);
    }

    private void onClickChoice2() {
        SSHHandler handler = this.popup(SftpConnectWindow.class);
        if (handler == null) return; // User aborted

        this.ctx.setRemote(handler.vfs());
        this.popup(RemoteImportWindow.class);

        try {
            handler.close();
        } catch (IOException ignored) { }
    }

    //

    private void loadArchive(@NotNull Path p) {
        try {
            this.ctx.loadArchive(p, this::passwordPrompt);
        } catch (IllegalArgumentException e) {
            this.showError(I18N.WINDOW_IMPORT_ERROR_FORMAT);
        } catch (IllegalStateException e) {
            this.showError(I18N.WINDOW_IMPORT_ERROR_PASSWORD);
        } catch (IOException e) {
            this.ctx.logger().log(Level.WARNING, "Failed to extract", e);
            System.exit(1);
        }
    }

    private @NotNull String passwordPrompt() {
        return this.pester(I18N.STAGE_PASSWORD_PROMPT_ENTER);
    }

}
