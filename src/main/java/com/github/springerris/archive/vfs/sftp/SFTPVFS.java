package com.github.springerris.archive.vfs.sftp;

import com.github.springerris.archive.vfs.AbstractVFS;
import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SFTPVFS extends AbstractVFS implements VFSEntity {

    @Override
    public VFS sub(String name) {
        return null;
    }

    @Override
    public VFSEntity[] list() throws IOException {
        return new VFSEntity[0];
    }

    @Override
    public VFSEntity stat(String name) {
        return null;
    }

    @Override
    public boolean exists(String name) {
        return false;
    }

    @Override
    public InputStream read(String name) throws IOException {
        return null;
    }

    @Override
    public OutputStream write(String name) throws IOException {
        return null;
    }

    @Override
    public void createDirectory(String name) throws IOException {

    }

    @Override
    public void mount(String localPath, String remotePath, VFS remote) throws UnsupportedOperationException {

    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long size() {
        return 0;
    }
}
