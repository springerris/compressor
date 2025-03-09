package com.github.springerris.archive.vfs.yandisk;

import com.github.springerris.archive.vfs.VFSEntity;
import io.github.wasabithumb.yandisk4j.node.FileNode;
import io.github.wasabithumb.yandisk4j.node.Node;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
record YanDiskVFSEntity(
        @NotNull Node handle
) implements VFSEntity {

    @Override
    public @NotNull String name() {
        return this.handle.name();
    }

    @Override
    public boolean isFile() {
        return this.handle.isFile();
    }

    @Override
    public boolean isDirectory() {
        return this.handle.isDirectory();
    }

    @Override
    public long size() {
        if (this.handle.isFile()) {
            return ((FileNode) this.handle).size();
        }
        return 0L;
    }

}
