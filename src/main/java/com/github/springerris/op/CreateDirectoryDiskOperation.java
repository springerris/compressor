package com.github.springerris.op;

import com.github.springerris.i18n.I18N;
import com.github.springerris.i18n.Language;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @see DiskOperation
 */
final class CreateDirectoryDiskOperation extends AbstractDiskOperation {

    public CreateDirectoryDiskOperation(Path dir) {
        super(dir);
    }

    //


    @Override
    public Type type() {
        return Type.CREATE;
    }

    @Override
    public String description(Language language) {
        return I18N.OP_CREATE_DIR.get(language) + this.suffix();
    }

    @Override
    public void execute() throws IOException {
        Files.createDirectories(this.path);
    }

}
