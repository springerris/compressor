// Класс, реализующий контекст окна

package com.github.springerris.gui;

import com.github.springerris.archive.Archive;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Все параметры между разными {@link Window}
 */
public class WindowContext {

    private final Logger logger;
    private Archive archive;
    private CompletableFuture<?> activeTask = null;

    public WindowContext(Logger logger, Archive archive) {
        this.logger = logger;
        this.archive = archive;
    }

    //

    public Logger logger() {
        return this.logger;
    }

    public Archive archive() {
        return this.archive;
    }

    public void loadArchive(Path file, Supplier<String> password) throws IOException {
        this.archive = Archive.read(file, password);
    }

    public void setActiveTask(CompletableFuture<?> activeTask) {
        this.activeTask = activeTask;
    }

    public void whenActiveTaskComplete(Runnable r) {
        if (this.activeTask == null) r.run();
        this.activeTask.whenComplete((Object result, Throwable err) -> {
            if (err != null) this.logger.log(Level.WARNING, "Deferred task raised an exception", err);
            r.run();
        });
    }

}
