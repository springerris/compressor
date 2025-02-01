package com.github.springerris.op;

import java.nio.file.Path;

abstract class AbstractDiskOperation implements DiskOperation {

    protected final Path path;
    public AbstractDiskOperation(Path path) {
        this.path = path;
    }

    //

    protected final String suffix() {
        return ": " + this.path.toAbsolutePath();
    }

}
