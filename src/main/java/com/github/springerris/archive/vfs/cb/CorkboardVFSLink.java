package com.github.springerris.archive.vfs.cb;

import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import org.jetbrains.annotations.NotNull;

/**
 * An entity which defers to an entity in a foreign VFS.
 */
class CorkboardVFSLink implements VFSEntity {

    private final VFS vfs;
    private final String path;
    private final String name;
    private final boolean dir;

    public CorkboardVFSLink(VFS vfs, String path, String name, boolean dir) {
        this.vfs = vfs;
        this.path = path;
        this.name = name;
        this.dir = dir;
    }

    public CorkboardVFSLink(VFS vfs, String path, String name) {
        this(vfs, path, name, vfs.stat(path).isDirectory());
    }

    //

    public @NotNull VFS vfs() {
        return this.vfs;
    }

    public @NotNull String path() {
        return this.path;
    }

    //

    @Override
    public @NotNull String name() {
        return this.name;
    }

    @Override
    public boolean isFile() {
        return !this.dir;
    }

    @Override
    public boolean isDirectory() {
        return this.dir;
    }

    @Override
    public long size() {
        return this.dir ? 0L : this.vfs.stat(this.name).size();
    }

}
