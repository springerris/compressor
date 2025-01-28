package com.github.springerris.util;

import io.github.wasabithumb.magma4j.Magma;
import io.github.wasabithumb.magma4j.io.stream.MagmaOutputStream;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

    private final Set<Path> selected;
    private final boolean includeMetadata;

    /**
     * @param includeMetadata If true, an extra file ".ROOTS" will be written to the zip stream containing
     *                        the selected filesystem paths (and their corresponding zip entries) in TSV format.
     */
    public Zipper(boolean includeMetadata) {
        this.selected = new HashSet<>();
        this.includeMetadata = includeMetadata;
    }

    public Zipper() {
        this(false);
    }

    //

    public boolean includes(File file) {
        return this.includes(file.toPath());
    }

    public boolean includes(Path path) {
        return this.selected.contains(path) || this.anyParentSelected(path);
    }

    public Set<Path> getSelected() {
        return Collections.unmodifiableSet(this.selected);
    }

    //

    public boolean add(File file) {
        return this.add(file.toPath());
    }

    public boolean add(Path path) throws IllegalArgumentException {
        path = path.toAbsolutePath();
        if (!Files.isDirectory(path))
            throw new IllegalArgumentException("Path \"" + path + "\" is not a directory");

        if (this.anyParentSelected(path)) return false;
        return this.selected.add(path);
    }


    public void write(File f) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(f, false);
             ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            this.write(zos);
            zos.flush();
        }
    }

    public void write(ZipOutputStream os) throws IOException {
        MagmaOutputStream mos = null;
        Set<String> created = new HashSet<>();
        StringBuilder meta = null;
        if (this.includeMetadata) {
            meta = new StringBuilder();
            meta.append("entry\tpath\n");
        }


        for (Path p : this.selected) {
            String prefix = this.buildPrefix(os, p, created);


            this.traverse(os, p, prefix);
            if (this.includeMetadata) {
                meta.append(prefix)
                        .append('\t')
                        .append(p)
                        .append('\n');
            }
        }

        if (this.includeMetadata) {
            byte[] data = meta.toString().getBytes(StandardCharsets.UTF_8);
            os.putNextEntry(new ZipEntry(".ROOTS"));
            os.write(data);
        }
    }

    //

    /**
     * Traverses the given directory, adding its contents to the given zip stream.
     * The prefix should be canon at this point.
     */
    private void traverse(ZipOutputStream os, Path path, String prefix) throws IOException {
        File f = path.toFile();
        File[] list = f.listFiles();
        if (list == null)
            throw new IOException("Unable to list path \"" + path + "\"");

        for (File sub : list) {
            if (sub.isDirectory()) {
                String name = prefix + sub.getName() + "/";
                os.putNextEntry(this.createDirEntry(name));
                this.traverse(os, path.resolve(sub.getName()), name);
            } else if (sub.isFile()) {
                os.putNextEntry(new ZipEntry(prefix + sub.getName()));
                this.copyFile(os, sub);
            }
        }
    }

    /**
     * Pipes the content of the given file into the given zip stream.
     */
    private void copyFile(ZipOutputStream os, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buf = new byte[8192];
            int read;
            while ((read = fis.read(buf, 0, 8192)) != -1) {
                os.write(buf, 0, read);
            }
        }
    }

    /**
     * Returns a string which represents the prefix to prepend to all children of the given directory,
     * while creating all parent directories within the zip file.
     */
    private String buildPrefix(ZipOutputStream os, Path path, Set<String> created) throws IOException {
        String root = this.buildRootPrefix(path);
        if (!root.isEmpty() && !created.add(root)) {
            os.putNextEntry(this.createDirEntry(root));
        }

        int count = path.getNameCount();
        if (count == 0) return root;

        StringBuilder sb = new StringBuilder(root);
        String s = null;
        for (int i=0; i < count; i++) {
            sb.append(path.getName(i))
                    .append('/');

            s = sb.toString();
            if (!created.add(s)) continue;

            os.putNextEntry(this.createDirEntry(s));
        }

        return s;
    }

    /**
     * Tends to return an empty string, except on Windows where it will return X:/
     */
    private String buildRootPrefix(Path p) {
        Path root = p.getRoot();
        if (root == null) return "";

        String str = root.toString();
        if (str.length() < 2) return "";

        return str.substring(0, str.length() - 1) + "/";
    }

    /**
     * Returns true if any parent of the given path is selected.
     */
    private boolean anyParentSelected(Path path) {
        Path parent = path.getParent();
        while (parent != null) {
            if (this.selected.contains(parent))
                return true;
            parent = parent.getParent();
        }
        return false;
    }

    private ZipEntry createDirEntry(String name) {
        ZipEntry ze = new ZipEntry(name);
        ze.setSize(0L);
        ze.setCrc(0L);
        ze.setMethod(ZipEntry.STORED);
        return ze;
    }

    public void printFiles() {
        for(Path p : this.selected) {
            System.out.println(p.getFileName());
        }
    }

}
