package com.github.springerris.archive.vfs.sftp;

import com.github.springerris.archive.vfs.VFSEntity;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.FileMode;

public record SFTPAttributesVFSEntity(
        String name,
        FileAttributes attributes
) implements VFSEntity {

    @Override
    public boolean isFile() {
        return this.attributes.getType() == FileMode.Type.REGULAR;
    }

    @Override
    public boolean isDirectory() {
        return this.attributes.getType() == FileMode.Type.DIRECTORY;
    }

    @Override
    public long size() {
        return this.attributes.getSize();
    }

}
