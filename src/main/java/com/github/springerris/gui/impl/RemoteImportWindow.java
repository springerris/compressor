package com.github.springerris.gui.impl;

import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.gui.Modal;
import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.component.VFSPicker;
import com.github.springerris.gui.helper.RemotePickerWindow;
import com.github.springerris.i18n.I18N;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

public class RemoteImportWindow extends RemotePickerWindow implements Modal.Nullary {

    public RemoteImportWindow(@NotNull WindowContext ctx) {
        super(ctx, I18N.WINDOW_MAIN_BUTTON_IMPORT, 500, 500);
    }

    //

    @Override
    protected void onPick() {
        VFSPicker.Selection selection = this.picker.selection();
        if (!selection.isExisting() || !selection.canBeFile()) return;

        this.setVisible(false);

        Path out;
        try {
            out = Files.createTempFile(selection.name(), null);
        } catch (IOException e) {
            this.ctx.logger().log(Level.WARNING, "Failed to create temporary file", e);
            this.dispose();
            return;
        }
        out.toFile().deleteOnExit();

        VFS vfs = selection.vfs();
        String path = selection.path();

        try (InputStream is = vfs.read(path);
             OutputStream os = Files.newOutputStream(out, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
        ) {
            byte[] buf = new byte[8192];
            int read;
            while ((read = is.read(buf)) != -1) {
                os.write(buf, 0, read);
            }
        } catch (IOException e) {
            try {
                Files.deleteIfExists(out);
            } catch (IOException ignored) { }
            this.ctx.logger().log(Level.WARNING, "Broken pipe", e);
            this.dispose();
            return;
        }

        try {
            this.ctx.loadArchive(out, () -> this.pester(I18N.STAGE_PASSWORD_PROMPT_ENTER));
        } catch (IOException e) {
            this.ctx.logger().log(Level.WARNING, "Failed to load archive", e);
        } finally {
            this.dispose();
        }
    }

    @Override
    protected boolean isWriting() {
        return false;
    }

}
