package com.github.springerris.op;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * @see DiskOperation
 */
@ApiStatus.Internal
abstract class WritingDiskOperation extends AbstractDiskOperation {

    protected final Callable<InputStream> source;

    public WritingDiskOperation(@NotNull Path file, @NotNull Callable<InputStream> source) {
        super(file);
        this.source = source;
    }

    //

    protected @NotNull InputStream open() throws IOException {
        try {
            return this.source.call();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception from source provider", e);
        }
    }

    protected void pipe(@NotNull InputStream is, @NotNull OutputStream os) throws IOException {
        byte[] buf = new byte[8192];
        int read;
        while ((read = is.read(buf)) != -1) {
            os.write(buf, 0, read);
        }
        os.flush();
    }

}
