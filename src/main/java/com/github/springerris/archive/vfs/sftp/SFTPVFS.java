package com.github.springerris.archive.vfs.sftp;

import com.github.springerris.archive.vfs.AbstractVFS;
import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import com.github.springerris.archive.vfs.fs.FilesystemVFS;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.RemoteResource;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class SFTPVFS extends AbstractVFS {

    private final SFTPClient sftpClient;
    private final RemoteResource root;

    public SFTPVFS(RemoteResource root, SFTPClient sftpClient) {
        this.sftpClient = sftpClient;
        this.root = root;
    }

    @Override
    public VFS sub(String name) {
        try {
            RemoteResource rs = this.sftpClient.open("./" + name);
            List<RemoteResourceInfo> rri = this.sftpClient.ls("./");
            RemoteResourceInfo rriSpecific = null;
            for (RemoteResourceInfo r : rri) {
                if (Objects.equals(r.getName(), name)) {
                    rriSpecific = r;
                }
            }
            if (rriSpecific != null) {
                if (rriSpecific.isDirectory()) {
                    throw new IllegalArgumentException("Path \"" + rriSpecific.getPath() + "\" is not a directory");
                }
            }

            return new SFTPVFS(rs,sftpClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public VFSEntity[] list() throws IOException {
        return new VFSEntity[0];
    }

    @Override
    public VFSEntity stat(String name) {
        RemoteResource rs;
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

}
