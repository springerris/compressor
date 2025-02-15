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
final class CreateDirectoryDiskOperation extends AbstractDiskOperation {

    public CreateDirectoryDiskOperation(@NotNull Path dir) {
        super(dir);
    }

    //


    @Override
    public @NotNull Type type() {
        return Type.CREATE;
    }

    @Override
    public @NotNull String description(@NotNull Language language) {
        return I18N.OP_CREATE_DIR.get(language) + this.suffix();
    }

    @Override
    public void execute() throws IOException {
        Files.createDirectories(this.path);
    }

}
