package com.github.springerris.op;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link Queue queue} of {@link DiskOperation}s.
 * @see #execute(Logger)
 */
public class DiskOperationQueue extends AbstractQueue<DiskOperation> {

    private final Queue<DiskOperation> backing;
    public DiskOperationQueue(@NotNull Queue<DiskOperation> backing) {
        this.backing = backing;
    }

    public DiskOperationQueue() {
        this(new LinkedList<>());
    }

    //

    @Override
    public @NotNull Iterator<DiskOperation> iterator() {
        return this.backing.iterator();
    }

    @Override
    public int size() {
        return this.backing.size();
    }

    @Override
    public boolean offer(@NotNull DiskOperation diskOperation) {
        return this.backing.offer(diskOperation);
    }

    @Override
    public @Nullable DiskOperation poll() {
        return this.backing.poll();
    }

    @Override
    public @Nullable DiskOperation peek() {
        return this.backing.peek();
    }

    //

    /**
     * Drains the queue, executing all of its operations in turn.
     * @param logger Logger to receive debug messages & warnings
     * @return True if all operations executed successfully
     */
    public boolean execute(@NotNull Logger logger) {
        DiskOperation op;
        while ((op = this.poll()) != null) {
            logger.log(Level.FINE, "Executing disk operation: " + op.description());
            try {
                op.execute();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to execute operation \"" + op.description() + "\"", e);
                return false;
            }
        }
        return true;
    }

}
