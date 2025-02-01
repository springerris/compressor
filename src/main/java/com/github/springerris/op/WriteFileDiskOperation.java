package com.github.springerris.op;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.Callable;

abstract class WriteFileDiskOperation extends AbstractDiskOperation {

    protected final Callable<InputStream> source;

    public WriteFileDiskOperation(Path file, Callable<InputStream> source) {
        super(file);
        this.source = source;
    }

    //

    protected abstract OpenOption[] openOptions();

    @Override
    public void execute() throws IOException {
        InputStream source;
        try {
            source = this.source.call();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception from source provider", e);
        }

        try (source; OutputStream os = Files.newOutputStream(this.path, this.openOptions())) {
            byte[] buf = new byte[8192];
            int read;
            while ((read = source.read(buf)) != -1) {
                os.write(buf, 0, read);
            }
            os.flush();
        }
    }

}
