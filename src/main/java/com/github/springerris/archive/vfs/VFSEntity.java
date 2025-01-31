package com.github.springerris.archive.vfs;

public interface VFSEntity {

    String name();

    boolean isFile();

    boolean isDirectory();

    long size();

}
