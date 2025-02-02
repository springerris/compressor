package com.github.springerris.archive.vfs;

/**
 * A file or directory in a {@link VFS}. Note that it is possible for
 * {@link #isFile()} and {@link #isDirectory()} to both be false if obtained from a
 * {@link com.github.springerris.archive.vfs.fs.FilesystemVFS FilesystemVFS} and the entity
 * represents a non-normal file (e.g. symbolic link).
 */
public interface VFSEntity extends Comparable<VFSEntity> {

    String name();

    boolean isFile();

    boolean isDirectory();

    long size();

    @Override
    default int compareTo(VFSEntity other) {
        boolean aDir = this.isDirectory();
        boolean bDir = other.isDirectory();
        if (aDir != bDir) return aDir ? -1 : 1;
        return CharSequence.compare(this.name(), other.name());
    }

}
