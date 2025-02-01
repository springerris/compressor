package com.github.springerris.op;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.concurrent.Callable;

abstract class WritingDiskOperation extends AbstractDiskOperation {

    protected final Callable<InputStream> source;

    public WritingDiskOperation(Path file, Callable<InputStream> source) {
        super(file);
        this.source = source;
    }

    //

    protected InputStream open() throws IOException {
        try {
            return this.source.call();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception from source provider", e);
        }
    }

    protected void pipe(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[8192];
        int read;
        while ((read = is.read(buf)) != -1) {
            os.write(buf, 0, read);
        }
        os.flush();
    }

}
