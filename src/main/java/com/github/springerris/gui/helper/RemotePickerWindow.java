package com.github.springerris.gui.helper;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.component.VFSPicker;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class RemotePickerWindow extends BorderWindow {

    protected VFSPicker picker;

    public RemotePickerWindow(
            @NotNull WindowContext ctx,
            @NotNull CharSequence title,
            int width,
            int height
    ) {
        super(ctx, title, width, height);
    }

    @Override
    protected void setupContent() {
        this.picker = new VFSPicker(this.ctx.getRemote(), this.isWriting());
        this.picker.addActionListener(this::onPickEvent);
        this.addElement(this.picker, BorderLayout.CENTER);
    }

    private void onPickEvent(ActionEvent ignored) {
        this.onPick();
    }

    @ApiStatus.OverrideOnly
    protected abstract void onPick();

    @ApiStatus.OverrideOnly
    protected abstract boolean isWriting();

}
