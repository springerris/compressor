package com.github.springerris.op;

import java.io.IOException;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiskOperationQueue extends AbstractQueue<DiskOperation> {

    private final Queue<DiskOperation> backing;
    public DiskOperationQueue(Queue<DiskOperation> backing) {
        this.backing = backing;
    }

    public DiskOperationQueue() {
        this(new LinkedList<>());
    }

    //

    @Override
    public Iterator<DiskOperation> iterator() {
        return this.backing.iterator();
    }

    @Override
    public int size() {
        return this.backing.size();
    }

    @Override
    public boolean offer(DiskOperation diskOperation) {
        return this.backing.offer(diskOperation);
    }

    @Override
    public DiskOperation poll() {
        return this.backing.poll();
    }

    @Override
    public DiskOperation peek() {
        return this.backing.peek();
    }

    //

    public boolean execute(Logger logger) {
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
