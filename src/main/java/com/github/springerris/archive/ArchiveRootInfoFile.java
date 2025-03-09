package com.github.springerris.archive;

import com.github.springerris.util.tsv.TSVReader;
import com.github.springerris.util.tsv.TSVWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.*;

/**
 * Handles serializing & deserializing lists of ArchiverRootInfo.
 */
final class ArchiveRootInfoFile implements Iterable<ArchiveRootInfo> {

    private final List<ArchiveRootInfo> data = new ArrayList<>();

    //

    public @NotNull @Unmodifiable List<ArchiveRootInfo> getData() {
        return Collections.unmodifiableList(this.data);
    }

    public void setData(@NotNull Collection<ArchiveRootInfo> collection) {
        this.data.clear();
        this.data.addAll(collection);
    }

    //

    public void read(@NotNull InputStream is) throws IOException {
        Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
        TSVReader tr = new TSVReader(r, 2);
        this.read(tr);
    }

    public void read(@NotNull TSVReader r) throws IOException {
        r.readHeader();

        FileSystem fs = FileSystems.getDefault();
        String entry;
        String path;
        ArchiveRootInfo info;

        while (r.readRow() != -1) {
            entry = r.readValue("entry");
            path = r.readValue("path");

            info = new ArchiveRootInfo(fs.getPath(path), entry);
            this.data.add(info);
        }
    }

    //

    public void write(@NotNull OutputStream os) throws IOException {
        Writer w = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        TSVWriter tw = new TSVWriter(w);
        this.write(tw);
        tw.flush();
    }

    public void write(@NotNull TSVWriter w) throws IOException {
        w.writeRow("entry", "path");
        for (ArchiveRootInfo info : this.data) {
            w.writeRow(
                    info.entry(),
                    info.path().toAbsolutePath().toString()
            );
        }
    }

    //

    @Override
    public @NotNull Iterator<ArchiveRootInfo> iterator() {
        return Collections.unmodifiableList(this.data).iterator();
    }

}
