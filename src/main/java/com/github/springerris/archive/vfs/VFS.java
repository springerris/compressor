package com.github.springerris.archive.vfs;

import com.github.springerris.archive.vfs.cb.CorkboardVFS;
import com.github.springerris.archive.vfs.fs.FilesystemVFS;
import com.github.springerris.archive.vfs.sftp.SFTPVFS;
import com.github.springerris.archive.vfs.yandisk.YanDiskVFS;
import com.github.springerris.archive.vfs.zip.ZipVFS;
import io.github.wasabithumb.yandisk4j.YanDisk;
import net.schmizz.sshj.sftp.SFTPClient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
    @Contract("-> new")
    static @NotNull VFS empty() {
        return new CorkboardVFS();
    }

    /**
     * Creates a VFS which represents the content of the specified directory on the filesystem.
     */
    @Contract("_ -> new")
    static @NotNull VFS directory(@NotNull Path path) {
        if (!Files.isDirectory(path))
            throw new IllegalArgumentException("Path \"" + path.toAbsolutePath() + "\" is not a directory");
        return new FilesystemVFS(path);
    }

    /**
     * Creates a VFS which represents the content of the specified ZIP on the filesystem.
     * @param key If not null, assumes the ZIP is encrypted and uses this as the decryption key.
     */
    @Contract("_, _ -> new")
    static @NotNull VFS zip(Path path, byte[] key) {
        if (!Files.isRegularFile(path))
            throw new IllegalArgumentException("Path \"" + path.toAbsolutePath() + "\" is not a file");
        return new ZipVFS(path.toFile(), key);
    }

    /**
     * Creates a VFS which represents the content of the specified unencrypted ZIP on the filesystem.
     * @see #zip(Path, byte[])
     */
    @Contract("_ -> new")
    static @NotNull VFS zip(Path path) {
        return zip(path, null);
    }

    /**
     * Creates a VFS which represents the content served through the connection of the specified SFTP client.
     */
    @Contract("_ -> new")
    static @NotNull VFS sftp(@NotNull SFTPClient client) {
        return new SFTPVFS(client);
    }

    /**
     * Creates a VFS which represents the content of Yandex Disk storage.
     * @param app If true, reads the tree at {@code app:/}. Otherwise, reads the tree at {@code disk:/}.
     */
    @Contract("_, _ -> new")
    static @NotNull VFS yanDisk(@NotNull YanDisk instance, boolean app) {
        return new YanDiskVFS(instance, app);
    }

    /**
     * Creates a VFS which represents the content of Yandex Disk storage.
     * Alias for {@code yanDisk(instance, false)}.
     * @see #yanDisk(YanDisk, boolean)
     */
    @Contract("_ -> new")
    static @NotNull VFS yanDisk(@NotNull YanDisk instance) {
        return yanDisk(instance, false);
    }

    //

    /** Enters the named directory */
    @NotNull VFS sub(@NotNull String name);

    /** Lists all entities at the root of the VFS */
    @NotNull VFSEntity @NotNull [] list() throws IOException;

    /** Reports info about the entity with the given name */
    @NotNull VFSEntity stat(@NotNull String name);

    /** Returns true if an entity with the given name exists */
    boolean exists(@NotNull String name);

    /** Reads the named file */
    @NotNull InputStream read(@NotNull String name) throws IOException;

    /** Writes the named file */
    @NotNull OutputStream write(@NotNull String name) throws IOException;

    /** Creates a new directory with the given name */
    void createDirectory(@NotNull String name) throws IOException;

    /** Mounts the given file such that the given name resolves to the same resource (file or directory) */
    void mount(
            @NotNull String localPath,
            @NotNull String remotePath,
            @NotNull VFS remote
    ) throws UnsupportedOperationException;

}
