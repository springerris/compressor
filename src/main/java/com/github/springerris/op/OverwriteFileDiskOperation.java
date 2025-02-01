package com.github.springerris.op;

import com.github.springerris.i18n.I18N;
import com.github.springerris.i18n.Language;

import java.io.InputStream;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

final class OverwriteFileDiskOperation extends WriteFileDiskOperation {

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
    protected OpenOption[] openOptions() {
        return new OpenOption[] {
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
        };
    }

}
