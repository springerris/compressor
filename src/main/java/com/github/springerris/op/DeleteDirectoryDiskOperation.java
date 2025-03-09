package com.github.springerris.op;

import com.github.springerris.i18n.I18N;
import com.github.springerris.i18n.Language;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * @see DiskOperation
 */
@ApiStatus.Internal
final class DeleteDirectoryDiskOperation extends AbstractDiskOperation {

    public DeleteDirectoryDiskOperation(@NotNull Path dir) {
        super(dir);
    }

    //


    @Override
    public @NotNull Type type() {
        return Type.DELETE;
    }

    @Override
    public @NotNull String description(@NotNull Language language) {
        return I18N.OP_DELETE_DIR.get(language) + this.suffix();
    }

    @Override
    public void execute() throws IOException {
        this.deleteRecursively(this.path, true);
    }

    private void deleteRecursively(Path path, boolean isDir) throws IOException {
        if (isDir) {
            try (Stream<Path> sub = Files.list(path)) {
                Iterator<Path> iter = sub.iterator();
                Path next;
                while (iter.hasNext()) {
                    next = iter.next();
                    this.deleteRecursively(next, Files.isDirectory(next));
                }
            }
        }
        Files.delete(path);
    }

}
