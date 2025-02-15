package com.github.springerris.op;

import com.github.springerris.i18n.I18N;
import com.github.springerris.i18n.Language;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @see DiskOperation
 */
@ApiStatus.Internal
final class DeleteFileDiskOperation extends AbstractDiskOperation {

    public DeleteFileDiskOperation(@NotNull Path file) {
        super(file);
    }

    //


    @Override
    public @NotNull Type type() {
        return Type.DELETE;
    }

    @Override
    public @NotNull String description(@NotNull Language language) {
        return I18N.OP_DELETE_FILE.get(language) + this.suffix();
    }

    @Override
    public void execute() throws IOException {
        Files.delete(this.path);
    }

}
