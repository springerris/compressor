package com.github.springerris.archive.vfs.cb;

import com.github.springerris.archive.vfs.AbstractVFS;
import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link VFS} implementation which supports only directories and mounts.
 */
public class CorkboardVFS extends AbstractVFS implements VFSEntity {

    private final String name;
    private final Map<String, CorkboardVFSBranch> contents;

    CorkboardVFS(String name) {
        this.name = name;
        this.contents = new HashMap<>();
    }

    public CorkboardVFS() {
        this("");
    }

    //

    @Override
    public VFS sub(String name) {
        if (name.isEmpty()) return this;
        return this.splitPath(
                name,
                this::subDirect,
                VFS::sub
        );
    }

    @Override
    public VFSEntity[] list() {
        Collection<CorkboardVFSBranch> branches = this.contents.values();
        Iterator<CorkboardVFSBranch> iter = branches.iterator();

        int len = branches.size();
        VFSEntity[] ret = new VFSEntity[len];
        for (int i=0; i < len; i++) ret[i] = iter.next().handle();

        return ret;
    }

    @Override
    public VFSEntity stat(String name) {
        if (name.isEmpty()) throw new IllegalArgumentException("Cannot stat empty name");
        return this.splitPath(
                name,
                this::statDirect,
                VFS::stat
        );
    }

    @Override
    public boolean exists(String name) {
        if (name.isEmpty()) throw new IllegalArgumentException("Cannot stat empty name");
        return this.splitPath(
                name,
                this::existsDirect,
                VFS::exists,
                Boolean.FALSE
        );
    }

    @Override
    public InputStream read(String name) throws IOException {
        if (name.isEmpty()) throw new IllegalArgumentException("Cannot open empty name");
        try {
            return this.splitPath(
                    name,
                    this::readDirect,
                    this::readDelegating
            );
        } catch (CorkboardVFSDelegatedIOException e) {
            throw e.getCause();
        }
    }

    @Override
    public OutputStream write(String name) throws IOException {
        if (name.isEmpty()) throw new IllegalArgumentException("Cannot open empty name");
        try {
            return this.splitPath(
                    name,
                    this::writeDirect,
                    this::writeDelegating
            );
        } catch (CorkboardVFSDelegatedIOException e) {
            throw e.getCause();
        }
    }

    @Override
    public void createDirectory(String name) {
        if (name.isEmpty()) throw new IllegalArgumentException("Cannot create directory with empty name");
        this.splitPath(
                name,
                this::createDirectoryDirect,
                this::createDirectoryDelegating
        );
    }

    @Override
    public void mount(String localPath, String remotePath, VFS remote) {
        if (localPath.isEmpty()) throw new IllegalArgumentException("Cannot create mount point with empty name");

        int whereSlash = localPath.indexOf('/');
        if (whereSlash == -1) {
            this.mountDirect(localPath, remotePath, remote);
            return;
        }

        String pre = localPath.substring(0, whereSlash);
        String post = localPath.substring(whereSlash + 1);
        if (post.isEmpty()) {
            this.mountDirect(pre, remotePath, remote);
            return;
        }

        // Special case: create the parent directory if it does not exist
        if (!this.existsDirect(pre)) this.createDirectoryDirect(pre);
        this.subDirect(pre).mount(post, remotePath, remote);
    }

    //

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public long size() {
        return 0L;
    }

    //

    private <T> T splitPath(String name, Function<String, T> whenDirect, BiFunction<VFS, String, T> whenMulti, T whenNotExists) {
        int whereSlash = name.indexOf('/');
        if (whereSlash == -1) return whenDirect.apply(name);

        String pre = name.substring(0, whereSlash);
        String post = name.substring(whereSlash + 1);
        if (post.isEmpty()) return whenDirect.apply(pre);

        if (whenNotExists != null && !this.existsDirect(pre)) return whenNotExists;
        return whenMulti.apply(this.subDirect(pre), post);
    }

    private <T> T splitPath(String name, Function<String, T> whenDirect, BiFunction<VFS, String, T> whenMulti) {
        return this.splitPath(name, whenDirect, whenMulti, null);
    }

    private CorkboardVFSBranch getDirectChild(String name) throws IllegalArgumentException {
        CorkboardVFSBranch branch = this.contents.get(name);
        if (branch == null) throw new IllegalArgumentException("No such entity \"" + name + "\" exists");
        return branch;
    }

    private VFS subDirect(String name) {
        CorkboardVFSBranch branch = this.getDirectChild(name);
        if (branch.isLink()) {
            CorkboardVFSLink link = branch.asLink();
            return link.vfs().sub(link.path());
        } else {
            return branch.asDir();
        }
    }

    private VFSEntity statDirect(String name) {
        return this.getDirectChild(name).handle();
    }

    private boolean existsDirect(String name) {
        return this.contents.containsKey(name);
    }

    private InputStream readDirect(String name) {
        CorkboardVFSBranch branch = this.getDirectChild(name);
        if (branch.isDir()) throw new IllegalArgumentException("Cannot open directory \"" + name + "\" as file");
        CorkboardVFSLink link = branch.asLink();
        try {
            return link.vfs().read(link.path());
        } catch (IOException e) {
            throw new CorkboardVFSDelegatedIOException(e);
        }
    }

    private OutputStream writeDirect(String name) {
        CorkboardVFSBranch branch = this.getDirectChild(name);
        if (branch.isDir()) throw new IllegalArgumentException("Cannot open directory \"" + name + "\" as file");
        CorkboardVFSLink link = branch.asLink();
        try {
            return link.vfs().write(link.path());
        } catch (IOException e) {
            throw new CorkboardVFSDelegatedIOException(e);
        }
    }

    private InputStream readDelegating(VFS vfs, String name) throws CorkboardVFSDelegatedIOException {
        try {
            return vfs.read(name);
        } catch (IOException e) {
            throw new CorkboardVFSDelegatedIOException(e);
        }
    }

    private OutputStream writeDelegating(VFS vfs, String name) throws CorkboardVFSDelegatedIOException {
        try {
            return vfs.write(name);
        } catch (IOException e) {
            throw new CorkboardVFSDelegatedIOException(e);
        }
    }

    private Object createDirectoryDirect(String name) {
        if (this.contents.containsKey(name)) {
            throw new IllegalArgumentException("Entity \"" + name + "\" already exists");
        }

        CorkboardVFS sub = new CorkboardVFS(name);
        this.contents.put(name, CorkboardVFSBranch.of(sub));

        return null;
    }

    private Object createDirectoryDelegating(VFS vfs, String name) throws CorkboardVFSDelegatedIOException {
        try {
            vfs.createDirectory(name);
            return null;
        } catch (IOException e) {
            throw new CorkboardVFSDelegatedIOException(e);
        }
    }

    private void mountDirect(String name, String remotePath, VFS remote) {
        String key = name.toLowerCase(Locale.ROOT);
        if (this.contents.containsKey(key)) {
            throw new IllegalArgumentException("Entity \"" + name + "\" already exists");
        }

        CorkboardVFSLink link = new CorkboardVFSLink(remote, remotePath, name);
        this.contents.put(name, CorkboardVFSBranch.of(link));
    }

}
