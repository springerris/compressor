package com.github.springerris.op;

import com.github.springerris.i18n.I18N;
import com.github.springerris.i18n.Language;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class DeleteFileDiskOperation extends AbstractDiskOperation {

    public DeleteFileDiskOperation(Path file) {
        super(file);
    }

    //


    @Override
    public Type type() {
        return Type.DELETE;
    }

    @Override
    public String description(Language language) {
        return I18N.OP_DELETE_FILE.get(language) + this.suffix();
    }

    @Override
    public void execute() throws IOException {
        Files.delete(this.path);
    }

}
