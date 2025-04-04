// Класс, реализующий контекст окна

package com.github.springerris.gui;

import com.github.springerris.archive.Archive;
import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.token.TokenStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

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
    private final TokenStore tokens;
    private Archive archive;
    private CompletableFuture<?> activeTask = null;
    private VFS remote = null;

    public WindowContext(@NotNull Logger logger) {
        this.logger = logger;
        this.tokens = new TokenStore(logger);
        this.archive = new Archive();
    }

    //

    public @NotNull Logger logger() {
        return this.logger;
    }

    public @NotNull TokenStore tokens() {
        return this.tokens;
    }

    /**
     * The archive being inspected/modified by the user.
     */
    public @NotNull Archive archive() {
        return this.archive;
    }

    /**
     * Replaces the active {@link #archive()} by loading a previously saved ZIP;
     * as specified by {@link Archive#read(Path, Supplier) Archive#read}.
     */
    public void loadArchive(
            @NotNull Path file,
            @NotNull Supplier<String> password
    ) throws IOException {
        this.archive = Archive.read(file, password);
    }

    /**
     * Sets the "active task". This is a resource which is expected to complete in the future,
     * by virtue of running concurrently on another thread. Currently used to notify
     * {@link com.github.springerris.gui.impl.AwaitingWindow AwaitingWindow} when the previous window's
     * action is complete.
     * @see #whenActiveTaskComplete(Runnable)
     */
    public void setActiveTask(@Nullable CompletableFuture<?> activeTask) {
        this.activeTask = activeTask;
    }

    /**
     * Runs the specified callback when the {@link #setActiveTask(CompletableFuture) active task}
     * is complete. If the active task is updated while a callback is pending, behavior is undefined.
     */
    public void whenActiveTaskComplete(@NotNull Runnable r) {
        if (this.activeTask == null) r.run();
        this.activeTask.whenComplete((Object result, Throwable err) -> {
            if (err != null) this.logger.log(Level.WARNING, "Deferred task raised an exception", err);
            r.run();
        });
    }

    /**
     * Gets the remote VFS previously stored by {@link #setRemote(VFS)}.
     */
    public @NotNull VFS getRemote() {
        if (this.remote == null) throw new IllegalStateException("Remote VFS was not set");
        return this.remote;
    }

    /**
     * Sets the VFS representing the remote server we are reading from/writing to.
     * Setting this is required before opening windows that deal with remote filesystems.
     */
    public void setRemote(@NotNull VFS remote) {
        this.remote = remote;
    }

}
