package com.github.springerris.archive.vfs;

import com.github.springerris.archive.vfs.cb.CorkboardVFS;
import com.github.springerris.archive.vfs.fs.FilesystemVFS;
import com.github.springerris.archive.vfs.sftp.SFTPVFS;
import com.github.springerris.archive.vfs.zip.ZipVFS;
import net.schmizz.sshj.sftp.SFTPClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Abstraction for a filesystem. Paths always take the form {@code a/b/c}, with no leading or trailing slashes.
 */
public interface VFS {

    /**
     * Creates an empty VFS. Cannot store files directly; but can create directories
     * and can mount other VFS instances.
     */
    static VFS empty() {
        return new CorkboardVFS();
    }

    /**
     * Creates a VFS which represents the content of the specified directory on the filesystem.
     */
    static VFS directory(Path path) {
        if (!Files.isDirectory(path))
            throw new IllegalArgumentException("Path \"" + path.toAbsolutePath() + "\" is not a directory");
        return new FilesystemVFS(path);
    }

    /**
     * Creates a VFS which represents the content of the specified ZIP on the filesystem.
     * @param key If not null, assumes the ZIP is encrypted and uses this as the decryption key.
     */
    static VFS zip(Path path, byte[] key) {
        if (!Files.isRegularFile(path))
            throw new IllegalArgumentException("Path \"" + path.toAbsolutePath() + "\" is not a file");
        return new ZipVFS(path.toFile(), key);
    }

    /**
     * Creates a VFS which represents the content of the specified unencrypted ZIP on the filesystem.
     * @see #zip(Path, byte[])
     */
    static VFS zip(Path path) {
        return zip(path, null);
    }

    /**
     * Creates a VFS which represents the content served through the connection of the specified SFTP client.
     */
    static VFS sftp(SFTPClient client) {
        return new SFTPVFS(client);
    }

    //

    /** Enters the named directory */
    VFS sub(String name);

    /** Lists all entities at the root of the VFS */
    VFSEntity[] list() throws IOException;

    /** Reports info about the entity with the given name */
    VFSEntity stat(String name);

    /** Returns true if an entity with the given name exists */
    boolean exists(String name);

    /** Reads the named file */
    InputStream read(String name) throws IOException;

    /** Writes the named file */
    OutputStream write(String name) throws IOException;

    /** Creates a new directory with the given name */
    void createDirectory(String name) throws IOException;

    /** Mounts the given file such that the given name resolves to the same resource (file or directory) */
    void mount(String localPath, String remotePath, VFS remote) throws UnsupportedOperationException;

}
