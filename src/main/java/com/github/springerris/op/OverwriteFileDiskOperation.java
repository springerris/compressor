package com.github.springerris.op;

import com.github.springerris.i18n.I18N;
import com.github.springerris.i18n.Language;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

final class OverwriteFileDiskOperation extends WritingDiskOperation {

    private static final OpenOption[] OPEN_OPTIONS = new OpenOption[] {
            StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING
    };

    public OverwriteFileDiskOperation(Path file, Callable<InputStream> source) {
        super(file, source);
    }

    //


    @Override
    public Type type() {
        return Type.MODIFY;
    }

    @Override
    public String description(Language language) {
        return I18N.OP_OVERWRITE_FILE.get(language) + this.suffix();
    }

    @Override
    public void execute() throws IOException {
        // Must copy to a temp file first, since in this case we could be reading & writing from the same source
        Path tempFile = Files.createTempFile("decompress", null);
        try {
            this.execute0(tempFile);
        } finally {
            Files.delete(tempFile);
        }
    }

    private void execute0(Path tempFile) throws IOException {
        try (InputStream is = this.open();
             OutputStream os = Files.newOutputStream(tempFile, StandardOpenOption.WRITE)
        ) {
            this.pipe(is, os);
        }
        try (InputStream is = Files.newInputStream(tempFile, StandardOpenOption.READ);
             OutputStream os = Files.newOutputStream(this.path, OPEN_OPTIONS)
        ) {
            this.pipe(is, os);
        }
    }

}
