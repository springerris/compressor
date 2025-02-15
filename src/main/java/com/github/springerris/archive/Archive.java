package com.github.springerris.archive;

import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import com.github.springerris.op.DiskOperation;
import com.github.springerris.op.DiskOperationQueue;
import io.github.wasabithumb.magma4j.Magma;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Handles compressing, encrypting, decrypting and decompressing path sets.
 */
public final class Archive {

    /**
     * Takes in a file previously created by an Archiver and restores it.
     *
     * @param file     A zip file or encrypted zip file
     * @param password A method which is called when the file is encrypted and must be decrypted
     * @throws IOException              Generic IO exception
     * @throws IllegalArgumentException File is malformed (missing metadata)
     * @throws IllegalStateException    Password is incorrect
     */
    @Contract("_, _ -> new")
    public static @NotNull Archive read(
            @NotNull Path file,
            @NotNull Supplier<String> password
    ) throws IOException {
        byte[] key;
        boolean isZip;
        try (InputStream is = Files.newInputStream(file, StandardOpenOption.READ)) {
            isZip = streamIsZip(is);
        }

        if (isZip) {
            key = null;
        } else {
            key = Magma.generateKeyFromPassword(password.get());
            try (InputStream is = Files.newInputStream(file, StandardOpenOption.READ);
                 InputStream mis = Magma.newInputStream(is, key)
            ) {
                isZip = streamIsZip(mis);
            }
            if (!isZip) throw new IllegalStateException("Encryption password is incorrect");
        }

        Archive ret = new Archive();
        VFS zipVFS = VFS.zip(file, key);
        if (!zipVFS.exists(".ROOTS")) throw new IllegalArgumentException("Archive is missing metadata");

        ArchiveRootInfoFile roots = new ArchiveRootInfoFile();
        try (InputStream is = zipVFS.read(".ROOTS")) {
            roots.read(is);
        }
        ret.roots.addAll(roots.getData());

        for (ArchiveRootInfo root : roots) {
            ret.files.mount(
                    root.entry(),
                    root.entry(),
                    zipVFS
            );
        }
        return ret;
    }

