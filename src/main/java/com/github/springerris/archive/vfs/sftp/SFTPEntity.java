package com.github.springerris.archive.vfs.sftp;

import com.github.springerris.archive.vfs.VFSEntity;
import net.schmizz.sshj.sftp.RemoteResourceInfo;

public record SFTPEntity(
        RemoteResourceInfo handle
) implements VFSEntity {

    @Override
    public String name() {
        return handle.getName();
    }

    @Override
    public boolean isFile() {
        return handle.isRegularFile();
    }

    @Override
    public boolean isDirectory() {
        return handle.isDirectory();
    }

    @Override
    public long size() {
        return handle.getAttributes().getSize();
    }

}
