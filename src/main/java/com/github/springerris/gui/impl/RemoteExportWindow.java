package com.github.springerris.gui.impl;

import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.gui.Modal;
import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.component.VFSPicker;
import com.github.springerris.gui.helper.RemotePickerWindow;
import com.github.springerris.i18n.I18N;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;

public class RemoteExportWindow extends RemotePickerWindow implements Modal.Nullary {

    public RemoteExportWindow(@NotNull WindowContext ctx) {
        super(ctx, I18N.WINDOW_MAIN_BUTTON_EXPORT, 500, 500);
    }

    @Override
    protected void onPick() {
        VFSPicker.Selection selection = this.picker.selection();
        if (!selection.canBeFile()) return;

        VFS vfs = selection.vfs();
        String path = selection.path();
        String password = selection.name().endsWith(".m64") ? this.pester(I18N.STAGE_PASSWORD_PROMPT_ENTER) : null;

        this.setVisible(false);

        try (OutputStream os = vfs.write(path)) {
            this.ctx.archive().write(os, password);
        } catch (IOException e) {
            this.ctx.logger().log(Level.WARNING, "Failed to write file", e);
        } finally {
            this.dispose();
        }
    }

    @Override
    protected boolean isWriting() {
        return true;
    }

}
