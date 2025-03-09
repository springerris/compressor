package com.github.springerris.op;

import com.github.springerris.i18n.I18N;
import com.github.springerris.i18n.Language;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

/**
 * @see DiskOperation
 */
@ApiStatus.Internal
final class WriteNewFileDiskOperation extends WritingDiskOperation {

    private static final OpenOption[] OPEN_OPTIONS = new OpenOption[] {
            StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW
    };

    //

    public WriteNewFileDiskOperation(@NotNull Path file, @NotNull Callable<InputStream> source) {
        super(file, source);
    }

    //

    @Override
    public @NotNull Type type() {
        return Type.CREATE;
    }

    @Override
    public @NotNull String description(@NotNull Language language) {
        return I18N.OP_WRITE_NEW_FILE.get(language) + this.suffix();
    }

    @Override
    public void execute() throws IOException {
        try (InputStream is = this.open();
             OutputStream os = Files.newOutputStream(this.path, OPEN_OPTIONS)
        ) {
            this.pipe(is, os);
        }
    }

}
