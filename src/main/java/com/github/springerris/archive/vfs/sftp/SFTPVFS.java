package com.github.springerris.archive.vfs.sftp;

import com.github.springerris.archive.vfs.AbstractVFS;
import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import com.github.springerris.util.ssh.RemoteFileInputStream;
import com.github.springerris.util.ssh.RemoteFileOutputStream;
import net.schmizz.sshj.sftp.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.List;

public class SFTPVFS extends AbstractVFS {

    private final SFTPClient client;
    private final String root;

    private SFTPVFS(SFTPClient client, String root) {
        this.client = client;
        this.root = root;
    }

    public SFTPVFS(SFTPClient client) {
        this(client, "./");
    }

    //

    @Override
    public VFS sub(String name) {
        String subName = this.root;
        if (!name.isEmpty()) {
            subName += name;
            if (name.charAt(name.length() - 1) != '/') subName += "/";
        }
        return new SFTPVFS(this.client, subName);
    }

    @Override
    public VFSEntity[] list() throws IOException {
        List<RemoteResourceInfo> list = this.client.ls(this.root);
        int size = list.size();
        VFSEntity[] ret = new VFSEntity[size];
        for (int i=0; i < size; i++)
            ret[i] = new SFTPResourceVFSEntity(list.get(i));
        return ret;
    }

    @Override
    public VFSEntity stat(String name) {
        FileAttributes attrs;
        try {
            attrs = this.client.stat(this.root + name);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to stat \"" + name + "\"", e);
        }
        int whereSlash = name.lastIndexOf('/');
        if (whereSlash != -1) name = name.substring(whereSlash + 1);
        return new SFTPAttributesVFSEntity(name, attrs);
    }

    @Override
    public boolean exists(String name) {
        FileAttributes attrs;
        try {
            attrs = this.client.stat(this.root + name);
        } catch (IOException e) {
            return false;
        }
        return attrs.getType() != FileMode.Type.UNKNOWN;
    }

    @Override
    public InputStream read(String name) throws IOException {
        RemoteFile rf = this.client.open(this.root + name, EnumSet.of(OpenMode.READ));
        return new RemoteFileInputStream(rf);
    }

    @Override
    public OutputStream write(String name) throws IOException {
        RemoteFile rf = this.client.open(this.root + name, EnumSet.of(OpenMode.WRITE, OpenMode.CREAT, OpenMode.TRUNC));
        return new RemoteFileOutputStream(rf);
    }

    @Override
    public void createDirectory(String name) throws IOException {
        this.client.mkdirs(this.root + name);
    }

    @Override
    public void mount(String localPath, String remotePath, VFS remote) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot mount to SFTPVFS");
    }

}
