package com.github.springerris.archive.vfs.yandisk;

import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import io.github.wasabithumb.yandisk4j.YanDisk;
import io.github.wasabithumb.yandisk4j.except.YanDiskAPIException;
import io.github.wasabithumb.yandisk4j.except.YanDiskIOException;
import io.github.wasabithumb.yandisk4j.except.YanDiskOperationException;
import io.github.wasabithumb.yandisk4j.node.Node;
import io.github.wasabithumb.yandisk4j.node.path.NodePath;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public class YanDiskVFS implements VFS {

    private final YanDisk handle;
    private final NodePath root;

    private YanDiskVFS(@NotNull YanDisk handle, @NotNull NodePath root) {
        this.handle = handle;
        this.root = root;
    }

    public YanDiskVFS(@NotNull YanDisk handle, boolean app) {
        this(handle, NodePath.parse(app ? "app:/" : "disk:/"));
    }

    //

    private @NotNull NodePath resolve(@NotNull String name) {
        if (name.isEmpty()) return this.root;
        return NodePath.join(this.root, NodePath.parse(name));
    }

    private @NotNull NodePath parent(@NotNull NodePath path) {
        List<CharSequence> parts = path.parts();
        int len = parts.size();
        if (len == 0)
            throw new IllegalArgumentException("Cannot get parent path of " + path + " (is root)");

        List<String> shift = new ArrayList<>(--len);
        for (int i=0; i < len; i++) {
            shift.add(parts.get(i).toString());
        }

        //noinspection UnstableApiUsage
        return NodePath.of(path.protocol(), shift);
    }

    //

    @Override
    public @NotNull YanDiskVFS sub(@NotNull String name) {
        return new YanDiskVFS(this.handle, this.resolve(name));
    }

    @Override
    public @NotNull VFSEntity @NotNull [] list() throws IOException {
        final int pageSize = 20;
        int capacity = pageSize;
        int len = 0;
        VFSEntity[] ret = new VFSEntity[capacity];

        List<Node> next;
        while (true) {
            try {
                next = this.handle.list(this.root, pageSize, len);
            } catch (YanDiskIOException io) {
                throw io.getCause();
            }

            for (Node n : next) {
                ret[len++] = new YanDiskVFSEntity(n);
            }
            if (next.size() < pageSize) break;

            int newCapacity = capacity + pageSize;
            VFSEntity[] cpy = new VFSEntity[newCapacity];
            System.arraycopy(ret, 0, cpy, 0, capacity);
            ret = cpy;
            capacity = newCapacity;
        }

        if (len < capacity) {
            VFSEntity[] shrink = new VFSEntity[len];
            System.arraycopy(ret, 0, shrink, 0, len);
            ret = shrink;
        }
        return ret;
    }

    @Override
    public @NotNull VFSEntity stat(@NotNull String name) {
        NodePath path = this.resolve(name);
        NodePath dir = this.parent(path);
        CharSequence n = path.parts().getLast();

        for (Node node : this.handle.list(dir)) {
            if (CharSequence.compare(node.name(), n) == 0) return new YanDiskVFSEntity(node);
        }

        throw new IllegalArgumentException("No entity exists at path " + path);
    }

    @Override
    public boolean exists(@NotNull String name) {
        NodePath path = this.resolve(name);
        NodePath dir = this.parent(path);
        CharSequence n = path.parts().getLast();

        try {
            for (Node node : this.handle.list(dir)) {
                if (CharSequence.compare(node.name(), n) == 0) return true;
            }
        } catch (YanDiskAPIException | YanDiskOperationException ignored) { }

        return false;
    }

    @Override
    public @NotNull InputStream read(@NotNull String name) throws IOException {
        try {
            return this.handle.download(this.resolve(name)).open();
        } catch (YanDiskIOException io) {
            throw io.getCause();
        }
    }

    @Override
    public @NotNull OutputStream write(@NotNull String name) throws IOException {
        try {
            return this.handle.upload(this.resolve(name), true).open();
        } catch (YanDiskIOException io) {
            throw io.getCause();
        }
    }

    @Override
    public void createDirectory(@NotNull String name) throws IOException {
        try {
            this.handle.mkdir(this.resolve(name), true);
        } catch (YanDiskIOException io) {
            throw io.getCause();
        }
    }

    @Override
    public void mount(@NotNull String localPath, @NotNull String remotePath, @NotNull VFS remote) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot mount to YanDiskVFS");
    }

}
