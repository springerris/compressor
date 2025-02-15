package com.github.springerris.archive.vfs.zip;

import com.github.springerris.archive.vfs.VFSEntity;

import java.util.zip.ZipEntry;

record ZipVFSEntity(
        ZipEntry handle,
        String name
) implements VFSEntity {

    @Override
    public boolean isFile() {
        return !this.handle.isDirectory();
    }

    @Override
    public boolean isDirectory() {
        return this.handle.isDirectory();
    }

    @Override
    public long size() {
        return this.handle.getSize();
    }

}
