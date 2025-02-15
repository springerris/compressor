package com.github.springerris.archive.vfs.fs;

import com.github.springerris.archive.vfs.AbstractVFS;
import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

/**
 * A {@link VFS} representing a directory on the actual filesystem
 */
@ApiStatus.Internal
public class FilesystemVFS extends AbstractVFS {

    private final Path root;
    public FilesystemVFS(Path root) {
        this.root = root;
    }

    private Path resolve(String str) {
        Path ret = this.root;
        int start = 0;
        for (int i=0; i < str.length(); i++) {
            if (str.charAt(i) != '/') continue;
            ret = ret.resolve(str.substring(start, i));
            start = i + 1;
        }
        if (start != str.length())
            ret = ret.resolve(str.substring(start));
        return ret;
    }

    @Override
    public @NotNull VFS sub(@NotNull String name) {
        Path target = this.resolve(name);
        if (!Files.isDirectory(target))
            throw new IllegalArgumentException("Path \"" + target.toAbsolutePath() + "\" is not a directory");
        return new FilesystemVFS(target);
    }

    @Override
    public @NotNull VFSEntity @NotNull [] list() throws IOException {
        try (Stream<Path> files = Files.list(this.root)) {
            return files
                    .map(FilesystemVFSEntity::new)
                    .map(VFSEntity.class::cast)
                    .toArray(VFSEntity[]::new);
        }
    }

    @Override
    public @NotNull VFSEntity stat(@NotNull String name) {
        Path p = this.resolve(name);
        if (!Files.exists(p))
            throw new IllegalArgumentException("Path \"" + p.toAbsolutePath() + "\" does not exist");
        return new FilesystemVFSEntity(p);
    }

    @Override
    public boolean exists(@NotNull String name) {
        return Files.exists(this.resolve(name));
    }

    @Override
    public @NotNull InputStream read(@NotNull String name) throws IOException {
        return Files.newInputStream(this.resolve(name), StandardOpenOption.READ);
    }

    @Override
    public @NotNull OutputStream write(@NotNull String name) throws IOException {
        return Files.newOutputStream(this.resolve(name), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public void createDirectory(@NotNull String name) throws IOException {
        Files.createDirectory(this.resolve(name));
    }

    @Override
    public void mount(@NotNull String localPath, @NotNull String remotePath, @NotNull VFS remote) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot mount to FilesystemVFS");
    }

}
