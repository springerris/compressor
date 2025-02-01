package com.github.springerris.archive.vfs.zip;

import java.io.IOException;
import java.util.Collection;
import java.util.zip.ZipEntry;

interface ZipEntryProvider {

    static ZipEntryProvider cached(ZipEntryProvider other) {
        if (other instanceof Cached cached) return cached;
        return new Cached(other);
    }

    //

    Collection<ZipEntry> getEntries() throws IOException;

    //

    final class Cached implements ZipEntryProvider {

        private final ZipEntryProvider backing;
        private Collection<ZipEntry> value;

        Cached(ZipEntryProvider backing) {
            this.backing = backing;
            this.value = null;
        }

        @Override
        public synchronized Collection<ZipEntry> getEntries() throws IOException {
            if (this.value != null) return this.value;
            return this.value = this.backing.getEntries();
        }

    }

}
