package com.github.springerris.op;

import com.github.springerris.i18n.I18N;
import com.github.springerris.i18n.Language;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * Describes an action that can be performed on the filesystem.
 * This allows {@link DiskOperationQueue operation queues} to be summarized to the user and confirmed before
 * being executed.
 */
public interface DiskOperation {

    /**
     * Creates a new DiskOperation which will write content to a new file.
     * @param file Path to the new file
     * @param source Called when the data to write is needed
     */
    static DiskOperation writeNewFile(Path file, Callable<InputStream> source) {
        return new WriteNewFileDiskOperation(file, source);
    }

    /**
     * Creates a new DiskOperation which will overwrite an existing file.
     * @param file Path to an existing file
     * @param source Called when the data to write is needed
     */
    static DiskOperation overwriteFile(Path file, Callable<InputStream> source) {
        return new OverwriteFileDiskOperation(file, source);
    }

    /**
     * Creates a new DiskOperation which will delete an existing file.
     * @param file Path to the file to delete
     */
    static DiskOperation deleteFile(Path file) {
        return new DeleteFileDiskOperation(file);
    }

    /**
     * Creates a new DiskOperation which will create a new directory.
     * @param dir Path to the directory to create
     */
    static DiskOperation createDirectory(Path dir) {
        return new CreateDirectoryDiskOperation(dir);
    }

    /**
     * Creates a new DiskOperation which will recursively delete an existing directory.
     * @param dir Path to the directory to delete
     */
    static DiskOperation deleteDirectory(Path dir) {
        return new DeleteDirectoryDiskOperation(dir);
    }

    //

    /**
     * Describes the effect of the operation; one of
     * {@link Type#CREATE CREATE}, {@link Type#MODIFY MODIFY} or {@link Type#DELETE DELETE}.
     */
    Type type();

    /**
     * Describes the operation in the specified language.
     * @see #description()
     */
    String description(Language language);

    /**
     * Describes the operation in the {@link I18N#LANGUAGE default language}.
     * @see #description(Language)
     */
    default String description() {
        return this.description(I18N.LANGUAGE);
    }

    /**
     * Executes the operation, enacting its effect on the filesystem.
     */
    void execute() throws IOException;

    //

    /** Roughly communicates the nature of the operation */
    enum Type {
        /** The operation will create entities that did not previously exist */
        CREATE,

        /** The operation will modify entities that already exist */
        MODIFY,

        /** The operation will delete entities that already exist */
        DELETE
    }

}
