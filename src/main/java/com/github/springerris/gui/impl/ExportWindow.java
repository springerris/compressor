package com.github.springerris.gui.impl;

import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import com.github.springerris.archive.vfs.yandisk.YanDiskVFS;
import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.ChoiceWindow;
import com.github.springerris.i18n.I18N;
import com.github.springerris.token.TokenType;
import com.github.springerris.util.SSHHandler;
import io.github.wasabithumb.yandisk4j.YanDisk;
import io.github.wasabithumb.yandisk4j.node.accessor.NodeUploader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

public class ExportWindow extends ChoiceWindow {

    public ExportWindow(@NotNull WindowContext ctx) {
        super(ctx, I18N.WINDOW_EXPORT_TITLE, 300, 200);
    }

    //

    @Override
    protected @NotNull String @NotNull [] getChoices() {
        return new String[] {
                I18N.WINDOW_EXPORT_OPTION_ZIP.get(),
                I18N.WINDOW_EXPORT_OPTION_YANDEX.get(),
                I18N.WINDOW_EXPORT_OPTION_SFTP.get()
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
        final String zipDesc = I18N.FILE_TYPE_ZIP.get();
        final String encryptedZipDesc = I18N.FILE_TYPE_ZIP_ENCRYPTED.get();

        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());
        jfc.setFileFilter(new FileNameExtensionFilter(zipDesc, "zip"));
        jfc.setFileFilter(new FileNameExtensionFilter(encryptedZipDesc, "m64"));

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
        YanDisk yd = YanDisk.yanDisk(this.ctx.tokens().get(TokenType.YANDEX_DISK));

        VFS vfs = VFS.yanDisk(yd);
        this.ctx.setRemote(vfs);
        this.popup(RemoteExportWindow.class);
    }

    private void onClickChoice2() {
        SSHHandler handler = this.popup(SftpConnectWindow.class);
        if (handler == null) return; // User aborted

        this.ctx.setRemote(handler.vfs());
        this.popup(RemoteExportWindow.class);

        try {
            handler.close();
        } catch (IOException ignored) { }
    }

    //

    @Contract("false -> !null")
    private @Nullable String passwordPrompt(boolean ask) {
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
        return this.pester(I18N.STAGE_PASSWORD_PROMPT_ENTER);
    }

}
