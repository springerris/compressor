package com.github.springerris.archive.vfs.sftp;

import com.github.springerris.archive.vfs.AbstractVFS;
import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import com.github.springerris.util.ssh.RemoteFileInputStream;
import com.github.springerris.util.ssh.RemoteFileOutputStream;
import net.schmizz.sshj.sftp.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.List;

@ApiStatus.Internal
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
    public @NotNull VFS sub(@NotNull String name) {
        String subName = this.root;
        if (!name.isEmpty()) {
            subName += name;
            if (name.charAt(name.length() - 1) != '/') subName += "/";
        }
        return new SFTPVFS(this.client, subName);
    }

    @Override
    public @NotNull VFSEntity @NotNull [] list() throws IOException {
        List<RemoteResourceInfo> list = this.client.ls(this.root);
        int size = list.size();
        VFSEntity[] ret = new VFSEntity[size];
        for (int i=0; i < size; i++)
            ret[i] = new SFTPResourceVFSEntity(list.get(i));
        return ret;
    }

    @Override
    public @NotNull VFSEntity stat(@NotNull String name) {
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
    public boolean exists(@NotNull String name) {
        FileAttributes attrs;
        try {
            attrs = this.client.stat(this.root + name);
        } catch (IOException e) {
            return false;
        }
        return attrs.getType() != FileMode.Type.UNKNOWN;
    }

    @Override
    public @NotNull InputStream read(@NotNull String name) throws IOException {
        RemoteFile rf = this.client.open(this.root + name, EnumSet.of(OpenMode.READ));
        return new RemoteFileInputStream(rf);
    }

    @Override
    public @NotNull OutputStream write(@NotNull String name) throws IOException {
        RemoteFile rf = this.client.open(this.root + name, EnumSet.of(OpenMode.WRITE, OpenMode.CREAT, OpenMode.TRUNC));
        return new RemoteFileOutputStream(rf);
    }

    @Override
    public void createDirectory(@NotNull String name) throws IOException {
        this.client.mkdirs(this.root + name);
    }

    @Override
    public void mount(@NotNull String localPath, @NotNull String remotePath, @NotNull VFS remote) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot mount to SFTPVFS");
    }

}
