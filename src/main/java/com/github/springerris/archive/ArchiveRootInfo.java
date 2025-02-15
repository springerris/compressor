package com.github.springerris.archive;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;

/** A combination of paths (the path in the archive and the path on the filesystem) */
final class ArchiveRootInfo implements Comparable<ArchiveRootInfo> {

    private final Path path;
    private transient String entry;

    public ArchiveRootInfo(@NotNull Path path) {
        this(path, null);
    }

    public ArchiveRootInfo(@NotNull Path path, @Nullable String entry) {
        this.path = path;
        this.entry = entry;
    }

    //

    public @NotNull Path path() {
        return this.path;
    }

    public synchronized @NotNull String entry() {
        String entry = this.entry;
        if (entry == null) {
            entry = this.computeEntry();
            this.entry = entry;
        }
        return entry;
    }

    private String computeEntry() {
        StringBuilder sb = new StringBuilder(this.computeRootEntry());

        int count = this.path.getNameCount();
        for (int i=0; i < count; i++) {
            sb.append('/');
            sb.append(this.path.getName(i));
        }

        if (Files.isDirectory(this.path)) sb.append('/');
        return sb.toString();
    }

    private String computeRootEntry() {
        Path root = this.path.getRoot();
        if (root == null) return "root";

        String str = root.toString();
        if (str.length() < 2) return "root";

        return str.substring(0, str.length() - 1);
    }

    public boolean isDirectory() {
        String entry = this.entry();
        return entry.charAt(entry.length() - 1) == '/';
    }

    //

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof ArchiveRootInfo other) {
            return this.path.equals(other.path);
        }
        return super.equals(obj);
    }

    @Override
    public int compareTo(@NotNull ArchiveRootInfo rootInfo) {
        return CharSequence.compare(this.entry(), rootInfo.entry());
    }

}
