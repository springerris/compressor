package com.github.springerris.archive.vfs.zip;

import com.github.springerris.archive.vfs.AbstractVFS;
import com.github.springerris.archive.vfs.VFS;
import com.github.springerris.archive.vfs.VFSEntity;
import io.github.wasabithumb.magma4j.Magma;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipVFS extends AbstractVFS implements VFSEntity {

    private final File file;
    private final String prefix;
    private final byte[] key;

    ZipVFS(File file, String prefix, byte[] key) {
        this.file = file;
        this.prefix = prefix;
        this.key = key;
    }

    public ZipVFS(File file, byte[] key) {
        this(file, "", key);
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

    private ZipEntry getEntry(ZipInputStream zis, String name) throws IOException {
        int len = name.length();
        ZipEntry ze;
        String zeName;
        int zeNameLen;

        while ((ze = zis.getNextEntry()) != null) {
            zeName = ze.getName();
            zeNameLen = zeName.length();
            if (zeNameLen == len) {
                if (zeName.equals(name)) return ze;
            } else if (zeNameLen == (len + 1)) {
                if (zeName.startsWith(name) && zeName.charAt(zeNameLen - 1) == '/') return ze;
            }
        }
        return null;
    }

    //

    @Override
    public VFS sub(String name) {
        if (name.isEmpty()) return this;
        return new ZipVFS(this.file, this.prefix + name + "/", this.key);
    }

    @Override
    public VFSEntity[] list() throws IOException {
        int capacity = 8;
        int len = 0;
        VFSEntity[] ret = new VFSEntity[capacity];
        Pattern p = Pattern.compile("^" + Pattern.quote(this.prefix) + "([^/]+)/?$");

        try (ZipInputStream zis = this.open()) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
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
        try (ZipInputStream zis = this.open()) {
            ze = this.getEntry(zis, this.prefix + name);
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
        try (ZipInputStream zis = this.open()) {
            return this.getEntry(zis, this.prefix + name) != null;
        } catch (IOException ignored) {
            return false;
        }
    }

    @Override
    public InputStream read(String name) throws IOException {
        ZipInputStream zis = this.open();
        boolean close = true;
        try {
            ZipEntry ze = this.getEntry(zis, this.prefix + name);
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

}
