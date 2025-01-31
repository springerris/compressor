package com.github.springerris.archive.vfs;

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
