package com.github.springerris.archive.vfs.cb;

import java.io.IOException;

/**
 * Wraps an IOException from another VFS, for rethrowing
 */
class CorkboardVFSDelegatedIOException extends RuntimeException {

    public CorkboardVFSDelegatedIOException(IOException cause) {
        super("Intent to rethrow an IO exception", cause);
    }

    @Override
    public synchronized IOException getCause() {
        return (IOException) super.getCause();
    }

}
