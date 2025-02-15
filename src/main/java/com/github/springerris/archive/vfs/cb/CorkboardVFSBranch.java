package com.github.springerris.archive.vfs.cb;

import com.github.springerris.archive.vfs.VFSEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents either a {@link CorkboardVFSLink} (link) or a {@link CorkboardVFS} (dir).
 */
sealed abstract class CorkboardVFSBranch {

    @Contract("_ -> new")
    static @NotNull CorkboardVFSBranch of(@NotNull CorkboardVFSLink link) {
        return new Link(link);
    }

    @Contract("_ -> new")
    static @NotNull CorkboardVFSBranch of(@NotNull CorkboardVFS dir) {
        return new Dir(dir);
    }

    //

    abstract VFSEntity handle();

    abstract boolean isLink();

    abstract boolean isDir();

    abstract CorkboardVFSLink asLink() throws UnsupportedOperationException;

    abstract CorkboardVFS asDir() throws UnsupportedOperationException;

    //

    static final class Link extends CorkboardVFSBranch {

        private final CorkboardVFSLink handle;
        Link(CorkboardVFSLink handle) {
            this.handle = handle;
        }

        //

        @Override
        VFSEntity handle() {
            return this.handle;
        }

        @Override
        boolean isLink() {
            return true;
        }

        @Override
        boolean isDir() {
            return false;
        }

        @Override
        CorkboardVFSLink asLink() {
            return this.handle;
        }

        @Override
        CorkboardVFS asDir() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Cannot get link as dir");
        }

    }

    //

    static final class Dir extends CorkboardVFSBranch {

        private final CorkboardVFS handle;
        Dir(CorkboardVFS handle) {
            this.handle = handle;
        }

        @Override
        VFSEntity handle() {
            return this.handle;
        }

        @Override
        boolean isLink() {
            return false;
        }

        @Override
        boolean isDir() {
            return true;
        }

        @Override
        CorkboardVFSLink asLink() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Cannot get dir as link");
        }

        @Override
        CorkboardVFS asDir() {
            return this.handle;
        }

    }

}
