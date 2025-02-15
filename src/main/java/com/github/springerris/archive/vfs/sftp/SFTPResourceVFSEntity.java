package com.github.springerris.archive.vfs.sftp;

import com.github.springerris.archive.vfs.VFSEntity;
import net.schmizz.sshj.sftp.RemoteResourceInfo;

public record SFTPResourceVFSEntity(
        RemoteResourceInfo handle
) implements VFSEntity {

    @Override
    public String name() {
        return this.handle.getName();
    }

    @Override
    public boolean isFile() {
        return this.handle.isRegularFile();
    }

    @Override
    public boolean isDirectory() {
        return this.handle.isDirectory();
    }

    @Override
    public long size() {
        return this.handle.getAttributes().getSize();
    }

}
