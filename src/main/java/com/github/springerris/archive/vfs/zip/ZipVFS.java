package com.github.springerris.archive.vfs.zip;

import com.github.springerris.archive.vfs.AbstractVFS;
import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import io.github.wasabithumb.magma4j.Magma;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipVFS extends AbstractVFS implements VFSEntity, ZipEntryProvider {

    private final File file;
    private final String prefix;
    private final byte[] key;
    private final ZipEntryProvider entryProvider;

    ZipVFS(File file, String prefix, byte[] key, ZipEntryProvider entryProvider) {
        this.file = file;
        this.prefix = prefix;
        this.key = key;
        this.entryProvider = entryProvider == null ? ZipEntryProvider.cached(this) : entryProvider;
    }

    public ZipVFS(File file, byte[] key) {
        this(file, "", key, null);
    }

    public ZipVFS(File file) {
        this(file, null);
    }

    //

    private ZipInputStream open() throws IOException {
        InputStream is = new FileInputStream(this.file);
        boolean close = true;
        try {
            if (this.key != null) {
                is = Magma.newInputStream(is, this.key);
                is = new BufferedInputStream(is);
            }
            is = new ZipInputStream(is);
            close = false;
            return (ZipInputStream) is;
        } finally {
            if (close) is.close();
        }
    }

    private ZipEntry getEntry(String name) throws IOException {
        for (ZipEntry ze : this.entryProvider.getEntries()) {
            if (this.entryMatches(ze, name)) return ze;
        }
        return null;
    }

    private boolean entryMatches(ZipEntry ze, String name) {
        String zeName = ze.getName();
        if (zeName.length() == name.length())
            return zeName.equals(name);
        if (zeName.length() == (name.length() + 1))
            return zeName.startsWith(name) && zeName.charAt(name.length()) == '/';
        return false;
    }

    //

    @Override
    public VFS sub(String name) {
        if (name.isEmpty()) return this;
        if (!name.endsWith("/")) name += "/";
        return new ZipVFS(this.file, this.prefix + name, this.key, this.entryProvider);
    }

    @Override
    public VFSEntity[] list() throws IOException {
        int capacity = 8;
        int len = 0;
        VFSEntity[] ret = new VFSEntity[capacity];
        Pattern p = Pattern.compile("^" + Pattern.quote(this.prefix) + "([^/]+)/?$");

        for (ZipEntry ze : this.entryProvider.getEntries()) {
            Matcher m = p.matcher(ze.getName());
            if (!m.matches()) continue;
            String name = m.group(1);

            if (len == capacity) {
                int newCapacity = (int) Math.ceil((capacity + 1) / 0.75d);
                VFSEntity[] cpy = new VFSEntity[newCapacity];
                System.arraycopy(ret, 0, cpy, 0, capacity);
                ret = cpy;
                capacity = newCapacity;
            }
            ret[len++] = new ZipVFSEntity(ze, name);
        }

        if (len < capacity) {
            VFSEntity[] cpy = new VFSEntity[len];
            System.arraycopy(ret, 0, cpy, 0, len);
            ret = cpy;
        }
        return ret;
    }

    @Override
    public VFSEntity stat(String name) {
        if (name.isEmpty()) return this;
        VFSEntity ret = this.stat0(name);
        if (ret == null) throw new AssertionError("Failed to stat \"" + name + "\"");
        return ret;
    }

    private VFSEntity stat0(String name) {
        ZipEntry ze;
        try {
            ze = this.getEntry(this.prefix + name);
            if (ze == null) return null;
        } catch (IOException ignored) {
            return null;
        }

        int whereSlash = name.lastIndexOf('/');
        if (whereSlash != 1) {
            if (whereSlash == (name.length() - 1)) {
                int whereSlash2 = name.lastIndexOf('/', whereSlash - 1);
                name = (whereSlash2 == -1) ?
                        name.substring(0, whereSlash) :
                        name.substring(whereSlash2 + 1, whereSlash);
            } else {
                name = name.substring(whereSlash + 1);
            }
        }

        return new ZipVFSEntity(ze, name);
    }

    @Override
    public boolean exists(String name) {
        try {
            return this.getEntry(this.prefix + name) != null;
        } catch (IOException ignored) {
            return false;
        }
    }

    @Override
    public InputStream read(String name) throws IOException {
        ZipInputStream zis = this.open();
        boolean close = true;
        try {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                if (this.entryMatches(ze, this.prefix + name)) break;
            }
            if (ze == null) throw new IOException("Entity \"" + this.prefix + name + "\" does not exist");
            close = false;
            return new FilterInputStream(zis) {
                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    } finally {
                        zis.close();
                    }
                }
            };
        } finally {
            if (close) zis.close();
        }
    }

    @Override
    public OutputStream write(String name) {
        throw new UnsupportedOperationException("Cannot write to ZipVFS");
    }

    @Override
    public void createDirectory(String name) {
        throw new UnsupportedOperationException("Cannot create directory in ZipVFS");
    }

    @Override
    public void mount(String localPath, String remotePath, VFS remote) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot mount in ZipVFS");
    }

    //

    @Override
    public String name() {
        String name = this.file.getName();
        int whereExt = name.lastIndexOf('.');
        if (whereExt == -1) return name;
        return name.substring(0, whereExt);
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public long size() {
        return 0L;
    }

    //

    /** INTERNAL USAGE ONLY */
    @Override
    public Collection<ZipEntry> getEntries() throws IOException {
        List<ZipEntry> ret = new LinkedList<>();
        try (ZipInputStream zis = this.open()) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                ret.add(ze);
            }
        }
        return ret;
    }

}
