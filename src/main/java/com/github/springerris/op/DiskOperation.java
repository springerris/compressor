package com.github.springerris.op;

import com.github.springerris.i18n.I18N;
import com.github.springerris.i18n.Language;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * Describes an action that can be performed on the filesystem.
 * This allows operation queues to be summarized to the user and confirmed before being executed.
 */
public interface DiskOperation {

    static DiskOperation writeNewFile(Path file, Callable<InputStream> source) {
        return new WriteNewFileDiskOperation(file, source);
    }

    static DiskOperation overwriteFile(Path file, Callable<InputStream> source) {
        return new OverwriteFileDiskOperation(file, source);
    }

    static DiskOperation deleteFile(Path file) {
        return new DeleteFileDiskOperation(file);
    }

    static DiskOperation createDirectory(Path dir) {
        return new CreateDirectoryDiskOperation(dir);
    }

    static DiskOperation deleteDirectory(Path dir) {
        return new DeleteDirectoryDiskOperation(dir);
    }

    //

    Type type();

    String description(Language language);

    default String description() {
        return this.description(I18N.LANGUAGE);
    }

    void execute() throws IOException;

    //

    /** Roughly communicates the nature of the operation */
    enum Type {
        CREATE,
        MODIFY,
        DELETE
    }

}
