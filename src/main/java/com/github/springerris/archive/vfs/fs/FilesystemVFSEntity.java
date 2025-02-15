package com.github.springerris.archive.vfs.fs;

import com.github.springerris.archive.vfs.VFSEntity;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

record FilesystemVFSEntity(
        Path handle
) implements VFSEntity {

    @Override
    public @NotNull String name() {
        return this.handle.getFileName().toString();
    }

    @Override
    public boolean isFile() {
        return Files.isRegularFile(this.handle);
    }

    @Override
    public boolean isDirectory() {
        return Files.isDirectory(this.handle);
    }

    @Override
    public long size() {
        try {
            return Files.size(this.handle);
        } catch (IOException ignored) {
            return -1L;
        }
    }

}