    private static final byte[] ZIP_HEADER = new byte[]{
            (byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04
    };

    private static boolean streamIsZip(@NotNull InputStream is) throws IOException {
        int c;
        for (byte b : ZIP_HEADER) {
            c = is.read();
            if (c == -1) return false;
            if (((byte) c) != b) return false;
        }
        return true;
    }

    //

    private final VFS files;
    private final SortedSet<ArchiveRootInfo> roots;

    public Archive() {
        this.files = VFS.empty();
        this.roots = new TreeSet<>();
    }

    //

    public @NotNull VFS files() {
        return this.files;
    }

    /**
     * True if the path or any of its parents are not already contained within the root tree
     */
    @Contract("null -> false")
    public boolean canAdd(Path path) {
        if (path == null) return false;

        ArchiveRootInfo info = new ArchiveRootInfo(path);
        if (this.roots.contains(info)) return false;

        Set<ArchiveRootInfo> less = this.roots.headSet(info);
        if (less.isEmpty()) return true;

        String a = info.entry();
        for (ArchiveRootInfo possibleParent : less) {
            String b = possibleParent.entry();
            boolean conflicts = possibleParent.isDirectory() ?
                    a.startsWith(b) :
                    a.equals(b);
            if (conflicts) return false;
        }

        return true;
    }

    /**
     * Adds a filesystem path to include in the archive
     */
    @Contract("null -> fail")
    public void add(Path path) {
        if (!this.canAdd(path))
            throw new IllegalArgumentException("Cannot add path \"" + path.toAbsolutePath() + "\", violates hierarchy");

        ArchiveRootInfo info = new ArchiveRootInfo(path);
        if (info.isDirectory()) {
            this.files.mount(info.entry(), "", VFS.directory(path));
        } else {
            this.files.mount(info.entry(), path.getFileName().toString(), VFS.directory(path.getParent()));
        }

        this.roots.add(info);
    }

    /**
     * Writes the archive data to the specified stream.
     *
     * @param os       Destination for the archive data
     * @param password If not null, data will be encrypted with this password
     */
    public void write(@NotNull OutputStream os, @Nullable String password) throws IOException {
        boolean close = false;
        if (password != null) {
            byte[] key = Magma.generateKeyFromPassword(password);
            os = Magma.newOutputStream(os, key);
            close = true;
        }
        try {
            try (ZipOutputStream zos = new ZipOutputStream(os)) {
                this.write(zos);
                zos.flush();
            }
        } finally {
            if (close) os.close();
        }
    }

    private void write(ZipOutputStream zos) throws IOException {
        this.writeMetadata(zos);
        this.writeTree(zos, this.files, "");
    }

    private void writeMetadata(ZipOutputStream zos) throws IOException {
        ZipEntry ze = new ZipEntry(".ROOTS");
        zos.putNextEntry(ze);

        ArchiveRootInfoFile file = new ArchiveRootInfoFile();
        file.setData(this.roots);
        file.write(zos);

        zos.closeEntry();
    }

    private void writeTree(ZipOutputStream zos, VFS root, String prefix) throws IOException {
        StringBuilder name = new StringBuilder(prefix);

        for (VFSEntity ent : root.list()) {
            name.setLength(prefix.length());
            name.append(ent.name());

            if (ent.isDirectory()) {
                name.append('/');
                String nameS = name.toString();

                ZipEntry ze = new ZipEntry(nameS);
                ze.setMethod(ZipEntry.STORED);
                ze.setSize(0L);
                ze.setCrc(0L);
                zos.putNextEntry(ze);

                this.writeTree(zos, root.sub(ent.name()), nameS);
                continue;
            } else if (!ent.isFile()) {
                continue;
            }

            ZipEntry ze = new ZipEntry(name.toString());
            zos.putNextEntry(ze);

            try (InputStream is = root.read(ent.name())) {
                byte[] buf = new byte[8192];
                int read;
                while ((read = is.read(buf)) != -1) {
                    zos.write(buf, 0, read);
                }
            }
        }
    }

    /**
     * Prepares an operation queue that, when executed, would extract the archive.
     */
    public @NotNull DiskOperationQueue extract() throws IOException {
        DiskOperationQueue ret = new DiskOperationQueue();

        for (ArchiveRootInfo root : this.roots) {
            String entry = root.entry();
            Path path = root.path();
            if (root.isDirectory()) {
                this.extractDir(ret, path, entry);
                continue;
            }
            this.extractOrphan(ret, path, entry);
        }

        return ret;
    }

    private void extractOrphan(DiskOperationQueue queue, Path path, String entry) {
        Path parent = path.getParent();
        if (parent != null && !Files.isDirectory(parent)) {
            if (Files.exists(parent)) queue.add(DiskOperation.deleteFile(parent));
            queue.add(DiskOperation.createDirectory(parent));
        }
        this.extractFile(queue, path, entry);
    }

    private void extractFile(DiskOperationQueue queue, Path path, String entry) {
        Callable<InputStream> procureStream = () -> this.files.read(entry);
        if (Files.exists(path)) {
            if (Files.isRegularFile(path)) {
                queue.add(DiskOperation.overwriteFile(path, procureStream));
                return;
            }
            queue.add(DiskOperation.deleteFile(path));
        }
        queue.add(DiskOperation.writeNewFile(path, procureStream));
    }

    private void extractDir(DiskOperationQueue queue, Path path, String prefix) throws IOException {
        boolean wasRegularDir = false;
        Set<String> old = new HashSet<>(0);

        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                wasRegularDir = true;
            } else {
                queue.add(DiskOperation.deleteFile(path));
            }
        }

        if (wasRegularDir) {
            try (Stream<Path> stream = Files.list(path)) {
                Iterator<Path> iterator = stream.iterator();
                Path next;
                while (iterator.hasNext()) {
                    next = iterator.next();
                    old.add(next.getFileName().toString());
                }
            }
        } else {
            queue.add(DiskOperation.createDirectory(path));
        }

        for (VFSEntity ent : this.files.sub(prefix).list()) {
            old.remove(ent.name());

            if (ent.isFile()) {
                this.extractFile(queue, path.resolve(ent.name()), prefix + ent.name());
            } else if (ent.isDirectory()) {
                this.extractDir(queue, path.resolve(ent.name()), prefix + ent.name() + "/");
            }
        }

        for (String oldName : old) {
            Path p = path.resolve(oldName);

            if (Files.isDirectory(p)) {
                queue.add(DiskOperation.deleteDirectory(p));
            } else {
                queue.add(DiskOperation.deleteFile(p));
            }
        }
    }

}
