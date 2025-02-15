package com.github.springerris.op;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * @see DiskOperation
 */
@ApiStatus.Internal
abstract class AbstractDiskOperation implements DiskOperation {

    protected final Path path;
    public AbstractDiskOperation(@NotNull Path path) {
        this.path = path;
    }

    //

    protected final @NotNull String suffix() {
        return ": " + this.path.toAbsolutePath();
    }

}
