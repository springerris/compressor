package com.github.springerris.gui.impl;

import com.github.springerris.archive.vfs.VFSEntity;
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
        super(ctx, I18N.WINDOW_EXPORT_TITLE.get(), 300, 200);
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
        final String zipDesc = I18N.WINDOW_EXPORT_OPTION_ZIP_TYPE_BASIC.get();
        final String encryptedZipDesc = I18N.WINDOW_EXPORT_OPTION_ZIP_TYPE_ENCRYPTED.get();

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
        String password = this.passwordPrompt(true);

        String zipName = JOptionPane.showInputDialog(I18N.SEND_PICK_NAME.get());
        if (zipName == null || zipName.isBlank()) {
            // TODO: something will go here
            return;
        }

        yd.mkdir("disk:/.archives", true);

        String ext = (password == null) ? ".zip" : ".zip.m64";
        NodeUploader nu = yd.upload("disk:/.archives/" + zipName + ext);
        try (OutputStream os = nu.open()) {
            this.ctx.archive().write(os, password);
        } catch (IOException ex) {
            this.ctx.logger().log(Level.SEVERE, "Ошибка работы с сервисом Yandex Disk", ex);
            this.showError("""
                    Ошибка работы с сервисом Yandex Disk. Проверьте: \
                    1) Есть ли у вас доступ к интернету              \
                    2) Доступен ли сервис Yandex на данный момент"""
            );
            System.exit(1);
        }
    }

    private void onClickChoice2() {
        SSHHandler handler = this.popup(SftpConnectWindow.class);
        if (handler == null) return; // User aborted

        // TODO
        // Alert the listing for debug purposes
        try {
            VFSEntity[] list = handler.vfs().list();
            for (VFSEntity ent : list) {
                this.showInfo((ent.isDirectory() ? "D" : "F") + " > " + ent.name());
            }
        } catch (IOException e) {
            this.ctx.logger().log(Level.WARNING, "Unexpected error", e);
        }

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
